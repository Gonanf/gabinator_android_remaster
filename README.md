<p align="center">
  <img src="assets/banner.png" alt="gabinator_android_remaster" width="100%">
</p>

<h1 align="center">gabinator_android_remaster</h1>

<p align="center"><b>Android receiver for the Gabinator screen-sharing system — receives display frames from a desktop companion via USB Accessory or TCP socket.</b></p>

<p align="center">
  <img alt="estado" src="https://img.shields.io/badge/estado-prototipo-F5A623">
  <img alt="lenguaje" src="https://img.shields.io/badge/Kotlin-1.9-7F52FF">
  <img alt="licencia" src="https://img.shields.io/badge/licencia-privado-blue">
  <img alt="ultima actividad" src="https://img.shields.io/badge/ultima_actividad-2025--05-lightgrey">
</p>

---

## What it is

An Android app that acts as the display endpoint for the Gabinator screen-sharing system. It connects to a desktop machine running `Gabinator_Desktop` (a separate companion app) over USB Accessory mode or TCP socket, receives compressed image frames, and renders them fullscreen in landscape mode.

The app supports two connection modes:
- **USB Accessory** — direct cable connection using Android's USB Accessory API
- **TCP** — network connection to a user-specified IP and port

**In short:** it turns an Android device into a secondary display fed by a desktop app.

## State

| | |
|---|---|
| **State** | Prototype |
| **Last activity** | 2025-05 |
| **Can it be used today** | No — the code compiles but has hardcoded debug values, no error recovery, and the desktop companion is not bundled or documented here |
| **What's missing** | Connection resilience, proper frame protocol, error handling, UI polish, desktop companion source/docs |
| **Known risks / tech debt** | Global mutable state for streams and permissions, byte-by-byte TCP reads (unbuffered), AndroidManifest.xml has a stray `q` character on line 61, no tests |

## Why it exists

This is the second iteration of an Android screen-sharing project. It was built to explore USB Accessory mode and TCP-based image transfer between a desktop and an Android device — likely as a course project or personal experiment.

## Demo

No demo available. The app requires the Gabinator_Desktop companion running on a connected machine, which is not included in this repo.

## Installation and use

**Requirements:**
- Android Studio (AGP 8.7.2)
- Android SDK 34 (min SDK 16)
- A device with USB Accessory support (for USB mode)
- The Gabinator_Desktop companion app running on the connected machine

```bash
# Clone
git clone https://github.com/Gonanf/gabinator_android_remaster.git
cd gabinator_android_remaster

# Build
./gradlew assembleDebug
```

```bash
# Install on connected device
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Stack

- **Language / runtime:** Kotlin 1.9, JVM target 1.8
- **Android SDK:** compileSdk 34, minSdk 16, targetSdk 34
- **Key dependencies:** Ktor 1.6 (TCP client/server), AndroidX (AppCompat, Material, Navigation, ConstraintLayout), ViewBinding
- **Build:** Gradle with version catalog (`libs.versions.toml`), AGP 8.7.2
- **CI:** GitHub Actions (`.github/workflows/android.yml`)

## Architecture

```
Main (menu) ──┬── USB mode ──→ ImageView (USB)
              ├── TCP mode ──→ Tcp_settings ──→ TCP_ImageView (TCP)
              └── Log mode ──→ Log_View
```

- **Main** — Entry point. Detects USB accessories, requests permission, routes to connection mode.
- **Tcp_settings** — Collects IP/port from user, opens a TCP socket.
- **ImageView** — Reads image data from USB accessory stream, decodes bitmap, displays fullscreen.
- **TCP_ImageView** — Reads framed image data from TCP socket, decodes bitmap, displays.
- **Log_View** — Displays the in-memory debug log.
- **Global state** — Stream references (`input_stream`, `socket`), permission flag (`permisos`), and log string (`LOG1`) are module-level globals shared across activities.

## Repo structure

```
app/src/main/java/com/chaos/gabinator_android/
  Main.kt             # Entry activity, USB permission handling, menu routing
  ImageView.kt        # USB image display (reads from USB accessory stream)
  TCP_ImageView.kt    # TCP image display (reads from socket, framed protocol)
  Tcp_settings.kt     # TCP connection settings (IP/port input)
  Log_View.kt         # Debug log viewer

app/src/main/res/layout/
  menu.xml            # Main menu (TCP/USB/LOG buttons)
  activity_tcp_image_view.xml  # Image display (shared by USB and TCP modes)
  settings_activity.xml        # TCP settings form
  activity_log_view.xml        # Log viewer

app/src/main/AndroidManifest.xml  # App manifest, activity declarations
build.gradle.kts                  # Root build config
app/build.gradle.kts              # App build config, dependencies
gradle/libs.versions.toml        # Version catalog
```

## Roadmap

- [ ] Remove global mutable state; use proper Android lifecycle patterns
- [ ] Implement a proper frame protocol (header with size, checksum)
- [ ] Add connection error handling and reconnection logic
- [ ] Buffer TCP reads (current byte-by-byte approach is extremely slow)
- [ ] Clean up AndroidManifest.xml (stray character on line 61)
- [ ] Add input validation on TCP settings
- [ ] Document the Gabinator_Desktop companion or bundle its source
- [x] Basic USB Accessory image transfer working
- [x] Basic TCP image transfer working
- [x] Debug log viewer

## Notes and decisions

- **USB Accessory mode** was chosen over USB Host for broader device compatibility — many Android devices support accessory mode but not host mode.
- **Ktor** was used for TCP networking, though the current usage is minimal (raw `java.net.Socket` in practice). Ktor's async features aren't leveraged.
- **Global mutable state** was used for simplicity during prototyping. Activities share stream references and permission flags through module-level variables — this works but is fragile and makes the app hard to test or extend.
- **Frame protocol** is ad-hoc: USB mode reads until the stream pauses; TCP mode reads a size prefix then that many bytes. Neither has checksums or error recovery.

## License

No license specified. Without a license, the code is visible but not legally open source — default copyright applies.

---

*Original author: Gonanf — https://github.com/Gonanf/gabinator_android_remaster*
