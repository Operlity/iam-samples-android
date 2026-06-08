package com.example.secureauthapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.secureauthapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private lateinit var authManager: AuthManager
    
    private val authLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        if (data != null) {
            handleAuthorizationResponse(data)
        } else {
            binding.tvStatus.text = "Login cancelled"
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.d("AuthDebug", "LoginActivity onCreate")
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        authManager = (application as AuthApplication).authManager
        
        setupClickListeners()
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        android.util.Log.d("AuthDebug", "LoginActivity onNewIntent")
    }
    
    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            performLogin()
        }
    }
    
    private fun performLogin() {
        binding.tvStatus.text = "Opening IdentityHub..."
        authManager.login(authLauncher)
    }
    
    private fun handleAuthorizationResponse(intent: Intent?) {
        android.util.Log.d("AuthDebug", "handleAuthorizationResponse called")
        
        // Update UI to show we are finishing the login
        binding.tvTitle.text = "Finalizing..."
        binding.tvSubtitle.text = "Setting up your secure vault"
        binding.btnLogin.visibility = android.view.View.GONE
        binding.tvStatus.text = "Verifying identity..."

        intent?.let {
            authManager.handleAuthorizationResponse(
                it,
                onSuccess = { accessToken ->
                    android.util.Log.d("AuthDebug", "Login successful, navigating to ProfileActivity")
                    showToast("Login successful")
                    navigateToHome()
                },
                onError = { error ->
                    android.util.Log.e("AuthDebug", "Login failed: $error")
                    // Restore UI if failed
                    binding.tvTitle.text = "SecureVault"
                    binding.tvSubtitle.text = "Your secure identity portal"
                    binding.btnLogin.visibility = android.view.View.VISIBLE
                    binding.tvStatus.text = "Login failed. Please try again."
                    showToast("Login failed: $error")
                }
            )
        } ?: android.util.Log.e("AuthDebug", "Intent is null in handleAuthorizationResponse")
    }
    
    private fun navigateToHome() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        finish()
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
