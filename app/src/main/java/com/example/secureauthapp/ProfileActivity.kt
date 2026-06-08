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
import com.example.secureauthapp.databinding.ActivityProfileBinding
import com.example.secureauthapp.ui.HomeViewModel
import com.example.secureauthapp.ui.HomeViewModelFactory

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var authManager: AuthManager
    private lateinit var viewModel: HomeViewModel

    private val logoutLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        navigateToLogin()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authManager = (application as AuthApplication).authManager

        // Initialize ViewModel (reusing HomeViewModel for now as it handles user info)
        val userRepository = UserRepository(RetrofitClient.apiService)
        val viewModelFactory = HomeViewModelFactory(userRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        setupObservers()
        setupClickListeners()

        // Load user data
        loadUserInfo()
    }

    private fun setupObservers() {
        viewModel.userInfo.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.profileProgressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.profileProgressBar.visibility = View.GONE
                    resource.data?.let { userInfo ->
                        val userName = userInfo.name ?: userInfo.preferredUsername ?: "User"
                        binding.tvWelcomeName.text = userName
                        binding.tvAvatarInitials.text = if (userName.isNotEmpty()) userName.take(1).uppercase() else "U"
                        binding.tvUserEmail.text = userInfo.email ?: "No email provided"
                        binding.tvUserIdValue.text = userInfo.sub ?: "Unknown"
                        binding.tvUsernameValue.text = userInfo.preferredUsername ?: "N/A"
                    } ?: run {
                        binding.tvWelcomeName.text = "User"
                        binding.tvAvatarInitials.text = "U"
                    }
                }
                is Resource.Error -> {
                    binding.profileProgressBar.visibility = View.GONE
                    Toast.makeText(this, "Error: ${resource.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadUserInfo() {
        authManager.getAccessToken()?.let { token ->
            viewModel.fetchUserInfo(token)
        }
    }

    private fun setupClickListeners() {
        binding.btnManageContacts.setOnClickListener {
            val intent = Intent(this, com.example.secureauthapp.ui.contact.ContactListActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            authManager.logout(logoutLauncher)
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
