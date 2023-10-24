package com.example.storageandroid.presentation.files_screen

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private val TAG = "FilesViewModel"

class FilesViewModel(private val application: Application) : ViewModel() {
    var fileListFlow = MutableStateFlow<Array<File>>(arrayOf())
    var docListFlow = MutableStateFlow<Array<DocumentFile>>(arrayOf())

    init {
        viewModelScope.launch {
            fetchFileList(Environment.getExternalStorageDirectory().absolutePath)
        }
    }


    suspend fun fetchFileList(path: String) {
        var file = File(path)
        Log.i(TAG, "absolute path: ${file.absolutePath}")
        val fileList = file.listFiles()
        if (fileList.isNullOrEmpty()) {
            val folderName = file.absolutePath.substringAfterLast("/")
            if(folderName.equals("data")){
                listOfFolderInside("data")
            }
            if(folderName.equals("obb")){
                listOfFolderInside("obb")
            }
        } else {
            fileListFlow.emit(fileList)
        }
    }

    suspend fun fetchDocFiles(uri: Uri) {
        Log.i("fetchDocFile", "uri: $uri")

//        docFile.listFiles()
//        if (docFile != null) {
//            fileListFlow.emit(arrayOf())
//            docListFlow.emit(docFile.listFiles())
//        }
    }

    suspend fun listOfFolderInside(folder: String) {
        withContext(Dispatchers.IO) {
            val dirList: MutableList<DocumentFile> = mutableListOf()
            val pm = application.packageManager
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            for (app in pm.queryIntentActivities(intent, 0)) {
                val fileDir = File(
                    Environment.getExternalStorageDirectory().absolutePath,
                    "/Android/$folder/${app.activityInfo.packageName}"
                )
                if (fileDir.exists()) {
                    Log.i("dataitems", "exit path: $fileDir")
                    val docfile = DocumentFile.fromFile(fileDir)
                    Log.i("dataitems", "doc path: ${docfile.uri}")
                    dirList.add(docfile)
                }
            }
            docListFlow.emit(dirList.toTypedArray())
            fileListFlow.emit(arrayOf())
        }
    }
}

class FilesViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FilesViewModel(application) as T
    }
}