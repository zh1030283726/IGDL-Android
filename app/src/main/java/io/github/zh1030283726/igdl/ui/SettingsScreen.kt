package io.github.zh1030283726.igdl.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope // ★ 新增
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // ★ 新增
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import io.github.zh1030283726.igdl.R
import io.github.zh1030283726.igdl.data.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, settingsVm: SettingsViewModel) {
    val darkMode by settingsVm.darkMode.collectAsState()
    val locale by settingsVm.locale.collectAsState()
    val server by settingsVm.serverUrl.collectAsState()
    val downloadDirUri by settingsVm.downloadDirUri.collectAsState() // ★ 新增

    val context = LocalContext.current // ★ 新增

    val pickFolderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            try { context.contentResolver.takePersistableUriPermission(uri, flags) } catch (_: Exception) {}
            settingsVm.setDownloadDirUri(uri.toString())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 外觀
            Text(stringResource(R.string.appearance), style = MaterialTheme.typography.titleLarge)
            SegmentedButtonRow {
                SingleChoiceSegmentedButton(
                    selected = darkMode == "light",
                    onClick = { settingsVm.setDarkMode("light") },
                    label = { Text(stringResource(R.string.light)) }
                )
                SingleChoiceSegmentedButton(
                    selected = darkMode == "dark",
                    onClick = { settingsVm.setDarkMode("dark") },
                    label = { Text(stringResource(R.string.dark)) }
                )
                SingleChoiceSegmentedButton(
                    selected = darkMode == "system",
                    onClick = { settingsVm.setDarkMode("system") },
                    label = { Text(stringResource(R.string.follow_system)) }
                )
            }

            HorizontalDivider()

            // 語言
            Text(stringResource(R.string.language), style = MaterialTheme.typography.titleLarge)
            SegmentedButtonRow {
                SingleChoiceSegmentedButton(
                    selected = locale == "en",
                    onClick = { settingsVm.setLocale("en") },
                    label = { Text("English") }
                )
                SingleChoiceSegmentedButton(
                    selected = locale == "zh-TW",
                    onClick = { settingsVm.setLocale("zh-TW") },
                    label = { Text("繁體中文") }
                )
                SingleChoiceSegmentedButton(
                    selected = locale == "system",
                    onClick = { settingsVm.setLocale("system") },
                    label = { Text(stringResource(R.string.follow_system)) }
                )
            }

            HorizontalDivider()

            // 伺服器
            Text(stringResource(R.string.server), style = MaterialTheme.typography.titleLarge)
            var serverText by remember(server) { mutableStateOf(server) }
            OutlinedTextField(
                value = serverText,
                onValueChange = { serverText = it },
                label = { Text(stringResource(R.string.server_base_url)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { settingsVm.setServerUrl(serverText) }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.save))
            }

            HorizontalDivider()

            // 下載路徑
            Text(stringResource(R.string.download_location_title), style = MaterialTheme.typography.titleLarge)
            Text(
                text = downloadDirUri?.let { uri ->
                    DocumentFile.fromTreeUri(context, Uri.parse(uri))?.name ?: uri
                } ?: stringResource(R.string.download_location_none),
                style = MaterialTheme.typography.bodyMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { pickFolderLauncher.launch(Uri.EMPTY) }, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.choose_folder))
                }
                OutlinedButton(onClick = { settingsVm.setDownloadDirUri(null) }, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.clear_choice))
                }
            }
        }
    }
}

@Composable
private fun SegmentedButtonRow(content: @Composable RowScope.() -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { content() }
}

@Composable
private fun RowScope.SingleChoiceSegmentedButton(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit
) {
    if (selected) {
        FilledTonalButton(onClick = onClick, modifier = Modifier.weight(1f)) { label() }
    } else {
        OutlinedButton(onClick = onClick, modifier = Modifier.weight(1f)) { label() }
    }
}
