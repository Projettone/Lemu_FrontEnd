import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import it.unical.ea.lemu_frontend.R
import org.openapitools.client.models.UtenteLoginDto
import it.unical.ea.lemu_frontend.ui.theme.endColor
import it.unical.ea.lemu_frontend.ui.theme.startColor
import it.unical.ea.lemu_frontend.viewmodels.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openapitools.client.models.UtenteRegistrazioneDto


@Composable
fun LoginActivity() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val coroutineScope = CoroutineScope(Dispatchers.IO)
    val context = LocalContext.current
    val loginViewModel = LoginViewModel()


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
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (!loginViewModel.validateCredenzialiEmail(email)){
                    Toast.makeText(context, "Inserisci una mail valida", Toast.LENGTH_SHORT).show()
                } else if(!loginViewModel.validateCredenzialiPassword(password)) {
                    Toast.makeText(context, "Inserisci password valida", Toast.LENGTH_SHORT).show()
                } else {
                    println("Credenziali: $email $password")
                    coroutineScope.launch {
                        val utente = UtenteLoginDto(
                            credenzialiEmail = email,
                            credenzialiPassword = password
                        )

                        val regDto = UtenteRegistrazioneDto(
                            credenzialiEmail = email,
                            credenzialiPassword = password,
                            nome = "fra",
                            cognome = "ca"
                        )

                        loginViewModel.registerUser(regDto) { apiResponse ->
                            if (apiResponse.success) {
                                val message = apiResponse.message
                                val data = apiResponse.data

                                println("Messaggio dal server: $message")
                                println("Messaggio dal server: data $data")

                                data?.let {
                                    // Gestione del caso di successo
                                }
                            } else {
                                val errorMessage = apiResponse.data
                                println("Errore dal server: $errorMessage")
                            }
                        }

                        /*
                        loginViewModel.loginUser(utente) { apiResponse ->
                            if (apiResponse.success) {
                                val message = apiResponse.message
                                val data = apiResponse.data

                                println("Messaggio dal server: $message")
                                println("Messaggio dal server: data $data")

                                data?.let {
                                    // Gestione del caso di successo
                                }
                            } else {
                                val errorMessage = apiResponse.data
                                println("Errore dal server: $errorMessage")
                            }
                        }

                         */
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

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Password dimenticata?", modifier = Modifier.clickable {
            //Password dimenticata clicked
        },
            color = startColor
            )

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Oppure accedi con")

        Spacer(modifier = Modifier.height(16.dp))


        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Image(painter = painterResource(id = R.drawable.facebook),
                contentDescription = "Facebook",
                modifier = Modifier.size(60.dp).clickable {
                    //Facebook clicked
                }
            )


            Image(painter = painterResource(id = R.drawable.google),
                contentDescription = "Google",
                modifier = Modifier.size(60.dp).clickable {
                    //Google clicked
                }
            )
        }

    }
}
