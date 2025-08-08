package io.github.zh1030283726.igdl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.zh1030283726.igdl.ui.HomeScreen
import io.github.zh1030283726.igdl.ui.SettingsScreen
import io.github.zh1030283726.igdl.ui.theme.IGTheme
import io.github.zh1030283726.igdl.data.SettingsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val settingsVm: SettingsViewModel = viewModel()

            val darkMode by settingsVm.darkMode.collectAsState()
            val dark = when (darkMode) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }

            // IMPORTANT:
            // Do NOT call recreate() manually on locale change.
            // AppCompatDelegate.setApplicationLocales() will trigger a configuration change
            // and recreate activities automatically when using AppCompatActivity.
            IGTheme(darkTheme = dark) {
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            onOpenSettings = { navController.navigate("settings") },
                            settingsVm = settingsVm
                        )
                    }
                    composable("settings") {
                        SettingsScreen(onBack = { navController.popBackStack() }, settingsVm = settingsVm)
                    }
                }
            }
        }
    }
}