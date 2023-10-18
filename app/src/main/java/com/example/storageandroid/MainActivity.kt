package com.example.storageandroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.storageandroid.ui.theme.StorageAndroidTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "my-preference")

class MainActivity : ComponentActivity() {
    private val TEXT_KEY = stringPreferencesKey("text_key")
    //    lateinit var preference:
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    val scope = rememberCoroutineScope()
                    val dataText: String? by getTextWithKey(TEXT_KEY).collectAsState(initial = null)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = if (dataText != null) "Data: ${dataText.toString()}" else "No Data Found")
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(
                            value = textValue,
                            onValueChange = {
                                textValue = it
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            scope.launch {
                                saveTextInSharedPreference(textValue)
                                textValue = ""
                            }
                        }) {
                            Text(text = "Save")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                           Intent(this@MainActivity, ProtoDatastoreActivity::class.java ).also {
                               startActivity(it)
                           }
                        }) {
                            Text(text = "Goto Proto Datastore")
                        }
                    }
                }
            }
        }
    }
    private suspend fun saveTextInSharedPreference(text: String) {
        this.dataStore.edit { preference ->
        preference[TEXT_KEY] = text
        }
    }

    private fun getTextWithKey(key: Preferences.Key<String>): Flow<String?> {
        return this.dataStore.data
            .map { preference ->
                preference[key]
            }
    }
}