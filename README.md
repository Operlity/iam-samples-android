# Secure Auth App

Android application implementing OAuth 2.0 authentication with best security practices.

## Features

✅ **AppAuth for Android** - Industry-standard OAuth 2.0 library  
✅ **Authorization Code + PKCE** - Secure public client flow  
✅ **Custom Tabs** - No embedded WebViews  
✅ **Secure Token Storage** - Android Keystore + EncryptedSharedPreferences  
✅ **Proper Logout** - Clears tokens and browser session  
✅ **No Hardcoded Secrets** - Public client configuration  

## Configuration

Before running the app, update the configuration in **AuthConfig.kt**:

```kotlin
object AuthConfig {
    const val AUTHORITY_URL = "https://your-identity-hub.com"
    const val CLIENT_ID = "your_client_id"
    const val REDIRECT_URI = "com.example.secureauthapp://callback"
    // ... other settings
}
```

## Security Best Practices

### ✓ AppAuth Library
Uses the recommended net.openid:appauth library for OAuth 2.0 and OpenID Connect.

### ✓ Authorization Code + PKCE
- No client secret (public client)
- PKCE (Proof Key for Code Exchange) enabled by default
- Protects against authorization code interception

### ✓ Custom Tabs (No WebView)
- Uses browser/Custom Tabs for authentication
- Avoids embedded WebViews which can be insecure
- User sees the actual identity provider URL

### ✓ Secure Token Storage
- **EncryptedSharedPreferences** - Application-level encryption
- **Android Keystore** - Hardware-backed key storage
- Access tokens, refresh tokens, and ID tokens encrypted at rest

### ✓ Proper Logout
1. Opens browser to /connect/endsession endpoint
2. Clears access token from secure storage
3. Clears refresh token from secure storage
4. Clears local session state

### ✓ No Hardcoded Secrets
- Public client only
- No client secret in code or resources
- All sensitive configuration externalized

## Flow

```
Android App → Browser/Custom Tab → IdentityHub
     ↓              ↓                    ↓
  Login      Authorization         Token Exchange
     ↓              ↓                    ↓
  Store      Secure Storage        Encrypted Prefs
```

## Requirements

- Android API 23+ (Android 6.0 Marshmallow)
- Kotlin 1.9+
- Gradle 8.1+

## Build

```bash
./gradlew build
```

## Run

```bash
./gradlew installDebug
```

## Dependencies

- **AppAuth**: net.openid:appauth:0.11.1
- **Security Crypto**: androidx.security:security-crypto:1.1.0-alpha06
- **Browser**: androidx.browser:browser:1.7.0

## Project Structure

```
app/
├── AuthApplication.kt          # Application class
├── AuthManager.kt              # Main authentication logic
├── AuthConfig.kt               # Configuration (update this!)
├── SecureTokenStorage.kt       # Encrypted token storage
├── MainActivity.kt             # UI and user interactions
└── res/
    ├── layout/
    │   └── activity_main.xml   # Main UI layout
    └── values/
        └── strings.xml         # String resources
```

## Usage

### Login
```kotlin
authManager.login(authLauncher)
```

### Refresh Token
```kotlin
authManager.refreshAccessToken(
    onSuccess = { accessToken -> /* use token */ },
    onError = { error -> /* handle error */ }
)
```

### Logout
```kotlin
authManager.logout {
    // Logout complete
}
```

### Get Access Token
```kotlin
val token = authManager.getAccessToken()
```

### Check Authentication Status
```kotlin
if (authManager.isAuthenticated()) {
    // User is authenticated
}
```

## License

This project is provided as a template for implementing secure OAuth 2.0 authentication in Android apps.
