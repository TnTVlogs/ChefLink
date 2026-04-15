package me.sergidalmau.cheflink.ui.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.sergidalmau.cheflink.domain.models.User

object AppSession {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken = _accessToken.asStateFlow()

    private val _refreshToken = MutableStateFlow<String?>(null)
    val refreshToken = _refreshToken.asStateFlow()

    fun loginUser(user: User, accessToken: String, refreshToken: String) {
        _currentUser.value = user
        _accessToken.value = accessToken
        _refreshToken.value = refreshToken
    }

    fun updateAccessToken(newToken: String) {
        _accessToken.value = newToken
    }

    fun updateRefreshToken(newToken: String) {
        _refreshToken.value = newToken
    }

    fun restoreSession(user: User, access: String, refresh: String) {
        _currentUser.value = user
        _accessToken.value = access
        _refreshToken.value = refresh
    }

    fun logout() {
        _currentUser.value = null
        _accessToken.value = null
        _refreshToken.value = null
    }
}
