package com.yashkumartech.groupgab.presentation.screens

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.yashkumartech.groupgab.presentation.state.AuthStates
import com.yashkumartech.groupgab.presentation.viewmodels.ChatViewModel

@Composable
fun BaseScreen(
    chatViewModel: ChatViewModel
) {
    val activity = LocalContext.current as Activity
    val uiState = chatViewModel.state.collectAsState()
    LaunchedEffect(key1 = null) {
        chatViewModel.isCredentialsSaved()
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp)
    ) {
        when(uiState.value.screenState) {
            AuthStates.AUTHORIZED -> {
                ChatScreen(chatViewModel = chatViewModel)
            }
            else -> {
                LoginScreen(chatViewModel = chatViewModel)
            }
        }
    }
}