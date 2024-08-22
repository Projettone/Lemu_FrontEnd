package it.unical.ea.lemu_frontend

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.unical.ea.lemu_frontend.viewmodels.AuthViewModel
import it.unical.ea.lemu_frontend.viewmodels.CarrelloViewModel
import it.unical.ea.lemu_frontend.viewmodels.PaymentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val ADDRESS_ERROR_MESSAGE = "Impostare correttamente l'indirizzo di spedizione prima di procedere"
private const val CARD_NUMBER_ERROR_MESSAGE = "Numero carta non valido. Deve essere composto da 13-19 cifre."
private const val EXPIRY_DATE_ERROR_MESSAGE = "Data di scadenza non valida o passata. Rispettare il formato MM/yy"
private const val CVC_ERROR_MESSAGE = "CVC non valido. Deve essere composto da 3-4 cifre."

@Composable
fun CheckoutActivity(
    authViewModel: AuthViewModel,
    carrelloViewModel: CarrelloViewModel,
    navController: NavController,
    paymentViewModel: PaymentViewModel
) {
    val context = LocalContext.current
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvc by remember { mutableStateOf("") }
    var addressError by remember { mutableStateOf("") }
    var cardNumberError by remember { mutableStateOf("") }
    var expiryDateError by remember { mutableStateOf("") }
    var cvcError by remember { mutableStateOf("") }
    val totalAmount by carrelloViewModel.totalPrice.collectAsState()
    val user by authViewModel.user

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colors.background)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Checkout",
            fontSize = 30.sp,
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            elevation = 4.dp,
            backgroundColor = MaterialTheme.colors.primary
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Importo totale",
                    fontSize = 20.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$${String.format("%.2f", totalAmount)}",
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        user?.indirizzo?.let { address ->
            val isAddressComplete = !address.via.isNullOrBlank() &&
                    address.numeroCivico != null &&
                    !address.citta.isNullOrBlank()

            if (isAddressComplete) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    elevation = 4.dp,
                    backgroundColor = MaterialTheme.colors.primary
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Indirizzo di spedizione",
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${address.via} ${address.numeroCivico}, ${address.citta}",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            } else {
                addressError = ADDRESS_ERROR_MESSAGE
            }
        } ?: run {
            addressError = ADDRESS_ERROR_MESSAGE
        }

        if (addressError.isNotEmpty()) {
            Text(
                text = addressError,
                color = Color.Red,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Button(
                onClick = {
                    navController.navigate("profile")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Set Up Address")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (cardNumberError.isNotEmpty()) {
            Text(
                text = cardNumberError,
                color = Color.Red,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        OutlinedTextField(
            value = cardNumber,
            onValueChange = { cardNumber = it },
            label = { Text("Card Number") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            )
        )

        if (expiryDateError.isNotEmpty()) {
            Text(
                text = expiryDateError,
                color = Color.Red,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        OutlinedTextField(
            value = expiryDate,
            onValueChange = { expiryDate = it },
            label = { Text("Expiry Date (MM/YY)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            )
        )

        if (cvcError.isNotEmpty()) {
            Text(
                text = cvcError,
                color = Color.Red,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        OutlinedTextField(
            value = cvc,
            onValueChange = { cvc = it },
            label = { Text("CVC") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                addressError = ""
                cardNumberError = ""
                expiryDateError = ""
                cvcError = ""

                val address = user?.indirizzo
                if (address == null || address.via.isNullOrBlank() || address.numeroCivico == null || address.citta.isNullOrBlank()) {
                    addressError = ADDRESS_ERROR_MESSAGE
                    return@Button
                }

                val isCardNumberValid = isValidCardNumber(cardNumber)
                val isExpiryDateValid = isValidExpiryDate(expiryDate)
                val isCvcValid = isValidCvc(cvc)

                if (!isCardNumberValid) {
                    cardNumberError = CARD_NUMBER_ERROR_MESSAGE
                }
                if (!isExpiryDateValid) {
                    expiryDateError = EXPIRY_DATE_ERROR_MESSAGE
                }
                if (!isCvcValid) {
                    cvcError = CVC_ERROR_MESSAGE
                }

                if (cardNumberError.isNotEmpty() || expiryDateError.isNotEmpty() || cvcError.isNotEmpty()) {
                    return@Button
                }

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = paymentViewModel.checkOut(totalAmount)
                        if (response) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Pagamento effettuato con successo",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate("profile")
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Saldo insufficiente, riscattare un buono e riprovare",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Errore durante il processo di pagamento, riprovare", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
        ) {
            Text("Paga ora")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}


private fun isValidCardNumber(cardNumber: String): Boolean {
    val cleanedNumber = cardNumber.replace(Regex("\\D"), "")
    return cleanedNumber.length in 13..19 && cleanedNumber.all { it.isDigit() }
}



private fun isValidExpiryDate(expiryDate: String): Boolean {
    return try {
        val dateFormat = SimpleDateFormat("MM/yy", Locale.getDefault())
        dateFormat.isLenient = false
        val parsedDate = dateFormat.parse(expiryDate) ?: return false

        val currentCalendar = Calendar.getInstance()
        val currentYear = currentCalendar.get(Calendar.YEAR)
        val currentMonth = currentCalendar.get(Calendar.MONTH) + 1

        val expiryCalendar = Calendar.getInstance().apply {
            time = parsedDate
        }
        val expiryYear = expiryCalendar.get(Calendar.YEAR)
        val expiryMonth = expiryCalendar.get(Calendar.MONTH) + 1

        (expiryYear > currentYear) ||
                (expiryYear == currentYear && expiryMonth >= currentMonth)
    } catch (e: Exception) {
        false
    }
}

private fun isValidCvc(cvc: String): Boolean {
    return cvc.length in 3..4 && cvc.all { it.isDigit() }
}


