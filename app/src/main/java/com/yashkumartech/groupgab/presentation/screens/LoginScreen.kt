package com.yashkumartech.groupgab.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.yashkumartech.groupgab.presentation.components.AppLogoLarge
import com.yashkumartech.groupgab.presentation.components.OutlinedTextInput
import com.yashkumartech.groupgab.presentation.state.AuthStates
import com.yashkumartech.groupgab.presentation.viewmodels.ChatViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    chatViewModel: ChatViewModel
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var userNameTextState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }
    var groupIdTextState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }
    var userNameTextFieldFocusState by remember { mutableStateOf(false) }
    var groupIdTextFieldFocusState by remember { mutableStateOf(false) }
    val uiState = chatViewModel.state.collectAsState()
    LaunchedEffect(key1 = uiState.value.errorMessage) {
        if(uiState.value.errorMessage == "Group ID not found") {
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    "Group does not exist.",
                    duration = SnackbarDuration.Long,
                    actionLabel = "Create group",
                    withDismissAction = true
                )
                if(result == SnackbarResult.ActionPerformed) {
                    chatViewModel.createGroup(
                        username = userNameTextState.text,
                        groupId = groupIdTextState.text
                    )
                }
            }
        } else if(uiState.value.errorMessage.isNotEmpty()) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    uiState.value.errorMessage.toString(),
                    duration = SnackbarDuration.Long,
                    withDismissAction = true
                )
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppLogoLarge()
            OutlinedTextInput(
                onTextChanged = {
                    userNameTextState = it
                },
                textFieldValue = userNameTextState,
                onTextFieldFocused = { focused ->
                    userNameTextFieldFocusState = focused
                },
                label = "User name",
                enabled = uiState.value.screenState != AuthStates.LOADING
            )
            OutlinedTextInput(
                onTextChanged = {
                    groupIdTextState = it
                },
                textFieldValue = groupIdTextState,
                onTextFieldFocused = { focused ->
                    groupIdTextFieldFocusState = focused
                },
                label = "Group id",
                enabled = uiState.value.screenState != AuthStates.LOADING
            )
            Button(
                onClick = {
                    chatViewModel.loginUser(
                        username = userNameTextState.text,
                        groupId = groupIdTextState.text
                    )
                },
                enabled = uiState.value.screenState != AuthStates.LOADING
            ) {
                Text("Enter group chat", style = MaterialTheme.typography.titleMedium)
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
            }
        }
    }
}