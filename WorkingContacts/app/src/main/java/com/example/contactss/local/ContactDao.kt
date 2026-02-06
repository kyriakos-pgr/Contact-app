package com.example.contactss.data.local
import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Διεπαφή (DAO) για την πρόσβαση και τη διαχείριση των δεδομένων στον πίνακα "contacts_table".
 */
@Dao
interface ContactDao {
    /**
     * Εισαγωγή νέας επαφής. Σε περίπτωση σύγκρουσης (ίδιο ID), γίνεται αντικατάσταση.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact)

    /**
     * Ενημέρωση των στοιχείων μιας υπάρχουσας επαφής.
     */
    @Update
    suspend fun updateContact(contact: Contact)

    /**
     * Διαγραφή μιας συγκεκριμένης επαφής από τη βάση.
     */
    @Delete
    suspend fun deleteContact(contact: Contact)

    /**
     * Ανάκτηση όλων των επαφών με αλφαβητική ταξινόμηση βάσει ονόματος.
     * Επιστρέφει Flow για συνεχή παρακολούθηση των αλλαγών.
     */
    @Query("SELECT * FROM contacts_table ORDER BY firstName ASC")
    fun getAllContacts(): Flow<List<Contact>>

    /**
     * Αναζήτηση επαφών βάσει ονόματος ή τηλεφώνου που ταιριάζουν με το ερώτημα αναζήτησης.
     */
    @Query("SELECT * FROM contacts_table WHERE firstName LIKE :searchQuery OR phoneNumber LIKE :searchQuery")
    fun searchDatabase(searchQuery: String): Flow<List<Contact>>
}