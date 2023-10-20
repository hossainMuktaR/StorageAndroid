package com.example.storageandroid.presentation.images_screen

import android.app.Application
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.IntentSender
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImagesViewModel(private val application: Application) : ViewModel() {
    var imageListFlow = MutableStateFlow<List<Image>>(emptyList())
    lateinit var contentObserver: ContentObserver

    init {
        viewModelScope.launch {
            fetchAllImages()
        }
        initContentObserver()
        Log.i("imagesViewModel", "viewModel Init")
    }

    suspend fun fetchAllImages() {
        withContext(Dispatchers.IO) {
            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_TAKEN
            )
            val selection = null
            val selectionArgs = null
            val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
            val imageList = mutableListOf<Image>()
            application.contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val size = cursor.getInt(sizeColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    imageList.add(Image(contentUri, name, size))
                }
                imageListFlow.emit(imageList)
            }
        }
    }

    fun detetePhoto(photoUri: Uri): IntentSenderRequest? {
        return try {
                application.contentResolver.delete(photoUri, null, null)
                return null
            } catch (e: SecurityException) {
                val intentSender = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                        MediaStore.createDeleteRequest(
                            application.contentResolver,
                            listOf(photoUri)
                        ).intentSender
                    }

                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                        val recoverableSecurityException =
                            e as? RecoverableSecurityException ?: throw RuntimeException(
                                e.message,
                                e
                            )
                        recoverableSecurityException.userAction.actionIntent.intentSender
                    }

                    else -> {
                        null
                    }
                }
                return intentSender?.let { IntentSenderRequest.Builder(it).build() }
            }

    }

    fun initContentObserver() {
        contentObserver = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                Log.i("imagesViewModel", "content observer called")
                viewModelScope.launch {
                    fetchAllImages()
                }
            }
        }
        application.baseContext.contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            contentObserver
        )
    }

    override fun onCleared() {
        super.onCleared()
        application.baseContext.contentResolver.unregisterContentObserver(contentObserver)
        Log.i("imagesViewModel", "Viewmodel cleared")
    }
}

class ImagesViewModelFactory(val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ImagesViewModel(application) as T
    }

}

data class Image(
    val uri: Uri,
    val name: String,
    val size: Int,

    )
