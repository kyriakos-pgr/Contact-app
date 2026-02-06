package com.example.contactss.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.contactss.data.local.Contact
import com.example.contactss.viewmodel.ContactViewModel

/**
 * Κύρια οθόνη προβολής και διαχείρισης επαφών.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ContactScreen(viewModel: ContactViewModel) {
    // Παρακολούθηση της κατάστασης των επαφών από το ViewModel
    val contacts by viewModel.allContacts.collectAsState(initial = emptyList())

    // Καταστάσεις για τη διαχείριση της διεπαφής (αναζήτηση, παράθυρα διαλόγου)
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedContact by remember { mutableStateOf<Contact?>(null) }
    var contactToDelete by remember { mutableStateOf<Contact?>(null) }

    val myCustomBlue = Color(0xFF2D50A6)

    // Προσαρμογή των χρωμάτων επιλογής κειμένου
    val customTextSelectionColors = TextSelectionColors(
        handleColor = myCustomBlue,
        backgroundColor = myCustomBlue.copy(alpha = 0.4f)
    )

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        Scaffold(
            topBar = {
                Surface(color = myCustomBlue) {
                    Column {
                        TopAppBar(
                            title = { Text("Οι Επαφές μου", color = Color.White) },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                        )
                        // Πεδίο δυναμικής αναζήτησης επαφών
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Αναζήτηση...", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Black,
                                cursorColor = myCustomBlue
                            ),
                            keyboardOptions = KeyboardOptions(autoCorrect = false)
                        )
                    }
                }
            },
            bottomBar = {
                // Μπάρα πλοήγησης για εναλλαγή λειτουργιών και προσθήκη επαφής
                NavigationBar(containerColor = myCustomBlue) {
                    NavigationBarItem(
                        selected = true,
                        onClick = { },
                        icon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White) },
                        label = { Text("CONTACTS", color = Color.White, fontSize = 10.sp) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { showAddDialog = true },
                        icon = { Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White) },
                        label = { Text("ADD USER", color = Color.White, fontSize = 10.sp) }
                    )
                }
            }
        ) { padding ->
            // Φιλτράρισμα της λίστας βάσει του ερωτήματος αναζήτησης
            val filteredContacts = contacts.filter {
                it.firstName.contains(searchQuery, ignoreCase = true) ||
                        it.lastName.contains(searchQuery, ignoreCase = true) ||
                        it.phoneNumber.contains(searchQuery)
            }

            Box(modifier = Modifier.padding(padding)) {
                ContactsContent(
                    filteredContacts,
                    onContactClick = { selectedContact = it },
                    onDelete = { contactToDelete = it }
                )
            }

            // Εμφάνιση παραθύρων διαλόγου ανάλογα με την κατάσταση (Add, Edit, Delete)
            if (showAddDialog) {
                AddContactDialog(
                    onDismiss = { showAddDialog = false },
                    onConfirm = { f, l, p, e ->
                        viewModel.addContact(Contact(firstName = f, lastName = l, phoneNumber = p, email = e))
                        showAddDialog = false
                    }
                )
            }

            if (selectedContact != null) {
                EditContactDialog(
                    contact = selectedContact!!,
                    onDismiss = { selectedContact = null },
                    onSave = { updated -> viewModel.updateContact(updated); selectedContact = null },
                    onDeleteRequest = { contactToDelete = selectedContact; selectedContact = null }
                )
            }

            if (contactToDelete != null) {
                DeleteConfirmationDialog(
                    onDismiss = { contactToDelete = null },
                    onConfirm = { viewModel.deleteContact(contactToDelete!!); contactToDelete = null }
                )
            }
        }
    }
}

/**
 * Προβολή της λίστας επαφών με ομαδοποίηση ανά αρχικό γράμμα.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactsContent(contacts: List<Contact>, onContactClick: (Contact) -> Unit, onDelete: (Contact) -> Unit) {
    val grouped = contacts.sortedBy { it.firstName }.groupBy { it.firstName.firstOrNull()?.uppercaseChar() ?: '#' }

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        grouped.forEach { (initial, contactsInGroup) ->
            stickyHeader {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = initial.toString(), color = Color.Gray, modifier = Modifier.padding(end = 8.dp))
                    HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.LightGray)
                }
            }
            items(contactsInGroup) { contact ->
                ContactItem(contact = contact, onDelete = { onDelete(contact) }, onClick = { onContactClick(contact) })
            }
        }
    }
}

/**
 * Παράθυρο διαλόγου για την επεξεργασία στοιχείων υπάρχουσας επαφής.
 */
@Composable
fun EditContactDialog(contact: Contact, onDismiss: () -> Unit, onSave: (Contact) -> Unit, onDeleteRequest: () -> Unit) {
    var firstName by remember { mutableStateOf(contact.firstName) }
    var lastName by remember { mutableStateOf(contact.lastName) }
    var phoneNumber by remember { mutableStateOf(contact.phoneNumber) }
    var email by remember { mutableStateOf(contact.email) }
    val myCustomBlue = Color(0xFF2D50A6)

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF1F1F1)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Κεφαλίδα προφίλ με χρώμα φόντου
                Box(modifier = Modifier.fillMaxWidth().height(280.dp).background(myCustomBlue)) {
                    IconButton(onClick = onDismiss, modifier = Modifier.padding(16.dp).align(Alignment.TopStart)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.align(Alignment.Center)) {
                        Surface(shape = CircleShape, color = Color.LightGray, modifier = Modifier.size(110.dp)) {
                            Box(contentAlignment = Alignment.Center) { Text("+", fontSize = 30.sp) }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("${firstName.uppercase()} ${lastName.uppercase()}", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                // Πεδία εισαγωγής στοιχείων
                Column(modifier = Modifier.padding(horizontal = 32.dp, vertical = 24.dp).weight(1f)) {
                    RoundedInputField(firstName, { firstName = it }, "Όνομα")
                    RoundedInputField(lastName, { lastName = it }, "Επίθετο")
                    RoundedInputField(phoneNumber, { phoneNumber = it }, "Τηλέφωνο", KeyboardType.Phone)
                    RoundedInputField(email, { email = it }, "Email", KeyboardType.Email)
                    Spacer(modifier = Modifier.weight(1f))
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(onClick = onDeleteRequest, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB33A3A)), shape = RoundedCornerShape(8.dp), modifier = Modifier.width(130.dp)) {
                            Text("DELETE", color = Color.White)
                        }
                        Button(onClick = { onSave(contact.copy(firstName = firstName, lastName = lastName, phoneNumber = phoneNumber, email = email)) },
                            colors = ButtonDefaults.buttonColors(containerColor = myCustomBlue), shape = RoundedCornerShape(8.dp), modifier = Modifier.width(130.dp)) {
                            Text("SAVE", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Παράθυρο διαλόγου για την προσθήκη νέας επαφής.
 */
@Composable
fun AddContactDialog(onDismiss: () -> Unit, onConfirm: (String, String, String, String) -> Unit) {
    var f by remember { mutableStateOf("") }
    var l by remember { mutableStateOf("") }
    var p by remember { mutableStateOf("") }
    var e by remember { mutableStateOf("") }
    val myCustomBlue = Color(0xFF2D50A6)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Νέα Επαφή", color = myCustomBlue, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                RoundedInputField(f, { f = it }, "Όνομα")
                RoundedInputField(l, { l = it }, "Επίθετο")
                RoundedInputField(p, { p = it }, "Τηλέφωνο", KeyboardType.Phone)
                RoundedInputField(e, { e = it }, "Email", KeyboardType.Email)
            }
        },
        confirmButton = {
            Button(
                onClick = { if (f.isNotBlank()) onConfirm(f, l, p, e) },
                colors = ButtonDefaults.buttonColors(containerColor = myCustomBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Αποθήκευση", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Ακύρωση", color = myCustomBlue, fontWeight = FontWeight.Bold)
            }
        }
    )
}

/**
 * Παράθυρο επιβεβαίωσης διαγραφής.
 */
@Composable
fun DeleteConfirmationDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    val myCustomBlue = Color(0xFF2D50A6)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Διαγραφή Επαφής", fontWeight = FontWeight.Bold) },
        text = { Text("Είστε σίγουροι ότι θέλετε να διαγράψετε αυτή την επαφή;") },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = Color.Red), shape = RoundedCornerShape(20.dp)) {
                Text("Διαγραφή")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Ακύρωση", color = myCustomBlue, fontWeight = FontWeight.Bold)
            }
        }
    )
}

/**
 * Μεμονωμένο στοιχείο λίστας για την προβολή βασικών στοιχείων επαφής.
 */
@Composable
fun ContactItem(contact: Contact, onDelete: () -> Unit, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("${contact.firstName} ${contact.lastName}", fontWeight = FontWeight.Bold)
                Text(contact.phoneNumber, color = Color.Gray)
            }
            IconButton(onClick = onDelete) { Text("🗑️", fontSize = 20.sp) }
        }
    }
}

/**
 * Προσαρμοσμένο πεδίο εισαγωγής κειμένου με στρογγυλεμένες γωνίες.
 */
@Composable
fun RoundedInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val myCustomBlue = Color(0xFF2D50A6)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label, color = Color.Gray) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(30.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = myCustomBlue,
            unfocusedBorderColor = myCustomBlue,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = myCustomBlue
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            autoCorrect = false,
            imeAction = ImeAction.Next
        )
    )
}

/**
 * Βοηθητικό κουμπί ενεργειών για τη διεπαφή της επαφής.
 */
@Composable
fun ContactActionButton(icon: String, onClick: () -> Unit) {
    Surface(modifier = Modifier.size(48.dp).clickable { onClick() }, shape = RoundedCornerShape(8.dp), color = Color.Transparent, border = ButtonDefaults.outlinedButtonBorder) {
        Box(contentAlignment = Alignment.Center) { Text(icon, fontSize = 22.sp) }
    }
}