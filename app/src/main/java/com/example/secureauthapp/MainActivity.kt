package com.example.secureauthapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.secureauthapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var authManager: AuthManager
    
    private val authLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleAuthorizationResponse(intent)
    }

    private val logoutLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        updateUI()
        showToast("Logged out successfully")
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        authManager = (application as AuthApplication).authManager
        
        updateUI()
        setupClickListeners()
        
        // Handle redirect from browser
        handleAuthorizationResponse(intent)
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleAuthorizationResponse(it) }
    }
    
    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            performLogin()
        }
        
        binding.btnRefreshToken.setOnClickListener {
            refreshToken()
        }
        
        binding.btnLogout.setOnClickListener {
            performLogout()
        }
    }
    
    private fun performLogin() {
        binding.tvStatus.text = "Logging in..."
        authManager.login(authLauncher)
    }
    
    private fun handleAuthorizationResponse(intent: Intent?) {
        intent?.let {
            authManager.handleAuthorizationResponse(
                it,
                onSuccess = { accessToken ->
                    binding.tvStatus.text = "Logged in successfully"
                    binding.tvToken.text = "Access Token: ${accessToken.take(50)}..."
                    updateUI()
                    showToast("Login successful")
                },
                onError = { error ->
                    binding.tvStatus.text = "Login failed: $error"
                    updateUI()
                    showToast("Login failed: $error")
                }
            )
        }
    }
    
    private fun refreshToken() {
        binding.tvStatus.text = "Refreshing token..."
        authManager.refreshAccessToken(
            onSuccess = { accessToken ->
                binding.tvStatus.text = "Token refreshed"
                binding.tvToken.text = "Access Token: ${accessToken.take(50)}..."
                showToast("Token refreshed")
            },
            onError = { error ->
                binding.tvStatus.text = "Token refresh failed: $error"
                showToast("Token refresh failed: $error")
            }
        )
    }
    
    private fun performLogout() {
        authManager.logout(logoutLauncher)
    }
    
    private fun updateUI() {
        val isAuthenticated = authManager.isAuthenticated()
        
        binding.btnLogin.isEnabled = !isAuthenticated
        binding.btnRefreshToken.isEnabled = isAuthenticated
        binding.btnLogout.isEnabled = isAuthenticated
        
        if (isAuthenticated) {
            binding.tvStatus.text = "Authenticated"
            authManager.getAccessToken()?.let { token ->
                binding.tvToken.text = "Access Token: ${token.take(50)}..."
            }
        } else {
            binding.tvStatus.text = "Not authenticated"
            binding.tvToken.text = ""
        }
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
