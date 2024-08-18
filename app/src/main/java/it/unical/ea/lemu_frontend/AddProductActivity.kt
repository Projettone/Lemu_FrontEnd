package it.unical.ea.lemu_frontend

import CameraCapture
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import it.unical.ea.lemu_frontend.viewmodels.ProdottoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openapitools.client.models.ProdottoDto
import java.io.ByteArrayOutputStream
import java.io.InputStream
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay


@Composable
fun AddProductActivity(navController: NavHostController, prodottoViewModel: ProdottoViewModel) {
    var productName by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productCategory by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var disponibilità by remember { mutableStateOf("") }
    var isBannerVisible by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var base64Image by remember { mutableStateOf<String?>(null) }
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    val disponibilitàInt = disponibilità.toIntOrNull() ?: 0

    val context = LocalContext.current

    // Stato per l'AlertDialog
    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Errore") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            OutlinedTextField(
                value = productName,
                onValueChange = { productName = it },
                label = { Text("Nome Prodotto") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = productDescription,
                onValueChange = { productDescription = it },
                label = { Text("Descrizione Prodotto") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }

        item {
            OutlinedTextField(
                value = productCategory,
                onValueChange = { productCategory = it },
                label = { Text("Categoria Prodotto") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }

        item {
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Prezzo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
        item {
            OutlinedTextField(
                value = disponibilità,
                onValueChange = { disponibilità = it },
                label = { Text("Disponibilità") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }

        item {
            ImagePickerButton(onImageSelected = { uri ->
                capturedImageUri = uri
                if (uri != null) {
                    base64Image = uriToBase64(uri, context)
                }
            })
        }
        item {
            CameraCapture(onImageCaptured = { uri ->
                capturedImageUri = uri
                if (uri != null) {
                    base64Image = uriToBase64(uri, context)
                }
            })
        }

        item {
            capturedImageUri?.let { uri ->
                val painter = rememberImagePainter(uri)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painter,
                        contentDescription = "Immagine catturata dalla fotocamera",
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .size(200.dp)
                    )
                }

            }
        }

        item {
            Box(){
                Button(
                    onClick = {
                        if (productName.isNotEmpty() && productDescription.isNotEmpty() && productCategory.isNotEmpty() && base64Image != null && price.isNotEmpty()) {
                            val normalizedPrice = normalizePrice(price)
                            val prodotto = ProdottoDto(
                                nome = productName,
                                descrizione = productDescription,
                                prezzo = normalizedPrice,
                                venduti = 0,
                                categoria = productCategory,
                                disponibilita = disponibilitàInt,
                                immagineProdotto = base64Image,
                                idrecensioni = null,
                                idutente = 1L
                            )
                            coroutineScope.launch {
                                prodottoViewModel.addProduct(prodotto)
                                isBannerVisible = true // Mostra il banner
                                delay(4000L) // 4 secondi
                                isBannerVisible = false // Nascondi il banner
                                // Resetta i campi e l'immagine
                                productName = ""
                                productDescription = ""
                                productCategory = ""
                                price = ""
                                disponibilità = ""
                                capturedImageUri = null
                                base64Image = null
                            }


                        } else {
                            errorMessage = "Tutti i campi devono essere compilati"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(top = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Green, // Colore di sfondo del bottone
                        contentColor = Color.White // Colore del testo e del contenuto del bottone
                    ),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text("Aggiungi prodotto",
                        color = Color.Black, // Colore del testo
                        fontSize = 17.sp, // Dimensione del carattere
                        fontWeight = FontWeight.Bold, // Peso del carattere (grassetto)
                        textAlign = TextAlign.Center )
                }

                AnimatedVisibility(
                    visible = isBannerVisible,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Red)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Prodotto aggiunto con successo",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                }
            }


        }
    }
}


@Composable
fun ImagePickerButton(onImageSelected: (Uri) -> Unit) {
    val getContent = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }

    Button(
        onClick = {
            // Chiedi all'utente di scegliere un'immagine dalla galleria
            getContent.launch("image/*")
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(top = 10.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Cyan, // Colore di sfondo del bottone
            contentColor = Color.White // Colore del testo e del contenuto del bottone
        ),
        shape = RoundedCornerShape(5.dp) // Angoli arrotondati con raggio di 10 dp
    ) {
        Text("Seleziona immagine",
            color = Color.Black, // Colore del testo
            fontSize = 17.sp, // Dimensione del carattere
            fontWeight = FontWeight.Bold, // Peso del carattere (grassetto)
            textAlign = TextAlign.Center )
    }
}

// Funzione per convertire un'immagine in base64
fun uriToBase64(uri: Uri, context: android.content.Context): String? {
    var inputStream: InputStream? = null
    try {
        inputStream = context.contentResolver.openInputStream(uri)
        val bytes = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var bytesRead: Int
        if (inputStream != null) {
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                bytes.write(buffer, 0, bytesRead)
            }
        }
        val base64 = Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT)
        bytes.close()
        return base64
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        inputStream?.close()
    }
    return null
}

// Funzione per normalizzare il prezzo sostituendo la virgola con il pu
// Funzione per normalizzare il prezzo sostituendo la virgola con il punto
fun normalizePrice(price: String): String {
    return price.replace(",", ".")
}