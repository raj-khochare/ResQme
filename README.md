# Rescue Guide

Emergency Response Application for Android

![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

---

## About

Rescue Guide is an Android application that provides quick access to emergency services, life-saving guides, and SOS alerts during critical situations.

## Features

**Emergency Services**
- One-tap emergency calling
- Quick access to Police, Fire, and Medical services
- Sample emergency numbers for testing

**Rescue Guides**
- CPR basics and life-saving techniques
- Fire extinguisher usage guide
- Earthquake response procedures
- Water rescue basics
- Categorized by Medical, Fire Safety, and Natural Disasters

**Emergency Contacts**
- Add and manage trusted emergency contacts
- Quick access during emergencies
- SMS alerts with location sharing

**Modern Interface**
- Clean and intuitive design
- Dark and Light theme support
- Material Design 3

---

## Screenshots

| Home Screen | Emergency | Guides | Profile |
|-------------|-----------|--------|---------|
| Splash screen with app logo | SOS button and emergency services | Categorized rescue guides | User profile and settings |

---

## Installation

### For Users

Download the APK from the releases page and install on your Android device.

### For Developers
```bash
git clone https://github.com/yourusername/rescue-guide.git
cd rescue-guide
```

Open the project in Android Studio and run on your device or emulator.

---

## Requirements

- Android 7.0 (API 24) or higher
- Location services enabled
- SMS and Phone call permissions

---

## Permissions

| Permission | Purpose |
|------------|---------|
| `CALL_PHONE` | Make emergency calls |
| `SEND_SMS` | Send SOS messages |
| `ACCESS_FINE_LOCATION` | Share location during emergencies |
| `VIBRATE` | Provide haptic feedback |

---

## Tech Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Architecture:** MVVM
- **Database:** Room
- **Dependency Injection:** Hilt
- **Navigation:** Jetpack Navigation Compose

---

## Building
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

---

## Project Structure
```
rescue-guide/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/mnp/rescueguide/
│   │   │   │   ├── data/
│   │   │   │   ├── ui/
│   │   │   │   ├── viewmodel/
│   │   │   │   └── MainActivity.kt
│   │   │   └── res/
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
└── build.gradle.kts
```

---

## Contributing

Contributions are welcome. Please follow these steps:

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/NewFeature`)
3. Commit your changes (`git commit -m 'Add NewFeature'`)
4. Push to the branch (`git push origin feature/NewFeature`)
5. Open a Pull Request

---

## Roadmap

- Multi-language support
- Offline guide access
- Voice-activated emergency calling
- Integration with wearable devices
- More rescue guide categories

---

## License

This project is licensed under the MIT License.

---

## Disclaimer

This application is intended to assist in emergencies but should not replace official emergency services. Always contact local authorities directly in life-threatening situations.

---

## Contact

For issues or questions, create an issue in this repository.

GitHub: [@raj-khochare](https://github.com/raj-khochare)

---

Made with care for safety and security
