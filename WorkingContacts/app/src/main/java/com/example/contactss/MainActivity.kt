package com.example.contactss

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.contactss.ui.ContactScreen
import com.example.contactss.viewmodel.ContactViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Εφαρμογή του Theme του project σου
            MaterialTheme {
                // Surface είναι το φόντο της εφαρμογής
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Δημιουργία του ViewModel αυτόματα μέσω της βιβλιοθήκης lifecycle-viewmodel-compose
                    val contactViewModel: ContactViewModel = viewModel()

                    // Κλήση της κύριας οθόνης που φτιάξαμε στο φάκελο UI
                    ContactScreen(viewModel = contactViewModel)
                }
            }
        }
    }
}