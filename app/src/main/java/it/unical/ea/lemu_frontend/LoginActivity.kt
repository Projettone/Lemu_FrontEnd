import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavController
import it.unical.ea.lemu_frontend.LoadingOverlay
import it.unical.ea.lemu_frontend.R
import it.unical.ea.lemu_frontend.ui.theme.endColor
import it.unical.ea.lemu_frontend.viewmodels.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginActivity(navController: NavController, authViewModel: AuthViewModel, signInLauncher: ActivityResultLauncher<Intent>) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val coroutineScope = CoroutineScope(Dispatchers.IO)
    val context = LocalContext.current
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val isLoading by authViewModel.isLoading
    val passwordFocusRequester = FocusRequester()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { passwordFocusRequester.requestFocus() }
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .focusRequester(passwordFocusRequester),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (!authViewModel.validateCredenzialiEmail(email)) {
                        Toast.makeText(context, "Inserisci una mail valida", Toast.LENGTH_SHORT).show()
                    } else if (!authViewModel.validateCredenzialiPassword(password)) {
                        Toast.makeText(context, "Inserisci password valida", Toast.LENGTH_SHORT).show()
                    } else {
                        keyboardController?.hide()
                        coroutineScope.launch {
                            try {
                                val response = authViewModel.authenticate(email, password)
                                withContext(Dispatchers.Main) {
                                    if (response.success == true) {
                                        navController.navigate("profile")
                                    } else if (response.message == "403 FORBIDDEN") {
                                        Toast.makeText(context, "Errore, utente bannato", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Errore, controlla le credenziali e riprova", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Errore durante il login", Toast.LENGTH_SHORT).show()
                                }
                                e.printStackTrace()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = endColor)
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { showForgotPasswordDialog = true }) {
                Text("Password dimenticata", color = Color.Blue)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Oppure accedi con")

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Image(painter = painterResource(id = R.drawable.facebook),
                    contentDescription = "Facebook",
                    modifier = Modifier
                        .size(60.dp)
                        .clickable {
                            coroutineScope.launch {
                                try {
                                    authViewModel.signInWithFacebook()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                )

                Image(painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google",
                    modifier = Modifier
                        .size(60.dp)
                        .clickable {
                            coroutineScope.launch {
                                try {
                                    authViewModel.signInWithGoogle(signInLauncher)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "Non possiedi un account?")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate("registration")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = endColor)
            ) {
                Text("Registrati")
            }
        }

        if (showForgotPasswordDialog) {
            ForgotPasswordDialog(
                authViewModel = authViewModel,
                onDismissRequest = { showForgotPasswordDialog = false },
                onSendEmail = { email ->
                    keyboardController?.hide()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            showForgotPasswordDialog = false
                            authViewModel.sendPasswordResetEmail(email)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Email inviata all'indirizzo inserito", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Errore nell'invio della mail", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }

        if (isLoading) {
            LoadingOverlay(isLoading = isLoading)
        }
    }
}

@Composable
fun ForgotPasswordDialog(authViewModel: AuthViewModel, onDismissRequest: () -> Unit, onSendEmail: (String) -> Unit) {
    val email = remember { mutableStateOf("") }
    val isLoading by authViewModel.isLoading

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Recupero Password") },
        text = {
            Column {
                Text("Inserisci l'email per ricevere le istruzioni di recupero della password.")
                TextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSendEmail(email.value)
                },
            ) {
                Text("Invia", color = Color.Blue)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Annulla", color = Color.Blue)
            }
        }
    )
}
