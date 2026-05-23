# ⚙️ V-Troid Compilation & Setup Guide

This guide walks you through importing V-Troid into your development environment, configuring the necessary API keys and credentials, and building the project locally.

---

## 📋 Prerequisites
Before you start, make sure you have:
1. **Android Studio** (Koala or newer recommended).
2. **Android SDK** (Target API Level 37, compile SDK Level 36).
3. **Gradle JDK** configured to Java 17.

---

## 🛠️ Setup Steps

### 1. Import Project
Clone the repository and import the directory into Android Studio:
```bash
git clone https://github.com/gtxprime/vtroid.git
```
In Android Studio:
* File -> New -> **Import Project...**
* Select the root `vtroid` folder.

### 2. Configure Firebase (`google-services.json`)
V-Troid depends on Firebase for notifications, watch databases, and key checks.
1. Create a project at [Firebase Console](https://console.firebase.google.com/).
2. Register an Android Application with the package name `com.gtxprime.vtroid`.
3. Download the `google-services.json` file.
4. Move `google-services.json` into the `/app` folder (e.g. `/vtroid/app/google-services.json`).
   * *Note: A helper structure can be found at `app/google-services.json.template`.*

### 3. Keystore & Signing Properties
We use a decoupled credentials mechanism so passwords aren't saved in Gradle files.

Open or create [local.properties](file:///f:/Source%20Codes/V-Troid/local.properties) in the root of the project and add the signing configuration:
```properties
# Location of your local Android SDK
sdk.dir=/path/to/android/sdk

# Release keystore config
release.storeFile=/absolute/path/to/your/app_key.jks
release.storePassword=your_keystore_password
release.keyAlias=your_key_alias
release.keyPassword=your_alias_password
```
* **If these properties are missing**: Gradle will compile using dummy fallback properties. This allows local debugging and testing without needing the official release key.

---

## 💻 Build Commands

You can build the app via the command line or from within Android Studio:

### Debug Build
Compiles a debug APK:
```bash
./gradlew assembleDebug
```
* The output APK is saved to: `/app/build/outputs/apk/debug/app-debug.apk`.

### Release Build
Compiles and signs a release APK:
```bash
./gradlew assembleRelease
```
* The output APK is saved to: `/app/build/outputs/apk/release/app-release.apk`.
* *Requires your local release keystore configuration to be specified in `local.properties`.*
