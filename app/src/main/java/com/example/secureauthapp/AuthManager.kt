package com.example.secureauthapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.browser.customtabs.CustomTabsIntent
import net.openid.appauth.*
import org.json.JSONException

class AuthManager(private val context: Context) {
    
    private val tokenStorage = SecureTokenStorage(context)
    private var authState: AuthState? = null
    
    init {
        loadAuthState()
    }
    
    private fun getServiceConfiguration(): AuthorizationServiceConfiguration {
        return AuthorizationServiceConfiguration(
            Uri.parse(AuthConfig.AUTHORIZATION_ENDPOINT),
            Uri.parse(AuthConfig.TOKEN_ENDPOINT),
            null,
            Uri.parse(AuthConfig.END_SESSION_ENDPOINT)
        )
    }
    
    fun login(launcher: ActivityResultLauncher<Intent>) {
        val serviceConfig = getServiceConfiguration()
        
        // Build authorization request with PKCE
        val authRequest = AuthorizationRequest.Builder(
            serviceConfig,
            AuthConfig.CLIENT_ID,
            ResponseTypeValues.CODE,
            Uri.parse(AuthConfig.REDIRECT_URI)
        ).apply {
            setScope(AuthConfig.SCOPES.joinToString(" "))
        }.build()

        // Customize the browser look to match the app (Modernized UI)
        val customTabsIntent = CustomTabsIntent.Builder()
            .setToolbarColor(android.graphics.Color.parseColor("#1976D2")) // Matches your blue theme
            .setShowTitle(false) // Hides the page title for a cleaner look
            .build()
        
        val authService = AuthorizationService(context)
        val authIntent = authService.getAuthorizationRequestIntent(authRequest, customTabsIntent)
        
        launcher.launch(authIntent)
    }
    
    fun handleAuthorizationResponse(
        intent: Intent,
        onSuccess: (accessToken: String) -> Unit,
        onError: (error: String) -> Unit
    ) {
        val authResponse = AuthorizationResponse.fromIntent(intent)
        val authException = AuthorizationException.fromIntent(intent)
        
        if (authResponse != null) {
            authState = AuthState(authResponse, authException)
            
            // Exchange authorization code for tokens
            val authService = AuthorizationService(context)
            authService.performTokenRequest(authResponse.createTokenExchangeRequest()) { tokenResponse, tokenException ->
                authState?.update(tokenResponse, tokenException)
                
                if (tokenResponse != null) {
                    // Store tokens securely
                    tokenResponse.accessToken?.let { tokenStorage.saveAccessToken(it) }
                    tokenResponse.refreshToken?.let { tokenStorage.saveRefreshToken(it) }
                    tokenResponse.idToken?.let { tokenStorage.saveIdToken(it) }
                    saveAuthState()
                    
                    onSuccess(tokenResponse.accessToken ?: "")
                } else {
                    onError(tokenException?.message ?: "Token exchange failed")
                }
                
                authService.dispose()
            }
        } else {
            onError(authException?.message ?: "Authorization failed")
        }
    }
    
    fun refreshAccessToken(
        onSuccess: (accessToken: String) -> Unit,
        onError: (error: String) -> Unit
    ) {
        val currentAuthState = authState ?: run {
            onError("Not authenticated")
            return
        }
        
        val authService = AuthorizationService(context)
        currentAuthState.performActionWithFreshTokens(authService) { accessToken, _, exception ->
            if (accessToken != null) {
                tokenStorage.saveAccessToken(accessToken)
                saveAuthState()
                onSuccess(accessToken)
            } else {
                onError(exception?.message ?: "Token refresh failed")
            }
            authService.dispose()
        }
    }
    
    fun logout(
        launcher: ActivityResultLauncher<Intent>
    ) {
        val serviceConfig = getServiceConfiguration()
        val idToken = tokenStorage.getIdToken()
        
        val endSessionRequest = EndSessionRequest.Builder(serviceConfig)
            .setPostLogoutRedirectUri(Uri.parse(AuthConfig.POST_LOGOUT_REDIRECT_URI))
            .apply {
                idToken?.let { setIdTokenHint(it) }
            }
            .build()
            
        val authService = AuthorizationService(context)
        val endSessionIntent = authService.getEndSessionRequestIntent(endSessionRequest)
        
        // Clear local tokens now
        tokenStorage.clearAllTokens()
        authState = null
        
        launcher.launch(endSessionIntent)
        authService.dispose()
    }
    
    fun getAccessToken(): String? {
        return tokenStorage.getAccessToken()
    }
    
    fun isAuthenticated(): Boolean {
        return authState?.isAuthorized == true && tokenStorage.getAccessToken() != null
    }
    
    private fun saveAuthState() {
        authState?.let { state ->
            tokenStorage.saveAuthState(state.jsonSerializeString())
        }
    }
    
    private fun loadAuthState() {
        tokenStorage.getAuthState()?.let { stateJson ->
            try {
                authState = AuthState.jsonDeserialize(stateJson)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
}
