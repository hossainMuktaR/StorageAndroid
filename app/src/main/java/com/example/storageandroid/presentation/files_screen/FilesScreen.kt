package com.example.storageandroid.presentation.files_screen

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storageandroid.R
import kotlinx.coroutines.launch

@Composable
fun FilesScreen(
    application: Application
) {
    val vm = viewModel<FilesViewModel>(
        factory = FilesViewModelFactory(application)
    )
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
        onResult = {
            it?.let {
                Log.i("fileScreen", "accept path: $it")
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                application.contentResolver.takePersistableUriPermission(it, takeFlags)
            }
        }
    )


    val fileList = vm.fileListFlow.collectAsState().value.sorted()
    val docList = vm.docListFlow.collectAsState().value.toList()
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        val uriPath =
            Uri.parse("content://com.android.externalstorage.documents/tree/primary/document/primary%3AAndroid%2Fdata%2Fcom.dv.adm")
//        launcher.launch(uriPath)
    }
    if (fileList.isEmpty()) {
        if (docList.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(docList) { file ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(70.dp)
                            .padding(8.dp)
                            .clickable {
                                scope.launch {
                                    vm.fetchDocFiles(file.uri)
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        file.name?.let { Text(it) }
                    }
                    Divider()
                }
            }
        }else{
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Folder is Empty")
            }
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(fileList) { file ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(70.dp)
                        .padding(8.dp)
                        .clickable {
                            scope.launch {
                                vm.fetchFileList(file.absolutePath)
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(file.name)
                }
                Divider()
            }
        }
    }
}

