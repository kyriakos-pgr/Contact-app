package com.example.contactss.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Κεντρικό σημείο πρόσβασης στη βάση δεδομένων Room.
 * Περιλαμβάνει τον πίνακα Contact και ορίζει την έκδοση (version) της βάσης.
 */
@Database(entities = [Contact::class], version = 1, exportSchema = false)
abstract class ContactDatabase : RoomDatabase() {
    /**
     * Παροχή πρόσβασης στο DAO για την εκτέλεση των λειτουργιών στη βάση.
     */
    abstract fun contactDao(): ContactDao

    companion object {
        /**
         * Μεταβλητή INSTANCE για τη διατήρηση της σύνδεσης (Singleton).
         * Η σήμανση @Volatile διασφαλίζει την άμεση ορατότητα των αλλαγών σε όλα τα threads.
         */
        @Volatile
        private var INSTANCE: ContactDatabase? = null
        /**
         * Επιστροφή της υπάρχουσας σύνδεσης ή δημιουργία νέας εάν δεν υπάρχει.
         * Χρήση synchronized για την αποφυγή ταυτόχρονης δημιουργίας από διαφορετικά threads.
         */
        fun getDatabase(context: Context): ContactDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactDatabase::class.java,
                    "contact_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}