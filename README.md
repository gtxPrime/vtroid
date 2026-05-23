<div align="center">

  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher_modified.png" alt="V-Troid Logo" width="120" height="120" />

# V-Troid

**Your Ultimate Android Media Player & Web Stream Interceptor**

  <p>
    <a href="https://github.com/gtxPrime/vtroid/stargazers">
      <img src="https://img.shields.io/github/stars/gtxPrime/vtroid?style=for-the-badge&color=yellow" alt="Stars" />
    </a>
    <a href="https://github.com/gtxPrime/vtroid/network/members">
      <img src="https://img.shields.io/github/forks/gtxPrime/vtroid?style=for-the-badge&color=orange" alt="Forks" />
    </a>
    <a href="https://github.com/gtxPrime/vtroid/issues">
      <img src="https://img.shields.io/github/issues/gtxPrime/vtroid?style=for-the-badge&color=blue" alt="Issues" />
    </a>
    <a href="https://github.com/gtxPrime/vtroid/blob/main/LICENSE">
      <img src="https://img.shields.io/badge/License-MIT-brightgreen?style=for-the-badge" alt="License" />
    </a>
    <a href="#">
      <img src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white&style=for-the-badge" alt="Platform" />
    </a>
  </p>

  <h3>
    <a href="#-features">Features</a>
    <span> | </span>
    <a href="#-tech-stack">Tech Stack</a>
    <span> | </span>
    <a href="#-repository-stats">Stats</a>
    <span> | </span>
    <a href="#-star-history">Star History</a>
    <span> | </span>
    <a href="#-documentation">Docs</a>
    <span> | </span>
    <a href="#-installation">Installation</a>
    <span> | </span>
    <a href="#-contributing">Contributing</a>
  </h3>

</div>

---

## 📱 About V-Troid

**V-Troid** is a highly optimized, dual-purpose Android media player and stream interceptor client. It is designed to act as a seamless native bridge to web-based content: when users browse movies and series on its companion web platform, V-Troid automatically captures the stream URLs, strips away intrusive page redirects or pop-ups, and pipes the media directly into its high-performance native ExoPlayer core.

> "Seamless hardware-accelerated streaming, directly from web browser to native player."

---

## 🚀 Features

### 🌐 Companion Web & Interception Engine
V-Troid integrates a custom Chromium-based browser engine configured to dynamically capture network redirects:
* **Download Redirector**: Intercepts video file links containing `vtroid` or `adminstreamx`, presenting a native dialog to play the stream instantly or redirect it to a local download agent.
* **Live Streaming Overrides**: Identifies custom `vtroid-live-match` URL protocols to cancel browser redirects, extract the raw streaming host, and play it directly on the native player.
* **GDrive Auto-Bypasser**: Automatically injects JavaScript on Google Drive confirmation pages to bypass prompt buttons and trigger direct downloads instantly.
* **Popup & Redirect Protection**: Suppresses browser JavaScript alert and confirm popups to prevent malicious page redirects.

### 🔑 Premium Key Verification Gating
The web companion layouts and browser sections are gated behind a secure, firebase-driven check:
* **Layout Hiding**: The entry layout (`mediaHolder`) containing movie/series actions is completely hidden (`View.GONE`) for standard users (and app store reviewers), showing only the local player features.
* **Firestore Verification**: Entering a valid premium key queries your Firebase Firestore document `PremiumKeyValue/Key` to unlock the layout dynamically and save configuration flags to `SharedPreferences`.
* **Dynamic Domain Resolving**: The streaming site's domain is loaded dynamically from Firestore at runtime rather than being hardcoded, preventing detection by static code analysis tools.

### 🎬 Powerful Native Video Player
The core playback engine utilizes Google's ExoPlayer, customized with premium options:
* **Tunneled Playback**: Enforce hardware tunneling for smooth 4K/HDR rendering with minimized battery draw.
* **Auto Frame-Rate Matching**: Dynamically adapts the device's display refresh rate to match the source video frame rate.
* **Auto Picture-in-Picture (PiP)**: Transitions automatically to PiP mode when navigating out of the application.
* **Skip Silence**: Detects and skips quiet sections in the audio stream automatically.
* **Multi-Format Support**: Comprehensive playback for HLS (`.m3u8`), DASH, Progressive streams (MP4, MKV, 3GP, MOV), and Dropbox URLs.

### 🎵 Background Music Player
* **System Service Integration**: Operates `AudioPlayerService` for background playback queue loops.
* **Audio Controllers**: Built-in support for playlists, system media controls, and equalizers.

### 📥 Media & Status Saver
* **WhatsApp Status Saver**: Direct directory hooks to download, share, and preview local status images and video files.
* **Instagram Downloader**: Story and post downloader with support for private accounts using persistent cookie jars.

---

## 🛠 Tech Stack

V-Troid is developed following structured Android architecture principles:

<div align="center">

| Category | Technologies |
| :--- | :--- |
| **Languages** | ![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white) ![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white) ![XML](https://img.shields.io/badge/XML-orange?style=flat-square) |
| **Media Player** | **ExoPlayer** (v2.17.1) |
| **Network & Parsing**| **Retrofit 3**, **OkHttp 5**, **jsoup 1.22.2**, **Volley**, RxJava 2 |
| **Database & Cache** | **SQLite** (`HistorySQLite`, `WListSQLite`), SharedPreferences |
| **UI Components** | **Material Design**, **Lottie**, **ArcSeekBar**, **Rubber Loader** |
| **Backend Integration**| **Firebase BoM** (Firestore, FCM / Cloud Messaging, Dynamic Links, Crashlytics, Auth) |
| **Tools** | **Gradle**, **Android Studio** |

</div>

---

## 📈 Repository Stats

<div align="center">

| **Commit Activity** | **Repo Size** |
| :---: | :---: |
| ![Commits](https://img.shields.io/github/commit-activity/m/gtxPrime/vtroid?style=for-the-badge&color=25D366) | ![Size](https://img.shields.io/github/repo-size/gtxPrime/vtroid?style=for-the-badge&color=blue) |

| **Top Language** | **Code Size** |
| :---: | :---: |
| ![Language](https://img.shields.io/github/languages/top/gtxPrime/vtroid?style=for-the-badge&color=blueviolet) | ![Code Size](https://img.shields.io/github/languages/code-size/gtxPrime/vtroid?style=for-the-badge&color=orange) |

</div>

---

## 🌟 Star History

<div align="center">
<a href="https://star-history.com/#gtxPrime/vtroid&Date">
  <img src="https://api.star-history.com/svg?repos=gtxPrime/vtroid&type=Date&theme=dark" alt="Star History Chart" />
</a>
</div>

---

## 🗺 Roadmap

- [ ] **Dynamic Web Ad-Blocker** - Hardened custom host blocking rule engine for WebView.
- [ ] **Cast Framework** - Enhanced Android Cast support for smart TVs.
- [ ] **Watchlist Sync** - Cloud-saved watch histories and movie bookmarks.
- [ ] **Material You UI** - Custom color palettes based on Android device system colors.

---

## 📚 Documentation

Detailed documentation on development configurations:
* **[🏛️ Architecture Guide](docs/ARCHITECTURE.md)** - Code directory setup and layers layout.
* **[🌐 Interception Engine](docs/INTERCEPTION.md)** - Walkthrough of URLs capturing and overriding filters.
* **[🔧 Setup & Build](docs/SETUP.md)** - Getting started with compilation and environment variables.

---

## <a id="-installation"></a>📥 Installation

To build and run V-Troid locally:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/gtxprime/vtroid.git
   cd vtroid
   ```
2. **Setup Credentials**:
   * Add your custom Firebase `google-services.json` inside the `/app` folder.
   * Provide local SDK path and signing properties in `local.properties` (see [docs/SETUP.md](docs/SETUP.md)).
3. **Build and Run**:
   ```bash
   ./gradlew assembleDebug
   ```

---

## <a id="-contributing"></a>🤝 How to Contribute

Contributions are welcome! If you want to submit a fix or feature:
1. **Fork** the repository.
2. Create your **Feature Branch** (`git checkout -b feature/AmazingFeature`).
3. Commit your **Changes** (`git commit -m 'Add some AmazingFeature'`).
4. Push to the **Branch** (`git push origin feature/AmazingFeature`).
5. Open a **Pull Request**.

---

## ⚖️ License

Distributed under the **MIT License**. See [`LICENSE`](./LICENSE) for full details.

---

<div align="center">
  <b>Built with ❤️ by the V-Troid Team</b><br/>
  <a href="https://github.com/gtxprime">GitHub</a>
</div>
