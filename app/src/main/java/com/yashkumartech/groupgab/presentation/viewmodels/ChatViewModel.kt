package com.yashkumartech.groupgab.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yashkumartech.groupgab.data.Message
import com.yashkumartech.groupgab.data.MessagesRepository
import com.yashkumartech.groupgab.data.UserRepository
import com.yashkumartech.groupgab.presentation.state.AuthStates
import com.yashkumartech.groupgab.presentation.state.UserState
import com.yashkumartech.groupgab.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ChatViewModel(
    private val userRepository: UserRepository,
    private val messagesRepository: MessagesRepository
): ViewModel() {

    private val _state: MutableStateFlow<UserState> = MutableStateFlow(UserState())
    val state: StateFlow<UserState> = _state.asStateFlow()

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>>
        get() = _messages

    fun isCredentialsSaved() {
        viewModelScope.launch {
            userRepository
                .getUserDetails()
                .collect { userDetails ->
                    when(userDetails) {
                        is Resource.Error -> {
                            _state.update {
                                it.copy(
                                    username = "",
                                    groupId = "",
                                    screenState = AuthStates.INITIALIZED
                                )
                            }
                        }
                        is Resource.Loading -> {
                            _state.update {
                                it.copy(
                                    screenState = AuthStates.LOADING
                                )
                            }
                        }
                        is Resource.Success -> {
                            _state.update {
                                it.copy(
                                    username = userDetails.data!!.userName,
                                    groupId = userDetails.data.groupId,
                                    screenState = AuthStates.AUTHORIZED
                                )
                            }
                        }
                    }
                }
        }
    }

    fun loginUser(
        username: String,
        groupId: String
    ) {
        viewModelScope.launch {
            userRepository
                .login(username, groupId)
                .collect { group ->
                    when(group) {
                        is Resource.Error -> {
                            _state.update {
                                it.copy(
                                    screenState = AuthStates.INITIALIZED,
                                    errorMessage = group.message!!
                                )
                            }
                        }
                        is Resource.Loading -> {
                            _state.update {
                                it.copy(
                                    screenState = AuthStates.LOADING,
                                    errorMessage = ""
                                )
                            }
                        }
                        is Resource.Success -> {
                            _state.update {
                                it.copy(
                                    screenState = AuthStates.AUTHORIZED,
                                    errorMessage = ""
                                )
                            }
                        }
                    }
                }
        }
    }

    fun createGroup(
        username: String,
        groupId: String
    ) {
        viewModelScope.launch {
            userRepository.createGroup(groupId)
                .collect {group ->
                    when(group) {
                        is Resource.Error -> {
                            _state.update {
                                it.copy(
                                    screenState = AuthStates.INITIALIZED,
                                    errorMessage = group.message.toString()
                                )
                            }
                        }
                        is Resource.Loading -> {
                            _state.update {
                                it.copy(
                                    screenState = AuthStates.LOADING,
                                    errorMessage = ""
                                )
                            }
                        }
                        is Resource.Success -> {
                            _state.update {
                                it.copy(
                                    username = username,
                                    groupId = groupId,
                                    screenState = AuthStates.AUTHORIZED,
                                    errorMessage = ""
                                )
                            }
                        }
                    }
                }
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            userRepository.logoutUser()
        }
    }


    fun sendMessage(
        messageText: String
    ) {
        viewModelScope.launch {
            try {
                messagesRepository.sendMessage(
                    username = state.value.username,
                    groupId = state.value.groupId,
                    messageText = messageText
                )
            } catch(e: Exception) {
                Log.d("SENDMESSAGE", "AN error occurred!!! $e")
            }
        }
    }

    fun getMessages() {
        messagesRepository.getMessages(state.value.groupId) { messagesList ->
            _messages.postValue(messagesList)
        }
    }
}

class UserViewModelFactory(
    private val userRepository: UserRepository,
    private val messagesRepository: MessagesRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(userRepository, messagesRepository) as T
        }
        throw IllegalArgumentException("Unknown ChatViewModel class")
    }
}