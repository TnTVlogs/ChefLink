package me.sergidalmau.cheflink.data.repository

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import me.sergidalmau.cheflink.ui.util.Language
import me.sergidalmau.cheflink.ui.util.ComponentSize
import me.sergidalmau.cheflink.data.remote.BASE_URL
import kotlinx.serialization.json.Json
import me.sergidalmau.cheflink.domain.models.User

class SettingsRepository(private val settings: Settings = Settings()) {

    companion object {
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_COMPONENT_SIZE = "component_size"
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_SERVER_ENABLED = "server_enabled" // Desktop only
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_JSON = "user_json"
        private val DEFAULT_SERVER_URL = BASE_URL
    }

    var isServerEnabled: Boolean
        get() = settings[KEY_SERVER_ENABLED, true]
        set(value) {
            settings[KEY_SERVER_ENABLED] = value
        }

    var isDarkMode: Boolean
        get() = settings[KEY_DARK_MODE, false]
        set(value) {
            settings[KEY_DARK_MODE] = value
        }

    var language: Language
        get() {
            val name = settings[KEY_LANGUAGE, Language.CA.name]
            return try {
                Language.valueOf(name)
            } catch (_: Exception) {
                Language.CA
            }
        }
        set(value) {
            settings[KEY_LANGUAGE] = value.name
        }

    var componentSize: ComponentSize
        get() {
            val name = settings[KEY_COMPONENT_SIZE, ComponentSize.MEDIUM.name]
            return try {
                ComponentSize.valueOf(name)
            } catch (_: Exception) {
                ComponentSize.MEDIUM
            }
        }
        set(value) {
            settings[KEY_COMPONENT_SIZE] = value.name
        }

    var serverUrl: String
        get() {
            val url = settings[KEY_SERVER_URL, DEFAULT_SERVER_URL]
            return url.ifEmpty { DEFAULT_SERVER_URL }
        }
        set(value) {
            settings[KEY_SERVER_URL] = value
        }

    var accessToken: String?
        get() = settings[KEY_ACCESS_TOKEN]
        set(value) {
            if (value == null) settings.remove(KEY_ACCESS_TOKEN)
            else settings[KEY_ACCESS_TOKEN] = value
        }

    var refreshToken: String?
        get() = settings[KEY_REFRESH_TOKEN]
        set(value) {
            if (value == null) settings.remove(KEY_REFRESH_TOKEN)
            else settings[KEY_REFRESH_TOKEN] = value
        }

    private val json = Json { ignoreUnknownKeys = true }

    var userJson: String?
        get() = settings[KEY_USER_JSON]
        set(value) {
            if (value == null) settings.remove(KEY_USER_JSON)
            else settings[KEY_USER_JSON] = value
        }

    var persistedUser: User?
        get() = userJson?.let {
            try {
                json.decodeFromString<User>(it)
            } catch (_: Exception) {
                null
            }
        }
        set(value) = if (value != null) {
            userJson = json.encodeToString(User.serializer(), value)
        } else {
            userJson = null
        }
}
