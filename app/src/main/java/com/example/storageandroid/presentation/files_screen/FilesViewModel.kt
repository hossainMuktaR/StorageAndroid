package com.example.storageandroid.presentation.files_screen

import android.app.Application
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private val TAG = "FilesViewModel"

class FilesViewModel(private val application: Application) : ViewModel() {
    var fileListFlow = MutableStateFlow<Array<File>>(arrayOf())

    init {
        viewModelScope.launch {
            fetchFileList(Environment.getExternalStorageDirectory().absolutePath)
        }
    }


    suspend fun fetchFileList(path: String) {
        val file = File(path)
        Log.i(TAG, "absolute path: ${file.absolutePath}")
        val fileList = file.listFiles()
        if (fileList.isNullOrEmpty()) {
            Log.i(TAG, "empty or null path: ${file.absolutePath}")
        } else {
            fileListFlow.emit(fileList)
        }
    }
}

class FilesViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FilesViewModel(application) as T
    }
}