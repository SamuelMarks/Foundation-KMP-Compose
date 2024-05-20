# Foundation (KMP-Compose Template)
## Presented by [Bridge Supplies](https://bridge.supplies)
### Active Development - W.I.P.

This is a biased [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html) [Compose](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-getting-started.html) project, targeting Android, iOS, and Desktop JVM (MacOS, Windows, Linux) platforms. It's intended to be used as a template to develop good-looking, decentralized, targeted-scope apps for commonly used platforms.

### Android
<img width="200" alt="android_scanner" src="/assets/android_scanner.png">
<img width="200" alt="android_settings" src="/assets/android_settings.png">

### iOS
<img width="200" alt="ios_qr" src="/assets/ios_qr.png">
<img width="200" alt="ios_settings" src="/assets/ios_settings.png">

### Desktop JVM
<img width="200" alt="macos_qr" src="/assets/macos_qr.png">
<img width="200" alt="macos_settings" src="/assets/macos_settings.png">

</br></br>

> [!TIP]
> We've pre-configured KMP-supporting libraries that work across all platforms, with few limitations. It is recommended you familiarize yourself with these libraries before developing with Foundation.

- UI
  - [Compose](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-getting-started.html)
  - [Material3](https://m3.material.io/)
- KotlinX
  - [Coroutines](https://github.com/Kotlin/kotlinx.coroutines)
  - [Serialization](https://github.com/Kotlin/kotlinx.serialization)
  - [DateTime](https://github.com/Kotlin/kotlinx-datetime)
- AndroidX
  - [ViewModels](https://developer.android.com/jetpack/androidx/releases/lifecycle)
  - [DataStore](https://developer.android.com/kotlin/multiplatform/datastore)
  - [Compose Navigation](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-navigation-routing.html)
- Dependency injection
  - [Koin](https://insert-koin.io/docs/reference/koin-mp/kmp/)
- QR code handling
  - [g0dkar's QRCode](https://github.com/g0dkar/qrcode-kotlin) (Android, iOS, Desktop - QR generation)
  - [Chaintech's QRKit](https://github.com/ChainTechNetwork/QRKitComposeMultiplatform) (Android, iOS - QR scanning)


## Project structure

* `/composeApp/src` contains all shared Compose Multiplatform applications code:
  - `build.gradle.kts` declares per-platform package dependencies and signing configurations
  - `/commonMain` is shared with all platforms.
    - `/composeResources` contains `.xml` resources like `strings.xml`
    - `App.kt` contains shared Compose UI with `Koin` and `Material`
    - `AppConfig.kt` declares supported platform features and `expect`-ed platform functions
    - `Color.kt` and `Theme.kt` contains theme information derived from the [Material Theme Builder](https://material-foundation.github.io/material-theme-builder/)
    - `DataStore.kt` and `DataRepository.kt` contain the `DataStore Preferences` `expect` implementation and wrapper
    - `FoundationTheme.kt` `expect`s a Theme definition for each platform
    - `MainViewModel.kt` contains the `ViewModel` and `DataStore` implementations injected with `Koin`
    - `Navigation.kt`/`HomeScreen.kt`/`ScannerScreen.kt`/`SettingsScreen.kt` contain Composable definitions for each screen
  - `/androidMain` is for Android-specific code.
    - `Foundation.android.kt` is the `Application` definition to initialize libraries (Koin)
    - `MainActivity.kt` displays the shared `App` Composable
    - `actual` implementations of `FoundationTheme`, `AppConfig`, `DataStore`, and `ScannerScreen`
  - `/iosMain` is for iOS-specific code.
    - `MainViewController.kt` initializes libraries (Koin) and displays shared `App` Composable
    - `actual` implementations of `FoundationTheme`, `AppConfig`, `DataStore`, and `ScannerScreen`
  - `/desktopMain` is for JVM-specific code.
    - `Foundation.desktop.kt` contains the `main()` class to initialize libraries (Koin) and display shared `App` Composable
     - `actual` implementations of `FoundationTheme`, `AppConfig`, `DataStore`, and `ScannerScreen`
* `/iosApp` contains the iOS Xcode project files.
  - `Config.xcconfig` declares iOS package and app name
  - `ContentView.swift` displays the `MainViewController`'s implementation of the shared `App` Composable
  - `iOSApp.swift` displays the `ContentView` as the `App` controller
  - `Info.plist` contains build definitions, permissions
* `/gradle` contains shared Gradle package versions
  - `libs.versions.toml` defines bulid info (package name, versionCode, versionName, supported Android SDKs) and package libraries


## Template cloning instructions

### Naming and versioning

> [!CAUTION]
> Apparently naming your app "Foundation" doesn't work on iOS due to a conflict with some Accessibility package, so it's called "Foundation_" there instead.

- Android
  - `libs.versions.toml`
    - `app-name`: App display name in launcher
    - `app-packageName`: package name
    - `app-versionCode`: build version code, usually date in `YYYYMMDD0` format
    - `app-versionName`: build version name, usually in `Major.Minor.Patch` notation
    - `android-minSdk`/`android-targetSdk`/`android-compileSdk`: supported Android API levels
  - `AndroidManifest.xml`
    - `android:name`: name of class containing the `Application` implementation (ex: ".Foundation" = `Foundation.android.kt`)
- iOS
  - `libs.versions.toml`
    - `app-packageName`: bundle ID
  - `/iosApp/Configuration/Config.xcconfig`: bundle ID (again), app name
  - `/iosApp/iosApp/Info.plist`: bundle ID (again), app name (again)
  - `/iosApp/iosApp.xcodeproj`: in Xcode, select `iosApp` > `Build Settings`
    - `Packaging` > `Product Name`: for app name ("Foundation_")
    - `Packaging` > `Product Bundle Identifier`: bundle ID
- Desktop
  - `libs.versions.toml`
    - `app-name`: display name on window
    - `app-packageName`: package name
    - `app-mainName`: name of class containing your `main()` function, also appears as MacOS menu item
    - `app-versionName`: build version name, usually in `Major.Minor.Patch` notation

### Icons
- Android
  - `composeApp/src/androidMain/kotlin/res/drawable`
    - `ic_launcher_foreground.xml`
    - `ic_launcher_background.xml`
    - `ic_launcher_monochrome.xml` ([if needed](https://developer.android.com/develop/ui/views/launch/icon_design_adaptive))
  - `composeApp/src/androidMain/kotlin/res/mipmap`
    - `ic_launcher.xml`
- iOS
  - `iosApp/Assets.xcassets/AppIcon.appiconset`
    - 1024x1024 `.png`
- Desktop - _TBD_
  - Mac
    - `.icns`
  - Windows
    - `.ico`
  - Linux
    - `.png`

### Running applications
- Android
  - `composeApp` run configuration pointed to `Foundation.composeApp.main` module
- iOS
  - `iosApp` run configuration using `iosApp.xcodeproj` project file
- Desktop
  - create `desktopRun` run configuration using `desktopRun -DmainClass=Foundation --quiet` run options
    - `DmainClass` should match `app-mainName` without the `.`

### Publishing
- Android
  - Create a `keystore.properties` file in the repository root directory for production signing. Do not check this file into version control, or your `upload-keystore.jks`
```
storeFile=/Users/Example/Documents/Keystores/upload-keystore.jks
storePassword=myStorePassword
keyAlias=upload
keyPassword=myKeyPassword
```
- iOS
  - _TBD_
- Desktop
  - _TBD_
