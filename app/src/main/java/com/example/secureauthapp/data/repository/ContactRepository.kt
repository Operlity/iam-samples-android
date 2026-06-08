package com.example.secureauthapp.data.repository

import com.example.secureauthapp.data.local.Contact
import com.example.secureauthapp.data.local.ContactDao
import kotlinx.coroutines.flow.Flow

class ContactRepository(private val contactDao: ContactDao) {
    val allContacts: Flow<List<Contact>> = contactDao.getAllContacts()

    fun searchContacts(query: String): Flow<List<Contact>> {
        return contactDao.searchContacts("%$query%")
    }

    suspend fun insert(contact: Contact) {
        contactDao.insertContact(contact)
    }

    suspend fun update(contact: Contact) {
        contactDao.updateContact(contact)
    }

    suspend fun delete(contact: Contact) {
        contactDao.deleteContact(contact)
    }

    suspend fun getContactById(id: Int): Contact? {
        return contactDao.getContactById(id)
    }
}
