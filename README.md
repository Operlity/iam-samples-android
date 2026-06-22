# SecureVault - Android Contact Management App

SecureVault is a production-ready, secure Android application that demonstrates authorization using **AppAuth-Android** (OAuth 2.0 with PKCE) and local data persistence using **Room (SQLite)**. It features a modern, professional UI built with Material 3 components.

---

## ✨ Features

- 🔐 **Secure Authentication**: Implements OAuth 2.0 with PKCE (Proof Key for Code Exchange) using the industry-standard `net.openid:appauth` library.
- 🎨 **Modern Material 3 UI/UX**: Clean, responsive design featuring Material 3 components, custom gradients, dynamic state representations, and elegant splash/login pages.
- 📂 **Local Contact Management**: Complete CRUD (Create, Read, Update, Delete) workflow for local contacts.
- 🔍 **Instant Search**: Live filtering of contacts by name, phone number, or email.
- 💾 **Robust Persistence**: SQLite interaction abstracted via Room Persistence Library, using Kotlin Coroutines and Flows for reactive UI updates.
- 🔑 **Encrypted Token Storage**: Tokens are stored securely using Android Keystore and `EncryptedSharedPreferences` to prevent credential exposure.

---

## 🏛️ Architecture

The project follows clean architecture principles, utilizing the **MVVM (Model-View-ViewModel)** design pattern coupled with the **Repository Pattern** to separate concerns:

```
com.example.secureauthapp
│
├── data/
│   ├── local/        # Room Database, Contact Entity, and DAO
│   ├── model/        # Data models (UserInfo, Resource states)
│   ├── remote/       # Remote API definitions (Retrofit, UserInfo endpoint)
│   └── repository/   # Repository abstraction layer coordinating local & remote data
│
└── ui/               # UI Layer (Activities, ViewModels, View Binding adapters)
```

- **UI Layer**: Activities observe LiveData/StateFlow from ViewModels. View Binding is used for type-safe layout interaction.
- **Domain/Data Layer**: Repositories act as the single source of truth, managing interactions between the local Room DB and remote API services.
- **Auth Layer**: Handled by a centralized `AuthManager`, encapsulating AppAuth configuration, auth state lifecycle, and token refreshes.

---

## 🚀 Getting Started

### 1. Prerequisites
- **Android Studio Ladybug** (or newer).
- **Android SDK 34** (Compile & Target SDK).
- **JDK 17** configured in Android Studio settings.
- **An OIDC/OAuth 2.0 Identity Provider (IdP)** (e.g., IdentityHub, Okta, Auth0, or Keycloak).

---

## 🛠️ Step-by-Step Setup & Configuration

To run the app successfully, you must register the client in your Identity Provider (IdP) and configure the app redirect schemes.

### Step A: Identity Provider Client Registration
Create a client in your IdP console with the following parameters:
- **Client Type**: `Public Client` (Native / Mobile App)
- **Allowed Grant Types**: `Authorization Code`
- **Proof Key for Code Exchange (PKCE)**: Enabled (using `S256` challenge method)
- **Allowed Redirect URIs**: `com.example.securevault://callback`
- **Allowed Post-Logout Redirect URIs**: `com.example.securevault://callback`
- **Scopes**: `openid`, `profile`, `email`, `offline_access` (for refresh tokens)

---

### Step B: Syncing the Redirect Scheme in the Android App
For security reasons, AppAuth uses Android Intent Filters to capture the authorization code redirection. If you change your redirect scheme from the default `com.example.securevault`, you **must** update the configuration in three places:

#### 1. Configuration Class: [AuthConfig.kt](file:///d:/Android%20App/app/src/main/java/com/example/secureauthapp/AuthConfig.kt)
Update the endpoints and Client ID matching your IdP:
```kotlin
object AuthConfig {
    const val AUTHORITY_URL = "https://your-identity-provider.com"
    const val AUTHORIZATION_ENDPOINT = "$AUTHORITY_URL/connect/authorize"
    const val TOKEN_ENDPOINT = "$AUTHORITY_URL/connect/token"
    const val END_SESSION_ENDPOINT = "$AUTHORITY_URL/connect/endsession"
    const val USERINFO_ENDPOINT = "$AUTHORITY_URL/connect/userinfo"

    const val CLIENT_ID = "YOUR_CLIENT_ID_HERE"
    const val REDIRECT_URI = "com.example.securevault://callback"
    const val POST_LOGOUT_REDIRECT_URI = "com.example.securevault://callback"
}
```

#### 2. App Build Gradle: [app/build.gradle](file:///d:/Android%20App/app/build.gradle)
Under `defaultConfig`, ensure `appAuthRedirectScheme` matches your redirect URI's custom scheme:
```groovy
defaultConfig {
    applicationId "com.example.secureauthapp"
    minSdk 23
    targetSdk 34
    
    manifestPlaceholders = [
        "appAuthRedirectScheme": "com.example.securevault"
    ]
}
```

#### 3. App Manifest: [AndroidManifest.xml](file:///d:/Android%20App/app/src/main/AndroidManifest.xml)
Ensure the intent-filter inside `RedirectUriReceiverActivity` is configured to catch the matching scheme and host:
```xml
<activity
    android:name="net.openid.appauth.RedirectUriReceiverActivity"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data
            android:scheme="com.example.securevault"
            android:host="callback" />
    </intent-filter>
</activity>
```

---

## 🗄️ Database Schema (Room)

Local data is stored in `secure_vault_database` using Room. The core table is `contacts`:

| Column Field | Data Type | Constraint | Description |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | `PRIMARY KEY AUTOINCREMENT` | Unique identifier |
| `name` | `TEXT` | `NOT NULL` | Contact display name |
| `phoneNumber` | `TEXT` | `NOT NULL` | Contact phone number |
| `email` | `TEXT` | `NOT NULL` | Contact email address |
| `company` | `TEXT` | `NULLABLE` | Company or organization |
| `createdAt` | `INTEGER` | `NOT NULL` | Epoch millisecond timestamp |

---

## ⚙️ Building & Running

### Option 1: Via Android Studio (Recommended)
1. Open Android Studio.
2. Select **File > Open** and choose the project root folder.
3. Wait for the Gradle Sync to complete.
4. Select your target device/emulator and click the **Run** button (or press `Shift + F10`).

### Option 2: Via Command Line (Gradle Wrapper)
Make sure you are in the project root directory.

- **On Windows**:
  ```powershell
  .\gradlew.bat assembleDebug
  ```
- **On macOS / Linux**:
  ```bash
  chmod +x gradlew
  ./gradlew assembleDebug
  ```
The compiled APK will be available under `app/build/outputs/apk/debug/app-debug.apk`.

---

## 📱 Usage Flow

1. **Splash Screen ([SplashActivity](file:///d:/Android%20App/app/src/main/java/com/example/secureauthapp/SplashActivity.kt))**:
   - The app boots and checks if user tokens are saved in secure storage.
   - If a valid token exists, it routes directly to `ContactListActivity`. Otherwise, it routes to `LoginActivity`.
2. **Login Screen ([LoginActivity](file:///d:/Android%20App/app/src/main/java/com/example/secureauthapp/LoginActivity.kt))**:
   - Features a clean, Material 3 login layout.
   - Tapping "Log In" initiates the AppAuth workflow, opening a secure Chrome Custom Tab to perform OIDC authorization.
3. **Contact Manager ([ContactListActivity](file:///d:/Android%20App/app/src/main/java/com/example/secureauthapp/ui/contact/ContactListActivity.kt))**:
   - Displays all persisted contacts in a scrollable list.
   - Allows users to search contacts using the top search bar.
   - Users can add, edit, or swipe to delete contacts.
4. **Profile View ([ProfileActivity](file:///d:/Android%20App/app/src/main/java/com/example/secureauthapp/ProfileActivity.kt))**:
   - Fetches profile info (e.g., name, email) from the Identity Provider's UserInfo endpoint using the OAuth access token.
   - Initiates OIDC logout to revoke local sessions and redirect back.

---

## 🔒 Security Implementations

- **Proof Key for Code Exchange (PKCE)**: Mandates dynamic code verifiers and challenges to prevent authorization code interception attacks.
- **Custom Tabs Integration**: Login runs within browser-isolated Chrome Custom Tabs, protecting credentials from keylogging or interception by the host app.
- **Keystore Encryption**: Auth tokens are encrypted at rest using Android Cryptographic Provider (`EncryptedSharedPreferences`), preventing root access extractions.
- **Strict Network Routing**: Custom cleartext configs enforce HTTPS connections unless configured otherwise for local debug hosts.

---

## 📄 License
This project is open-source and available under the [MIT License](file:///d:/Android%20App/LICENSE).
