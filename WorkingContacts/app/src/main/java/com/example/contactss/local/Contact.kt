package com.example.contactss.data.local
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Ορισμός του πίνακα "contacts_table" για τη βάση δεδομένων Room.
 */
@Entity(tableName = "contacts_table")
data class Contact(
    /**
     * Ο μοναδικός κωδικός της επαφής με αυτόματη παραγωγή τιμών.
     */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val email: String
)
