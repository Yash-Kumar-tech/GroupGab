package com.yashkumartech.groupgab.presentation.screens

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.yashkumartech.groupgab.data.Message
import com.yashkumartech.groupgab.presentation.components.OutlinedTextInput
import com.yashkumartech.groupgab.presentation.viewmodels.ChatViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel
) {
    val activity = LocalContext.current as Activity
    val uiState = chatViewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val messages = chatViewModel.messages.observeAsState(initial = emptyList())
    LaunchedEffect(Unit) {
        chatViewModel.getMessages()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.value.groupId) },
                actions = {
                    IconButton(onClick = { chatViewModel.logoutUser() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            MessageInput(
                onSendMessage = { text ->
                    Log.d("SENDMESSAGE", "SENDING MESSAGE")
                    chatViewModel.sendMessage(text)
                },
                resetScroll = {
                    coroutineScope.launch { 
                        listState.scrollToItem(0)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            ChatList(
                messages = messages.value,
                state = listState,
                username = uiState.value.username
            )
        }
    }
}

@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    resetScroll: () -> Unit = {},
) {
    var userMessageTextState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(end = 8.dp)
            ) {
                OutlinedTextInput(
                    onTextChanged = { userMessageTextState = it },
                    textFieldValue = userMessageTextState,
                    onTextFieldFocused = {},
                    label = "Message",
                )
            }
            IconButton(
                onClick = {
                    if(userMessageTextState.text.isNotEmpty()) {
                        onSendMessage(userMessageTextState.text)
                        userMessageTextState = TextFieldValue()
                        resetScroll()
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Message",
                    modifier = Modifier
                        .padding(8.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun ChatList(
    messages: List<Message>,
    state: LazyListState,
    username: String
) {
    LazyColumn(
        state = state
    ) {
        items(messages) {message ->
            ChatMessage(message, username)
        }
    }
}

@Composable
fun ChatMessage(
    message: Message,
    username: String
) {
    val isSelfMessage = message.sender == username
    val bgColor = if(isSelfMessage) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer

    val shape = if(isSelfMessage) {
        RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)
    } else {
        RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
    }

    val alignment = if(isSelfMessage) {
        Alignment.End
    } else {
        Alignment.Start
    }

    Column(
        horizontalAlignment = alignment,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = message.sender,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        BoxWithConstraints {
            Card(
                colors = CardDefaults.cardColors(containerColor = bgColor),
                shape = shape,
                modifier = Modifier
                    .widthIn(max = maxWidth * 0.9f)
            ) {
                Text(
                    text = message.message,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}