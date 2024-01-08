package com.yashkumartech.groupgab.presentation.state

import com.yashkumartech.groupgab.data.Message

data class UserState(
    val username: String = "",
    val groupId: String = "",
    val screenState: AuthStates = AuthStates.INITIALIZED,
    val errorMessage: String = "",
    val messages: List<Message> = emptyList()
)

enum class AuthStates {
    INITIALIZED,
    LOADING,
    AUTHORIZED
}
