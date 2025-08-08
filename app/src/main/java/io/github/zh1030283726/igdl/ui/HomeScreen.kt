package io.github.zh1030283726.igdl.ui

import android.app.DownloadManager
import androidx.compose.ui.Alignment
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import io.github.zh1030283726.igdl.R
import io.github.zh1030283726.igdl.data.SettingsViewModel
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject

private fun normalizeBaseUrl(input: String): String {
    var s = input.trim()
    if (!s.startsWith("http://") && !s.startsWith("https://")) s = "http://$s"
    while (s.endsWith("/")) s = s.dropLast(1)
    return s
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onOpenSettings: () -> Unit, settingsVm: SettingsViewModel) {
    val server by settingsVm.serverUrl.collectAsState()
    val downloadDirUri by settingsVm.downloadDirUri.collectAsState()
    var url by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.app_name))
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(24.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = stringResource(R.string.home_title), style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text(stringResource(R.string.enter_reel_url)) },
                placeholder = { Text("https://www.instagram.com/reel/XXXX") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    // ❗ 未設置下載路徑 → 提示並導向設定頁，且不開始下載
                    if (downloadDirUri.isNullOrBlank()) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.please_set_download_dir_first),
                            Toast.LENGTH_SHORT
                        ).show()
                        onOpenSettings()
                        return@Button
                    }

                    // 開始下載提示（Toast）
                    Toast.makeText(
                        context,
                        context.getString(R.string.start_download),
                        Toast.LENGTH_SHORT
                    ).show()

                    isLoading = true
                    message = null

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val client = OkHttpClient()

                            // 後端 API
                            val base = normalizeBaseUrl(server).toHttpUrlOrNull()
                                ?: throw IllegalArgumentException(context.getString(R.string.bad_server_url))

                            val apiUrl: HttpUrl = base.newBuilder()
                                .addPathSegment("api")
                                .addPathSegment("video")
                                .addQueryParameter("postUrl", url)
                                .build()

                            val apiResp = client.newCall(
                                Request.Builder()
                                    .url(apiUrl)
                                    .header("User-Agent", "IGDL/1.0 (Android)")
                                    .get()
                                    .build()
                            ).execute()

                            val bodyStr = apiResp.body?.string()?.trim().orEmpty()
                            if (!apiResp.isSuccessful) {
                                message = context.getString(
                                    R.string.api_http_error,
                                    apiResp.code,
                                    if (bodyStr.isEmpty()) "Error" else bodyStr
                                )
                                return@launch
                            }

                            val root = JSONObject(bodyStr)
                            val ok = root.optString("status") == "success"
                            val data = root.optJSONObject("data")
                            val fileUrl = data?.optString("videoUrl")
                            val fileName = data?.optString("filename") ?: "ig-video.mp4"
                            if (!ok || fileUrl.isNullOrBlank()) {
                                message = bodyStr
                                return@launch
                            }

                            val targetName = if (fileName.endsWith(".mp4", true)) fileName else "$fileName.mp4"
                            val ua = "IGDL/1.0 (Android)"

                            // 使用者已選下載資料夾（必須）→ 走 SAF 串流，對 HTTPS 最穩
                            val treeUri = Uri.parse(downloadDirUri)
                            val tree = DocumentFile.fromTreeUri(context, treeUri)
                            val target = tree?.createFile("video/mp4", targetName)
                            val sink = target?.uri?.let { context.contentResolver.openOutputStream(it) }
                            if (sink == null) {
                                message = context.getString(R.string.cannot_create_file, targetName)
                            } else {
                                val fileReq = Request.Builder().url(fileUrl).header("User-Agent", ua).build()
                                client.newCall(fileReq).execute().use { resp ->
                                    if (!resp.isSuccessful) throw IllegalStateException("HTTPS ${resp.code}")
                                    resp.body?.byteStream()?.use { input ->
                                        sink.use { out -> input.copyTo(out, 8 * 1024) }
                                    }
                                }
                                message = context.getString(R.string.saved_as, targetName)
                            }
                        } catch (e: Exception) {
                            message = e.localizedMessage
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = url.isNotBlank() && !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Download, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.download))
            }

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            message?.let {
                ElevatedCard {
                    Text(it, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                }
            }

            AssistChip(onClick = { url = "" }, label = { Text(stringResource(R.string.clear)) })
            Text(text = stringResource(R.string.server_in_use, server), style = MaterialTheme.typography.labelMedium)
        }
    }
}
