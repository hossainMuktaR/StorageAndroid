package com.example.storageandroid

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.storageandroid.ui.theme.StorageAndroidTheme

class MainActivity : ComponentActivity() {
    private val TEXT_KEY = "text_key"
    lateinit var preference: SharedPreferences
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preference = getSharedPreferences("my_preference", Context.MODE_PRIVATE)
        setContent {
            StorageAndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var textValue by remember {
                        mutableStateOf("")
                    }
                    var dataText: String? by remember {
                        mutableStateOf(null)
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ){
                        Text(text = if(dataText != null) "Data: $dataText" else "No Data Found")
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(
                            value = textValue,
                            onValueChange ={
                                textValue = it
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick= {
                            saveTextInSharedPreference(textValue)
                            textValue = ""
                        }) {
                            Text(text = "Save")
                        }
                        Button(onClick= {
                            dataText = getTextWithKey(TEXT_KEY)
                        }) {
                            Text(text = "Read")
                        }
                    }
                }
            }
        }
    }
    private fun saveTextInSharedPreference(text: String) {
        with( preference.edit()){
            putString(TEXT_KEY, text)
            apply()
        }
    }
    private fun getTextWithKey(key: String): String?{
        return preference.getString(key, null)
    }
}
