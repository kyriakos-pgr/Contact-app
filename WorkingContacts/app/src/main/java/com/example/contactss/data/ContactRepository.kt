package com.example.contactss.data
import com.example.contactss.data.local.Contact
import com.example.contactss.data.local.ContactDao
import kotlinx.coroutines.flow.Flow

class ContactRepository (private val contactDao: ContactDao){
    // Παίρνει όλες τις επαφές ταξινομημένες αλφαβητικά
    val allContacts: Flow<List<Contact>> = contactDao.getAllContacts()

    // Λειτουργία προσθήκης νέας επαφής
    suspend fun insert(contact: Contact) {
        contactDao.insertContact(contact)
    }

    // Λειτουργία ενημέρωσης υπάρχουσας επαφής
    suspend fun update(contact: Contact) {
        contactDao.updateContact(contact)
    }

    // Λειτουργία διαγραφής επαφής
    suspend fun delete(contact: Contact) {
        contactDao.deleteContact(contact)
    }

    // Λειτουργία αναζήτησης βάσει ονόματος ή τηλεφώνου
    fun searchContacts(query: String): Flow<List<Contact>> {
        return contactDao.searchDatabase("%$query%")
    }
}