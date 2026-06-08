package com.example.secureauthapp

object AuthConfig {
    // IdentityHub endpoints
    const val AUTHORITY_URL = "https://ogsiamapp.azurewebsites.net"
    const val AUTHORIZATION_ENDPOINT = "$AUTHORITY_URL/connect/authorize"
    const val TOKEN_ENDPOINT = "$AUTHORITY_URL/connect/token"
    const val END_SESSION_ENDPOINT = "$AUTHORITY_URL/connect/endsession"
    const val USERINFO_ENDPOINT = "$AUTHORITY_URL/connect/userinfo"

    // Updated Client ID from your IdentityHub dashboard
    const val CLIENT_ID = "551b1ff7-d7f5-46fd-960f-e32cdfed81de"

    // Redirect URI - Matches the Native Application configuration
    const val REDIRECT_URI = "com.example.securevault://callback"

    // Post logout redirect
    const val POST_LOGOUT_REDIRECT_URI = "com.example.securevault://callback"

    // Scopes
    const val SCOPE_OPENID = "openid"
    const val SCOPE_PROFILE = "profile"
    const val SCOPE_EMAIL = "email"
    const val SCOPE_OFFLINE_ACCESS = "offline_access"

    val SCOPES = arrayOf(SCOPE_OPENID, SCOPE_PROFILE, SCOPE_EMAIL, SCOPE_OFFLINE_ACCESS)
}
