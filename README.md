# SecureVault - Android Contact Management App

SecureVault is a production-ready Android application that demonstrates secure authentication using **AppAuth** and local data persistence using **SQLite (Room)**. It features a modern, professional UI built with Material 3.

## ✨ Features

- **Secure Authentication**: Implements OAuth 2.0 with PKCE using the industry-standard `net.openid:appauth` library.
- **Modern UI/UX**: Clean, responsive design featuring Material 3 components, custom gradients, and bottom-sheet login integration.
- **Local Contact Management**: Full CRUD (Create, Read, Update, Delete) functionality for managing contacts.
- **Smart Search**: Instant filtering of contacts by name, phone, or email.
- **Persistent Storage**: Robust local data handling using the Room Persistence Library (SQLite).
- **Secure Token Storage**: Encrypted storage of authentication tokens using Android Keystore and `EncryptedSharedPreferences`.

## 🚀 Getting Started

### 1. Prerequisites
- Android Studio Ladybug or newer.
- Android SDK 34 (Compile SDK).
- A valid Identity Provider (like IdentityHub, Auth0, or Okta).

### 2. Configuration
Before building the app, you must configure your authentication settings in `app/src/main/java/com/example/secureauthapp/AuthConfig.kt`:

```kotlin
object AuthConfig {
    const val AUTHORITY_URL = "https://your-identity-provider.com"
    const val CLIENT_ID = "YOUR_CLIENT_ID_HERE"
    const val REDIRECT_URI = "com.example.securevault://callback"
    // ...
}
```

### 3. Identity Provider Setup
Ensure your Identity Provider dashboard is configured with the following Redirect URIs:
- **Redirect URI**: `com.example.securevault://callback`
- **Logout Redirect URI**: `com.example.securevault://callback`

## 🛠️ Build and Run

1. Clone the repository.
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. Click **Run** to deploy the app to your emulator or physical device.

## 🏛️ Architecture

The app follows the **MVVM (Model-View-ViewModel)** architectural pattern and uses the **Repository Pattern** to abstract data sources.

- **UI Layer**: Activities and Adapters using View Binding.
- **Data Layer**: Room Database, DAOs, and Repositories.
- **Auth Layer**: AuthManager handling the AppAuth flow and token lifecycle.

## 🔒 Security Best Practices
- **No Hardcoded Secrets**: Uses public client configuration suitable for mobile apps.
- **PKCE**: Enforces Proof Key for Code Exchange to prevent authorization code interception.
- **Custom Tabs**: Uses Chrome Custom Tabs for a secure, integrated login experience.
- **Local Encryption**: Sensitive data is encrypted at rest on the device.

## 📄 License
This project is open-source and available under the MIT License.
