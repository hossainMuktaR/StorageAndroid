package com.example.storageandroid

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.storageandroid.ui.theme.StorageAndroidTheme
import java.io.File

class MainActivity : ComponentActivity() {
    private val TAG = "MAINACTIVTIY"
    val fileName = "SampleFile.txt"

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StorageAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val inputText = remember {
                        mutableStateOf("")
                    }
                    val fileExist = remember {
                        mutableStateOf(fileAlreadyExist(fileName))
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(if (fileExist.value) "File Exist" else "File not Exist")
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(value = inputText.value, onValueChange = {
                            inputText.value = it
                        })
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            fileExist.value = saveText(inputText.value)
                            inputText.value = ""
                        }) {
                            Text(text = "Save Text")
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(onClick = {
                            fileExist.value = !deleteFileWithName(fileName)
                        }) {
                            Text(text = "Delete File")
                        }
                    }
                }
            }
        }
    }

    private fun saveText(text: String): Boolean {
        openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(text.toByteArray())
            Log.i(TAG, "File saved Success")
        }
        return true
    }

    private fun fileAlreadyExist(name: String): Boolean {
        val fileList = fileList()
        for (fname in fileList) {
            if (fname == name) {
                return true
            }
        }
        return false
    }

    private fun deleteFileWithName(name: String): Boolean {
        val file = File(filesDir, name)
        return if (fileAlreadyExist(name)) {
            file.delete()
        } else {
            Log.i(TAG, "Delete unSuccessful")
            false
        }
    }
}
