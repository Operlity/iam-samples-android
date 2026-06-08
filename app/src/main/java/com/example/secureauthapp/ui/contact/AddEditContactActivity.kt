package com.example.secureauthapp.ui.contact

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.secureauthapp.data.local.AppDatabase
import com.example.secureauthapp.data.local.Contact
import com.example.secureauthapp.data.repository.ContactRepository
import com.example.secureauthapp.databinding.ActivityAddEditContactBinding
import kotlinx.coroutines.launch

class AddEditContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditContactBinding
    private lateinit var viewModel: ContactViewModel
    private var contactId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contactId = intent.getIntExtra("CONTACT_ID", -1)
        
        setupViewModel()
        setupUI()
        setupClickListeners()
        
        if (contactId != -1) {
            loadContactData()
        }
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(this)
        val repository = ContactRepository(database.contactDao())
        val factory = ContactViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ContactViewModel::class.java]
    }

    private fun setupUI() {
        if (contactId != -1) {
            binding.toolbar.title = "Edit Contact"
            binding.btnSave.text = "Update Contact"
        }
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        
        binding.btnSave.setOnClickListener {
            saveContact()
        }
    }

    private fun loadContactData() {
        val database = AppDatabase.getDatabase(this)
        val repository = ContactRepository(database.contactDao())
        
        lifecycleScope.launch {
            val contact = repository.getContactById(contactId)
            contact?.let {
                binding.etName.setText(it.name)
                binding.etPhone.setText(it.phoneNumber)
                binding.etEmail.setText(it.email)
                binding.etCompany.setText(it.company)
            }
        }
    }

    private fun saveContact() {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val company = binding.etCompany.text.toString().trim()

        if (!validateInput(name, phone, email)) {
            return
        }

        val contact = Contact(
            id = if (contactId != -1) contactId else 0,
            name = name,
            phoneNumber = phone,
            email = email,
            company = if (company.isNotEmpty()) company else null
        )

        if (contactId == -1) {
            viewModel.insert(contact)
            Toast.makeText(this, "Contact added", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.update(contact)
            Toast.makeText(this, "Contact updated", Toast.LENGTH_SHORT).show()
        }
        
        finish()
    }

    private fun validateInput(name: String, phone: String, email: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.tilName.error = "Name is required"
            isValid = false
        } else {
            binding.tilName.error = null
        }

        if (phone.isEmpty()) {
            binding.tilPhone.error = "Phone number is required"
            isValid = false
        } else {
            binding.tilPhone.error = null
        }

        if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Invalid email format"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        return isValid
    }
}
