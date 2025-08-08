# 📱 IGDL-Android (Instagram Reels Dowloader Android)

IGDL-Android is an Android-based tool for downloading Instagram Reels and videos.  
It supports multiple languages and custom server endpoints.  
Simple, efficient, and works out of the box without any additional plugins.

![GitHub release](https://img.shields.io/github/v/release/zh1030283726/IGDL-Android)
![License](https://img.shields.io/github/license/zh1030283726/IGDL-Android)
![Platform](https://img.shields.io/badge/platform-Android-green)

---
### Language
### [ENG](/README.md) [繁體中文](/README_TW.md)
---

## ✨ Features
- 🚀 **Fast downloads** for Instagram Reels / videos
- 🌏 **Multi-language support** (English, Traditional Chinese)
- ⚙️ **Custom server URL** (HTTP / HTTPS, auto-completes default ports)
- 📂 **Custom download directory** (via system file picker)
- 📢 **Download notification** (Toast message when download starts)
- 🖼️ Customizable app icon

---

## 🖥️ Server Setup
- **Option A:**  
  Deploy using [Instagram-reels-download-docker](https://github.com/zh1030283726/Instagram-reels-download-docker)

- **Option B:**  
  Deploy using the [original repo](https://github.com/Okramjimmy/Instagram-reels-downloader)

---

## 📥 Installation
1. Go to the [Releases](https://github.com/YourGitHubUsername/IGDL-Android/releases) page and download the latest APK.
2. Transfer the APK to your phone and install it.
3. Open the app, set the server URL and download location, and start using it.

---

## 🛠️ Build Instructions

### Prerequisites
- [Android Studio](https://developer.android.com/studio) (latest version recommended)
- JDK 17 or higher
- Android SDK (Compile SDK 34)

### Build Steps
```bash
# 1. Clone the repository
git clone https://github.com/YourGitHubUsername/IGDL-Android.git
cd IGDL-Android

# 2. Build the Release APK with Gradle
./gradlew assembleRelease
```
---

## 📚 Based On / Credits
This project is modified and extended from the following open-source Repo:
Instagram-reels-downloader — https://github.com/Okramjimmy/Instagram-reels-downloader

---

## 🧾 License
This project is licensed under the [MIT License](/LICENSE).
