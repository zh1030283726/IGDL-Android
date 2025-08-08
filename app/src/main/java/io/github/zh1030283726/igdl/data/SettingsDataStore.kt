package io.github.zh1030283726.igdl.data

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "settings")

object Keys {
    val DARK_MODE = stringPreferencesKey("dark_mode") // "light" | "dark" | "system"
    val LOCALE = stringPreferencesKey("locale")       // "en" | "zh-TW" | "system"
    val SERVER = stringPreferencesKey("server_url")
    val DOWNLOAD_DIR = stringPreferencesKey("download_dir_uri") // ★ 新增
}

class SettingsViewModel(app: Application): AndroidViewModel(app) {
    private val store = app.applicationContext.dataStore
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _darkMode = MutableStateFlow("system")
    val darkMode: StateFlow<String> = _darkMode

    private val _locale = MutableStateFlow("system")
    val locale: StateFlow<String> = _locale

    private val _serverUrl = MutableStateFlow("http://your.server.example")
    val serverUrl: StateFlow<String> = _serverUrl

    // ★ 下載資料夾 URI
    private val _downloadDirUri = MutableStateFlow<String?>(null)
    val downloadDirUri: StateFlow<String?> = _downloadDirUri

    init {
        scope.launch {
            store.data.map { it[Keys.DARK_MODE] ?: "system" }.collect { _darkMode.value = it }
        }
        scope.launch {
            store.data.map { it[Keys.LOCALE] ?: "system" }.collect { loc ->
                _locale.value = loc
                val locales = if (loc == "system") LocaleListCompat.getEmptyLocaleList()
                else LocaleListCompat.forLanguageTags(loc)
                AppCompatDelegate.setApplicationLocales(locales)
            }
        }
        scope.launch {
            store.data.map { it[Keys.SERVER] ?: "http://your.server.example" }.collect { _serverUrl.value = it }
        }
        // ★ 收集下載資料夾
        scope.launch {
            store.data.map { it[Keys.DOWNLOAD_DIR] }.collect { _downloadDirUri.value = it }
        }
    }

    fun setDarkMode(mode: String) = scope.launch { store.edit { it[Keys.DARK_MODE] = mode } }
    fun setLocale(tag: String) = scope.launch { store.edit { it[Keys.LOCALE] = tag } }

    // 規範化 server：無協議→補 http://；去掉尾端 /
    fun setServerUrl(input: String) = scope.launch {
        var s = input.trim()
        if (!s.startsWith("http://") && !s.startsWith("https://")) s = "http://$s"
        while (s.endsWith("/")) s = s.dropLast(1)
        store.edit { it[Keys.SERVER] = s }
    }

    // ★ 設定/清除 下載資料夾 URI（SAF）
    fun setDownloadDirUri(uri: String?) = scope.launch {
        store.edit { prefs ->
            if (uri == null) prefs.remove(Keys.DOWNLOAD_DIR) else prefs[Keys.DOWNLOAD_DIR] = uri
        }
    }
}
