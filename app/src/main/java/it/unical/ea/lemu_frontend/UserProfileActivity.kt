package it.unical.ea.lemu_frontend

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import it.unical.ea.lemu_frontend.viewmodels.AuthViewModel
import it.unical.ea.lemu_frontend.viewmodels.UserProfileViewModel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.openapitools.client.models.CouponDto
import org.openapitools.client.models.Indirizzo
import org.openapitools.client.models.UtenteDto

import android.util.Base64
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import coil.compose.rememberImagePainter
import org.openapitools.client.models.RecensioneDto
import java.io.ByteArrayInputStream
import java.io.InputStream


@Composable
fun UserProfileActivity(
    authViewModel: AuthViewModel,
    userProfileViewModel: UserProfileViewModel,
    navController: NavController
) {
    var isLoggingOut by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var user by authViewModel.user
    var via by remember { mutableStateOf(user?.indirizzo?.via ?: "") }
    var numeroCivico by remember { mutableStateOf(user?.indirizzo?.numeroCivico?.toString() ?: "") }
    var citta by remember { mutableStateOf(user?.indirizzo?.citta ?: "") }
    val saldo by userProfileViewModel.saldo.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            userProfileViewModel.updateSaldo()
        }
    }

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
            saldo = saldo,
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
            onLogoutClick = { isLoggingOut = true },
            onRedeemCoupon = { CouponCode ->
                coroutineScope.launch {
                    try {
                        userProfileViewModel.redeemCoupon(CouponCode)
                        Toast.makeText(
                            context,
                            "Richiesta coupon eseguita correttamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception){
                        Toast.makeText(
                            context,
                            "Errore durante il riscatto del Coupon",
                            Toast.LENGTH_SHORT
                        ).show()
                        e.printStackTrace()
                    }
                }
            },
            userProfileViewModel = userProfileViewModel
        )
    }
}

@Composable
fun ScrollableContent(
    user: UtenteDto?,
    indirizzo: Indirizzo,
    saldo: Double,
    onAddressChange: (Indirizzo) -> Unit,
    onSave: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onRedeemCoupon: (String) -> Unit,
    userProfileViewModel: UserProfileViewModel
    ) {
    val context = LocalContext.current
    val reviews by userProfileViewModel.recensioni.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserProfileHeader(user)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Saldo: €${String.format("%.2f", saldo)}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(24.dp))
        EditableUserInfo(
            indirizzo = indirizzo,
            onAddressChange = onAddressChange,
            onSave = onSave
        )
        Spacer(modifier = Modifier.height(24.dp))
        ChangePasswordButton(onClick = onChangePasswordClick)
        Spacer(modifier = Modifier.height(16.dp))
        RedeemCouponBox(onRedeemCoupon = onRedeemCoupon)
        Spacer(modifier = Modifier.height(24.dp))
        UserReviewsManagement(reviews = reviews, userProfileViewModel = userProfileViewModel)
        Spacer(modifier = Modifier.height(24.dp))

        user?.let {
            if (it.isAdmin == true) {
                AdminActions(
                    userProfileViewModel = userProfileViewModel,
                    onActionComplete = { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

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
            user?.immagineProfilo?.let { immagineProfilo ->
                if (immagineProfilo.startsWith("data:image/")) {
                    // Se l'immagine è in base64
                    val base64String = immagineProfilo.substringAfter("base64,")
                    val imageBitmap = base64ToImageBitmap(base64String)
                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.placeholder),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    // Se l'immagine è un URL o altro tipo di dato supportato da AsyncImagePainter
                    Image(
                        painter = rememberAsyncImagePainter(immagineProfilo),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
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
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Indirizzo di spedizione:",
                    fontSize = 20.sp,
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
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedIndicatorColor = Color(0xFF3F51B5), // Colore della linea di focus
                            unfocusedIndicatorColor = Color.Gray, // Colore della linea di non-focus
                            focusedLabelColor = Color(0xFF3F51B5), // Colore dell'etichetta in focus
                            unfocusedLabelColor = Color.Gray, // Colore dell'etichetta non in focus
                            cursorColor = Color(0xFF3F51B5) // Colore del cursore
                        )
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
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedIndicatorColor = Color(0xFF3F51B5),
                            unfocusedIndicatorColor = Color.Gray,
                            focusedLabelColor = Color(0xFF3F51B5),
                            unfocusedLabelColor = Color.Gray,
                            cursorColor = Color(0xFF3F51B5)
                        )
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
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedIndicatorColor = Color(0xFF3F51B5),
                            unfocusedIndicatorColor = Color.Gray,
                            focusedLabelColor = Color(0xFF3F51B5),
                            unfocusedLabelColor = Color.Gray,
                            cursorColor = Color(0xFF3F51B5)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onSave()
                            isEditing = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
                    ) {
                        Text("Salva Indirizzo", color = Color.White)
                    }
                } else {
                    Text(text = "Via: $via")
                    Text(text = "Numero Civico: $numeroCivico")
                    Text(text = "Città: $citta")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { isEditing = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
                    ) {
                        Text("Modifica", color = Color.White)
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


@Composable
fun AdminActions(
    userProfileViewModel: UserProfileViewModel,
    onActionComplete: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val coupons by userProfileViewModel.coupons.collectAsState()

    AdminCouponManagement(
        coupons = coupons,
        userProfileViewModel = userProfileViewModel
    )

    fun handleAdminAction(
        email: String,
        action: suspend (String) -> Boolean,
        onSuccessMessage: String,
        onFailureMessage: String
    ) {
        if (email.isBlank()) {
            Toast.makeText(context, "Inserire una Email valida", Toast.LENGTH_SHORT).show()
        } else {
            coroutineScope.launch {
                val response = action(email)
                withContext(Dispatchers.Main) {
                    onActionComplete(
                        if (response) onSuccessMessage else onFailureMessage
                    )
                }
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Pannello Amministratore",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("User Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedIndicatorColor = Color(0xFF3F51B5),
                    unfocusedIndicatorColor = Color.Gray,
                    focusedLabelColor = Color(0xFF3F51B5),
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = Color(0xFF3F51B5)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        handleAdminAction(
                            email = email,
                            action = userProfileViewModel::banUser,
                            onSuccessMessage = "Utente bannato con successo",
                            onFailureMessage = "Errore durante l'operazione di ban"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text("Ban", color = Color.White)
                }

                Button(
                    onClick = {
                        handleAdminAction(
                            email = email,
                            action = userProfileViewModel::unbanUser,
                            onSuccessMessage = "Utente sbannato con successo",
                            onFailureMessage = "Errore durante l'operazione di unban"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text("Unban", color = Color.Black)
                }

                Button(
                    onClick = {
                        handleAdminAction(
                            email = email,
                            action = userProfileViewModel::makeAdmin,
                            onSuccessMessage = "L'utente è diventato amministratore",
                            onFailureMessage = "Errore durante l'operazione"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text("Rendi Admin", color = Color.White)
                }

                Button(
                    onClick = {
                        handleAdminAction(
                            email = email,
                            action = userProfileViewModel::revokeAdmin,
                            onSuccessMessage = "Diritti di amministratore revocati",
                            onFailureMessage = "Errore durante la rimozione dei diritti di amministratore"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E9E9E)),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text("Rimuovi Admin", color = Color.White)
                }
            }
        }
    }
}


@Composable
fun RedeemCouponBox(onRedeemCoupon: (String) -> Unit) {
    var CouponCode by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Riscatta Buono",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = CouponCode,
                onValueChange = { CouponCode = it },
                label = { Text("Codice Buono") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onRedeemCoupon(CouponCode) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
            ) {
                Text("Riscatta", color = Color.White)
            }
        }
    }
}



@Composable
fun AdminCouponManagement(
    coupons: List<CouponDto>,
    userProfileViewModel: UserProfileViewModel
) {
    var newCouponValue by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        userProfileViewModel.getPagedCoupons()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Gestione Coupon",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = newCouponValue,
                onValueChange = { newCouponValue = it },
                label = { Text("Valore del Coupon") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val value = newCouponValue.toDoubleOrNull()
                    if (value != null) {
                        coroutineScope.launch {
                            try {
                                userProfileViewModel.createCoupon(value)
                                Toast.makeText(context, "Coupon creato con successo", Toast.LENGTH_SHORT).show()
                                newCouponValue = ""
                                errorMessage = ""
                            } catch (e: Exception){
                                Toast.makeText(context, "Errore durante la creazione del coupon", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        errorMessage = "Inserire un valore valido"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Crea Coupon", color = Color.White)
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Coupon Attivi:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            coupons.forEachIndexed { index, coupon ->
                coupon.valore?.let { value ->
                    coupon.codice?.let { code ->
                        CouponItem(
                            index = userProfileViewModel.currentPageCoupon * userProfileViewModel.pageSize + index + 1,
                            value = value,
                            code = code
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { userProfileViewModel.loadPreviousPageCoupons() },
                    enabled = userProfileViewModel.currentPageCoupon > 0
                ) {
                    Text("Precedente")
                }

                Button(
                    onClick = { userProfileViewModel.loadNextPageCoupons() },
                    enabled = userProfileViewModel.currentPageCoupon < userProfileViewModel.totalPagesCoupon - 1
                ) {
                    Text("Successivo")
                }
            }
        }
    }
}

@Composable
fun CouponItem(
    index: Int,
    value: Double,
    code: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(BorderStroke(1.dp, Color.Gray), shape = RoundedCornerShape(4.dp))
            .padding(16.dp)
    ) {
        Text(text = "ID: $index", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = "Valore: €${String.format("%.2f", value)}", fontSize = 16.sp)
        Text(text = "Codice: $code", fontSize = 16.sp)
    }
}


fun base64ToImageBitmap(base64String: String): ImageBitmap? {
    val bitmap = base64ToBitmap(base64String)
    return bitmap?.asImageBitmap()
}

fun base64ToBitmap(base64String: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeStream(ByteArrayInputStream(decodedBytes))
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}




@Composable
fun UserReviewsManagement(
    reviews: List<RecensioneDto>,
    userProfileViewModel: UserProfileViewModel
) {

    LaunchedEffect(Unit) {
        userProfileViewModel.getPagedReviews()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Le tue recensioni",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (reviews.isEmpty()) {
                Text(
                    text = "Nessuna recensione disponibile",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray
                )
            } else {
                reviews.forEachIndexed { index, review ->
                    ReviewItem(
                        index = userProfileViewModel.currentPageRecensioni * userProfileViewModel.pageSize + index + 1,
                        rating = review.rating,
                        name = review.nomeProdotto,
                        comment = review.commento,
                        onDeleteClick = { userProfileViewModel.deleteReview(review.id) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { userProfileViewModel.loadPreviousPageReviews() },
                    enabled = userProfileViewModel.currentPageRecensioni > 0
                ) {
                    Text("Precedente")
                }

                Button(
                    onClick = { userProfileViewModel.loadNextPageReviews() },
                    enabled = userProfileViewModel.currentPageRecensioni < userProfileViewModel.totalPagesRecensioni - 1
                ) {
                    Text("Successivo")
                }
            }
        }
    }
}


@Composable
fun ReviewItem(
    index: Int,
    rating: Float,
    name: String,
    comment: String?,
    onDeleteClick: suspend () -> Unit
) {

    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Conferma cancellazione") },
            text = { Text("Sei sicuro di voler cancellare questa recensione?") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            onDeleteClick()
                            showDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Conferma", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                ) {
                    Text("Annulla", color = Color.White)
                }
            }
        )
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "Recensione #$index",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Prodotto: $name",
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Rating: ${rating.toString()}",
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            comment?.let {
                Text(
                    text = "Commento: $it",
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text = "Elimina", color = Color.White)
                }
            }
        }
    }
}
