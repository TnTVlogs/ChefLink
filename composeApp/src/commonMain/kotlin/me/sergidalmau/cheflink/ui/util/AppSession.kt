package me.sergidalmau.cheflink.ui.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.sergidalmau.cheflink.domain.models.User

object AppSession {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    fun loginUser(user: User) {
        _currentUser.value = user
    }

    fun logout() {
        _currentUser.value = null
    }
}
