import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import it.unical.ea.lemu_frontend.LoadingOverlay
import it.unical.ea.lemu_frontend.ui.theme.endColor
import it.unical.ea.lemu_frontend.viewmodels.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun RegistrationActivity(navController: NavController, authViewModel: AuthViewModel) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = CoroutineScope(Dispatchers.IO)
    val nameFocusRequester = FocusRequester()
    val surnameFocusRequester = FocusRequester()
    val emailFocusRequester = FocusRequester()
    val passwordFocusRequester = FocusRequester()
    val confirmPasswordFocusRequester = FocusRequester()
    val keyboardController = LocalSoftwareKeyboardController.current
    val isLoading by authViewModel.isLoading

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .focusRequester(nameFocusRequester),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { surnameFocusRequester.requestFocus() }
                )
            )

            OutlinedTextField(
                value = surname,
                onValueChange = { surname = it },
                label = { Text("surname") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .focusRequester(surnameFocusRequester),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { emailFocusRequester.requestFocus() }
                )
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .focusRequester(emailFocusRequester),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
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
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { confirmPasswordFocusRequester.requestFocus() }
                )
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Conferma Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .focusRequester(confirmPasswordFocusRequester),
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
                    if (name.isBlank() || surname.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                        Toast.makeText(context, "Inserisci tutti i dati", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (!authViewModel.validateCredenzialiEmail(email)) {
                        Toast.makeText(context, "Inserire un indirizzo email valido", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (!authViewModel.validateCredenzialiPassword(password)) {
                        Toast.makeText(context, "Password non valida, assicurarsi che la lunghezza sia di almeno 8 caratteri", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (password != confirmPassword) {
                        Toast.makeText(context, "Le password non coincidono", Toast.LENGTH_SHORT).show()
                    } else {
                        coroutineScope.launch {
                            try {
                                val response = authViewModel.register(name, surname, email, password)
                                withContext(Dispatchers.Main) {
                                    if (response.success == true) {
                                        Toast.makeText(context, "Registrazione avvenuta con successo", Toast.LENGTH_SHORT).show()
                                        navController.navigate("profile")
                                    } else if (response.message == "403 FORBIDDEN") {
                                        Toast.makeText(context, "Errore durante la registrazione, utente bannato", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Errore durante la registrazione, controlla le credenziali e riprova", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Errore durante la registrazione", Toast.LENGTH_SHORT).show()
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
                Text("Registrati")
            }
        }

        if (isLoading) {
            LoadingOverlay(isLoading = isLoading)
        }
    }
}
