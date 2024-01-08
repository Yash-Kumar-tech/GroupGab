package com.yashkumartech.groupgab

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.yashkumartech.groupgab.data.MessagesRepository
import com.yashkumartech.groupgab.data.UserRepository
import com.yashkumartech.groupgab.presentation.screens.BaseScreen
import com.yashkumartech.groupgab.presentation.viewmodels.ChatViewModel
import com.yashkumartech.groupgab.presentation.viewmodels.UserViewModelFactory
import com.yashkumartech.groupgab.ui.theme.GroupGabTheme

private const val USER_DETAILS_NAME = "user_details"

private val Context.dataStore by preferencesDataStore(
    name = "USER_DETAILS_NAME",
    produceMigrations = { context ->
        listOf(SharedPreferencesMigration(context, USER_DETAILS_NAME))
    }
)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GroupGabTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val chatViewModel = ViewModelProvider(
                        this,
                        UserViewModelFactory(
                            UserRepository(dataStore = dataStore),
                            MessagesRepository()
                        )
                    )[ChatViewModel::class.java]

                    BaseScreen(chatViewModel = chatViewModel)
                }
            }
        }
    }
}