package com.example.secureauthapp.ui.contact

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.secureauthapp.data.local.AppDatabase
import com.example.secureauthapp.data.repository.ContactRepository
import com.example.secureauthapp.databinding.ActivityContactListBinding

class ContactListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactListBinding
    private lateinit var viewModel: ContactViewModel
    private lateinit var adapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupRecyclerView()
        setupSearchView()
        setupClickListeners()
        observeContacts()
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(this)
        val repository = ContactRepository(database.contactDao())
        val factory = ContactViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ContactViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = ContactAdapter(
            onEditClick = { contact ->
                val intent = Intent(this, AddEditContactActivity::class.java)
                intent.putExtra("CONTACT_ID", contact.id)
                startActivity(intent)
            },
            onDeleteClick = { contact ->
                showDeleteConfirmation(contact)
            }
        )
        binding.rvContacts.layoutManager = LinearLayoutManager(this)
        binding.rvContacts.adapter = adapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText ?: "")
                return true
            }
        })
    }

    private fun setupClickListeners() {
        binding.fabAddContact.setOnClickListener {
            startActivity(Intent(this, AddEditContactActivity::class.java))
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeContacts() {
        viewModel.allContacts.observe(this) { contacts ->
            adapter.submitList(contacts)
            binding.emptyView.visibility = if (contacts.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun showDeleteConfirmation(contact: com.example.secureauthapp.data.local.Contact) {
        AlertDialog.Builder(this)
            .setTitle("Delete Contact")
            .setMessage("Are you sure you want to delete ${contact.name}?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.delete(contact)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
