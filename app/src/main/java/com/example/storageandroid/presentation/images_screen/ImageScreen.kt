package com.example.storageandroid.presentation.images_screen

import android.app.Activity.RESULT_OK
import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@Composable
fun ImageScreen(
    application: Application
) {
    val vm: ImagesViewModel = viewModel(
        factory = ImagesViewModelFactory(application)
    )
    val clickedImageUri = remember {
        mutableStateOf<Uri?>(null)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult =  {
            if (it.resultCode == RESULT_OK){
                clickedImageUri.value = null
            }
        }
    )
    val imageList = vm.imageListFlow.collectAsState()
    if (clickedImageUri.value != null) {
        Column(modifier = Modifier.fillMaxSize()){
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
                contentAlignment = Alignment.Center){
                AsyncImage(
                    model = clickedImageUri.value,
                    contentDescription = null
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround){
                Button(onClick = {

                    launcher.launch( vm.detetePhoto(clickedImageUri.value!!))

                    Log.i("imageScree","Delete button Clicked")
                }) {
                    Text("Delete Photo")
                }
                Button(onClick = { clickedImageUri.value = null}) {
                    Text("Exit")
                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 128.dp),
            ) {
                items(imageList.value) { image ->
                    AsyncImage(
                        model = image.uri,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            clickedImageUri.value = image.uri
                        }
                    )
                }
            }
        }
    }

}