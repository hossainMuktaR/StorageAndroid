package com.example.storageandroid

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storageAndroid.UserPreferences
import com.example.storageandroid.ui.theme.StorageAndroidTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException

private const val DATA_STORE_FILE_NAME = "user_prefs.pb"

private val Context.userPreferencesStore: DataStore<UserPreferences> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = UserPreferencesSerializer
)

private val TAG = "protdatastore"

class ProtoDatastoreActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState,)

        val vm = viewModels<ProtoDatastreViewModel> {
            object: ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProtoDatastreViewModel(userPreferencesStore) as T
                }
            }
        }.value

        setContent {
            StorageAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val nameFlow by vm.nameFlow.collectAsState(initial = "")
                    val ageFlow by vm.ageFlow.collectAsState(initial = -1)
                    var name by remember {
                        mutableStateOf("")
                    }
                    var age by remember {
                        mutableStateOf("")
                    }
                    val scope = rememberCoroutineScope()
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Name: $nameFlow, Age: $ageFlow")
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(
                            value = name,
                            onValueChange = {
                                name = it
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(
                            value = age,
                            onValueChange = {
                                age = it
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            scope.launch {
                                saveNameAgeUseProto(name, age.toInt())
                                name = ""
                                age = ""
                            }
                        }) {
                            Text(text = "Save")
                        }
                    }
                }
            }
        }
    }

    private suspend fun saveNameAgeUseProto(name: String, age: Int) {
        userPreferencesStore.updateData { preferences ->
            preferences.toBuilder()
                .setUsername(name)
                .setAge(age)
                .build()
        }
    }

    private fun getTextWithKey(key: Preferences.Key<String>): Flow<String?> {
        return this.dataStore.data
            .map { preference ->
                preference[key]
            }
    }
}