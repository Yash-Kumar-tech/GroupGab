package com.yashkumartech.groupgab.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.yashkumartech.groupgab.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton
import kotlin.Exception


data class UserDetails(
    val userName: String,
    val groupId: String
)

@Singleton
class UserRepository(
    private val dataStore: DataStore<Preferences>
) {

    private val groupIdsDb = Firebase.firestore.collection("groupIds")

    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val GROUP_ID = stringPreferencesKey("group_id")
    }

    private val userPreferencesFlow: Flow<UserDetails> = dataStore.data
        .catch { exception ->
            emit(emptyPreferences())
        }
        .map { preferences ->
            mapUserPreferences(preferences)
        }

    suspend fun getUserDetails(): Flow<Resource<UserDetails>> {
        return flow {
            emit(Resource.Loading())
            userPreferencesFlow.collect {
                if(it.groupId.isNotBlank() and it.userName.isNotBlank()) {
                    emit(Resource.Success(it))
                } else {
                    emit(Resource.Error("Couldn't read saved user details."))
                }
            }
        }
    }

    private fun mapUserPreferences(preferences: Preferences): UserDetails {
        val username = preferences[PreferencesKeys.USER_NAME] ?: ""
        val groupid = preferences[PreferencesKeys.GROUP_ID] ?: ""
        return UserDetails(username, groupid)
    }

    suspend fun login(username: String, groupId: String): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading())
            if(username.isEmpty()) {
                emit(Resource.Error("Invalid username"))
                return@flow
            }
            if(groupId.isEmpty()) {
                emit(Resource.Error("Invalid group name"))
                return@flow
            }
            try {
                val documentSnapshot = groupIdsDb.document(groupId).get().await()
                if (documentSnapshot.exists()) {
                    dataStore.edit { preferences ->
                        preferences[PreferencesKeys.USER_NAME] = username
                        preferences[PreferencesKeys.GROUP_ID] = groupId
                    }
                    emit(Resource.Success("Group exists"))
                } else {
                    emit(Resource.Error("Group ID not found"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "An error occurred"))
            }
        }
    }

    suspend fun createGroup(groupId: String): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading())
            try {
                val emptyMap = emptyMap<String, String>()
                val documentSnapshot = groupIdsDb.document(groupId).set(emptyMap).await()
                emit(Resource.Success(groupId))
            } catch(e: Exception) {
                emit(Resource.Error(e.message.toString()))
            }
        }
    }

    suspend fun logoutUser() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = ""
            preferences[PreferencesKeys.GROUP_ID] = ""
        }
    }
}