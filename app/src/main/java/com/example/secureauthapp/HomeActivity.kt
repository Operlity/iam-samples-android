package com.example.secureauthapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.secureauthapp.data.model.Resource
import com.example.secureauthapp.data.remote.RetrofitClient
import com.example.secureauthapp.data.repository.UserRepository
import com.example.secureauthapp.databinding.ActivityHomeBinding
import com.example.secureauthapp.ui.HomeViewModel
import com.example.secureauthapp.ui.HomeViewModelFactory

class HomeActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityHomeBinding
    private lateinit var authManager: AuthManager
    private lateinit var viewModel: HomeViewModel
    
    private val logoutLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        navigateToLogin()
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        authManager = (application as AuthApplication).authManager
        
        // Check if user is authenticated
        if (!authManager.isAuthenticated()) {
            navigateToLogin()
            return
        }
        
        // Initialize ViewModel with Repository
        val userRepository = UserRepository(RetrofitClient.apiService)
        val viewModelFactory = HomeViewModelFactory(userRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
        
        setupObservers()
        setupClickListeners()
        
        // Fetch user info from IdentityHub
        loadUserInfo()
    }
    
    private fun setupObservers() {
        viewModel.userInfo.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    resource.data?.let { userInfo ->
                        displayUserInfo(userInfo)
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    binding.tvStatus.text = "Error: ${resource.message}"
                    showToast("Failed to load user info")
                }
            }
        }
    }
    
    private fun displayUserInfo(userInfo: com.example.secureauthapp.data.model.UserInfo) {
        binding.tvWelcome.text = "Welcome, ${userInfo.name ?: userInfo.preferredUsername ?: "User"}!"
        
        val userDetails = buildString {
            append("User Information:\n\n")
            userInfo.email?.let { append("Email: $it\n") }
            userInfo.preferredUsername?.let { append("Username: $it\n") }
            userInfo.givenName?.let { append("First Name: $it\n") }
            userInfo.familyName?.let { append("Last Name: $it\n") }
            userInfo.sub?.let { append("\nUser ID: $it\n") }
        }
        
        binding.tvUserDetails.text = userDetails
        
        authManager.getAccessToken()?.let { token ->
            binding.tvTokenPreview.text = "Token: ${token.take(50)}..."
        }
    }
    
    private fun loadUserInfo() {
        authManager.getAccessToken()?.let { accessToken ->
            viewModel.fetchUserInfo(accessToken)
        } ?: run {
            binding.tvStatus.text = "No access token available"
            showToast("Please login again")
            navigateToLogin()
        }
    }
    
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.tvStatus.text = if (isLoading) "Loading user information..." else ""
    }
    
    private fun setupClickListeners() {
        binding.btnRefreshToken.setOnClickListener {
            refreshToken()
        }
        
        binding.btnRefreshUserInfo.setOnClickListener {
            loadUserInfo()
        }
        
        binding.btnLogout.setOnClickListener {
            performLogout()
        }
    }
    
    private fun refreshToken() {
        binding.tvStatus.text = "Refreshing token..."
        authManager.refreshAccessToken(
            onSuccess = { accessToken ->
                binding.tvStatus.text = "Token refreshed successfully"
                binding.tvTokenPreview.text = "Token: ${accessToken.take(50)}..."
                showToast("Token refreshed")
                
                // Reload user info with new token
                loadUserInfo()
            },
            onError = { error ->
                binding.tvStatus.text = "Token refresh failed"
                showToast("Token refresh failed: $error")
            }
        )
    }
    
    private fun performLogout() {
        authManager.logout(logoutLauncher)
    }
    
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
