import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.navigation.NavController
import it.unical.ea.lemu_frontend.R
import it.unical.ea.lemu_frontend.ui.theme.endColor
import it.unical.ea.lemu_frontend.ui.theme.startColor
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
                if (!authViewModel.validateCredenzialiEmail(email)){
                    Toast.makeText(context, "Inserisci una mail valida", Toast.LENGTH_SHORT).show()
                } else if(!authViewModel.validateCredenzialiPassword(password)) {
                    Toast.makeText(context, "Inserisci password valida", Toast.LENGTH_SHORT).show()
                } else {
                    println("Credenziali: $email $password")
                    coroutineScope.launch {
                    try{
                        val response = authViewModel.authenticate(email, password)
                        withContext(Dispatchers.Main) {
                            if (response == true) {
                                navController.navigate("home")
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

                    coroutineScope.launch {
                        try{
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
}
