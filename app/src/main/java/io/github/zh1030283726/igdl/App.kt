package io.github.zh1030283726.igdl

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class App: Application() {
    private val _appLocale = MutableStateFlow<String>("system")
    val appLocale = _appLocale.asStateFlow()

    fun updateLocale(tag: String) {
        _appLocale.value = tag
        val locales = if (tag == "system") {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(tag)
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }

    override fun onCreate() {
        super.onCreate()
        // Start following system by default
        updateLocale("system")
    }
}
