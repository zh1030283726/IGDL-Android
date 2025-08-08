# 📱 IGDL-Android （Instagram Reels Dowloader Android）

IGDL-Android 是一款 Android 平台的 Instagram Reels / 影片下載工具，支援多語言與自訂伺服器端點，簡單高效，無需額外安裝插件即可使用。

![GitHub release](https://img.shields.io/github/v/release/zh1030283726/IGDL-Android/releases)
![License](https://img.shields.io/github/license/zh1030283726/IGDL-Android)
![Platform](https://img.shields.io/badge/platform-Android-green)
---
### 語言
### [ENG](/README_EN.md) [繁體中文](/README.md)
---

## ✨ 功能特點
- 🚀 **快速下載** Instagram Reels / 影片
- 🌏 **多語言支援**（English、繁體中文）
- ⚙️ **自訂伺服器 URL**（HTTP / HTTPS，自動補全預設端口）
- 📂 **自訂下載目錄**（透過系統檔案選擇器）
- 📢 **下載提示**（Toast 提示下載開始）
- 🖼️ 自訂應用圖示

---
## 🖥️ 伺服器搭建
- 方案A:
    透過 [Instagram-reels-download-docker](https://github.com/zh1030283726/Instagram-reels-download-docker)搭建

- 方案B:
    透過上方[原項目](https://github.com/Okramjimmy/Instagram-reels-downloader)搭建
---
## 📥 安裝方式
1. 前往 [Releases](https://github.com/你的GitHub帳號/IGDL-Android/releases) 頁面下載最新版 APK。
2. 將 APK 傳送到手機並安裝。
3. 打開應用，設定伺服器 URL 與下載位置後即可使用。

---

## 🛠️ 編譯方法

### 先決條件
- [Android Studio](https://developer.android.com/studio) (推薦最新版本)
- JDK 17 或以上
- Android SDK (Compile SDK 34)

### 編譯步驟
```bash
# 1. Clone 專案
git clone https://github.com/你的GitHub帳號/IGDL-Android.git
cd IGDL-Android

# 2. 使用 Gradle 編譯 Release APK
./gradlew assembleRelease
```
---

### 📚 基於項目 / Credits
本項目基於以下開源專案修改與擴展：

Instagram-reels-downloader — https://github.com/Okramjimmy/Instagram-reels-downloader

---

### 🧾 授權
本項目採用 MIT License 授權。
