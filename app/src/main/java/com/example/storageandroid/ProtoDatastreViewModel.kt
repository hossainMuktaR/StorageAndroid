package com.example.storageandroid

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import com.example.storageAndroid.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProtoDatastreViewModel(userPreferencesStore: DataStore<UserPreferences>): ViewModel() {
    val nameFlow: Flow<String> = userPreferencesStore.data
        .map { preferences ->
            preferences.username
        }
    val ageFlow: Flow<Int> = userPreferencesStore.data
        .map { preferences ->
            preferences.age
        }
}