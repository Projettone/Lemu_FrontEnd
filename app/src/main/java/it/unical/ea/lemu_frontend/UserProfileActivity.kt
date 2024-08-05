package it.unical.ea.lemu_frontend

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import it.unical.ea.lemu_frontend.viewmodels.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.openapitools.client.models.Indirizzo
import org.openapitools.client.models.UtenteDto

@Composable
fun UserProfileActivity(
    authViewModel: AuthViewModel,
    navController: NavController
) {
    var isLoggingOut by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var user by authViewModel.user
    var via by remember { mutableStateOf(user?.indirizzo?.via ?: "") }
    var numeroCivico by remember { mutableStateOf(user?.indirizzo?.numeroCivico?.toString() ?: "") }
    var citta by remember { mutableStateOf(user?.indirizzo?.citta ?: "") }
    val coroutineScope = rememberCoroutineScope()

    if (isLoggingOut) {
        LaunchedEffect(Unit) {
            try {
                authViewModel.logout()
                navController.navigate("login") { popUpTo("home") { inclusive = true } }
            } catch (e: Exception) {
                Toast.makeText(context, "Logout failed: ${e.message}", Toast.LENGTH_SHORT).show()
                isLoggingOut = false
            }
        }
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            authViewModel = authViewModel,
            onDismiss = { showChangePasswordDialog = false },
            onChangePassword = { newPassword ->
                coroutineScope.launch {
                    val response = authViewModel.changePassword(newPassword)
                    withContext(Dispatchers.Main) {
                        if (response) {
                            Toast.makeText(
                                context,
                                "Password modificata con successo",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Errore durante l'aggiornamento della password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        showChangePasswordDialog = false
                    }
                }
            })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White)
            .padding(bottom = 0.dp)
    ) {
        ScrollableContent(
            user = user,
            indirizzo = Indirizzo(
                via = via,
                numeroCivico = numeroCivico.toIntOrNull(),
                citta = citta
            ),
            onAddressChange = { newAddress ->
                via = newAddress.via.toString()
                numeroCivico = newAddress.numeroCivico?.toString() ?: ""
                citta = newAddress.citta.toString()
            },
            onSave = {
                coroutineScope.launch {
                    val indirizzo = Indirizzo(
                        via = via,
                        numeroCivico = numeroCivico.toIntOrNull(),
                        citta = citta
                    )
                    val response = authViewModel.updateAddress(indirizzo)
                    withContext(Dispatchers.Main) {
                        if (response) {
                            Toast.makeText(
                                context,
                                "Indirizzo aggiornato con successo",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Errore durante l'aggiornamento dell'indirizzo",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            },
            onChangePasswordClick = { showChangePasswordDialog = true },
            onLogoutClick = { isLoggingOut = true }
        )
    }
}

@Composable
fun ScrollableContent(
    user: UtenteDto?,
    indirizzo: Indirizzo,
    onAddressChange: (Indirizzo) -> Unit,
    onSave: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserProfileHeader(user)
        Spacer(modifier = Modifier.height(24.dp))
        EditableUserInfo(
            indirizzo = indirizzo,
            onAddressChange = onAddressChange,
            onSave = onSave
        )
        Spacer(modifier = Modifier.height(24.dp))
        ChangePasswordButton(onClick = onChangePasswordClick)
        Spacer(modifier = Modifier.height(16.dp))
        LogoutButton(onClick = onLogoutClick)
    }
}

@Composable
fun UserProfileHeader(user: UtenteDto?) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .padding(bottom = 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(100.dp)
                .clickable {
                    // Controllo foto profilo
                },
            shape = CircleShape,
            color = Color.Gray
        ) {
            user?.immagineProfilo?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        user?.let {
            Text(
                text = "Ciao, ${it.nome} ${it.cognome}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        user?.email?.let { Text(text = it, fontSize = 20.sp, color = Color.Gray) }
    }
}

@Composable
fun EditableUserInfo(
    indirizzo: Indirizzo,
    onAddressChange: (Indirizzo) -> Unit,
    onSave: () -> Unit
) {
    var via by remember { mutableStateOf(indirizzo.via ?: "") }
    var numeroCivico by remember { mutableStateOf(indirizzo.numeroCivico?.toString() ?: "") }
    var citta by remember { mutableStateOf(indirizzo.citta ?: "") }
    var isEditing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Indirizzo di spedizione:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                if (isEditing) {
                    OutlinedTextField(
                        value = via,
                        onValueChange = { newVia ->
                            via = newVia
                            onAddressChange(
                                Indirizzo(
                                    via = via,
                                    numeroCivico = numeroCivico.toIntOrNull(),
                                    citta = citta
                                )
                            )
                        },
                        label = { Text("Via") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = numeroCivico,
                        onValueChange = { newNumeroCivico ->
                            numeroCivico = newNumeroCivico
                            onAddressChange(
                                Indirizzo(
                                    via = via,
                                    numeroCivico = numeroCivico.toIntOrNull(),
                                    citta = citta
                                )
                            )
                        },
                        label = { Text("Numero Civico") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = citta,
                        onValueChange = { newCitta ->
                            citta = newCitta
                            onAddressChange(
                                Indirizzo(
                                    via = via,
                                    numeroCivico = numeroCivico.toIntOrNull(),
                                    citta = citta
                                )
                            )
                        },
                        label = { Text("Città") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onSave()
                            isEditing = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Salva Indirizzo")
                    }
                } else {
                    Text(text = "Via: $via")
                    Text(text = "Numero Civico: $numeroCivico")
                    Text(text = "Città: $citta")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { isEditing = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                    ) {
                        Text("Modifica")
                    }
                }
            }
        }
    }
}

@Composable
fun ChangePasswordButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
    ) {
        Text(text = "Cambia Password", color = Color.White)
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
    ) {
        Text(text = "Logout", color = Color.White)
    }
}

@Composable
fun ChangePasswordDialog(
    authViewModel: AuthViewModel,
    onDismiss: () -> Unit,
    onChangePassword: (String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Cambia Password") },
        text = {
            Column {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nuova Password") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Conferma Password") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        if (newPassword == confirmPassword) {
                            coroutineScope.launch {
                                val response = authViewModel.changePassword(newPassword)
                                withContext(Dispatchers.Main) {
                                    if (response) {
                                        Toast.makeText(
                                            context,
                                            "Password modificata con successo",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onDismiss()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Errore durante l'aggiornamento della password",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Le due password non coincidono",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Conferma")
                }
            }
        },
        dismissButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                ) {
                    Text("Annulla")
                }
            }
        }
    )
}

