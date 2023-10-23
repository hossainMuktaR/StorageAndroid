package com.example.storageandroid.presentation.files_screen

import android.app.Application
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

    val fileList = vm.fileListFlow.collectAsState().value.sorted()
    val scope = rememberCoroutineScope()
    if(fileList.isEmpty()){
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Folder is Empty")
        }
    }else {
        LazyColumn(modifier = Modifier.fillMaxSize()){
            items(fileList) {file ->
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .size(70.dp)
                    .padding(8.dp)
                    .clickable {
                        scope.launch {
                            vm.fetchFileList(file.absolutePath)
                        }
                    },
                    verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(file.name)
                }
                Divider()
            }
        }
    }
}