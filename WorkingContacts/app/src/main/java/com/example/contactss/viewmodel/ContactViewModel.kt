package com.example.contactss.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactss.data.ContactRepository
import com.example.contactss.data.local.Contact
import com.example.contactss.data.local.ContactDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    val allContacts: Flow<List<Contact>>
    private val repository: ContactRepository

    init {
        val contactDao = ContactDatabase.getDatabase(application).contactDao()
        repository = ContactRepository(contactDao)
        allContacts = repository.allContacts // Παρακολούθηση δεδομένων [cite: 91]
    }

    // Προσθήκη επαφής
    fun addContact(contact: Contact) = viewModelScope.launch {
        repository.insert(contact)
    }

    // Ενημέρωση επαφής [cite: 70]
    fun updateContact(contact: Contact) = viewModelScope.launch {
        repository.update(contact)
    }

    // Διαγραφή επαφής [cite: 71]
    fun deleteContact(contact: Contact) = viewModelScope.launch {
        repository.delete(contact)
    }

    // Αναζήτηση [cite: 72]
    fun searchContacts(query: String): Flow<List<Contact>> {
        return repository.searchContacts(query)
    }
}