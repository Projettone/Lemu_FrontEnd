package it.unical.ea.lemu_frontend

import CameraCapture
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Base64
import android.util.Log
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
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import it.unical.ea.lemu_frontend.viewmodels.AuthViewModel
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddProductActivity(authViewModel: AuthViewModel,navController: NavHostController, prodottoViewModel: ProdottoViewModel) {
    var productName by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productCategory by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var disponibilità by remember { mutableStateOf("") }
    var isBannerVisible by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var base64Image by remember { mutableStateOf<String?>(null) }
    val coroutineScope = CoroutineScope(Dispatchers.IO)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Seleziona Categoria") }
    val categories = listOf("Informatica", "Giardinaggio", "Moda", "Sport", "Illuminazione", "Gioielli","Scarpe")

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
            Text(
                text = "Aggiungi un nuovo prodotto",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

        }

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
            Spacer(modifier = Modifier.height(15.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = { selectedCategory = it },
                    label = { Text("Categoria Prodotto") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                    }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            onClick = {
                                selectedCategory = category
                                expanded = false
                            }
                        ) {
                            Text(text = category)
                        }
                    }
                }
            }
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
                        if (productName.isNotEmpty() && productDescription.isNotEmpty() && selectedCategory.isNotEmpty() && base64Image != null && price.isNotEmpty()) {
                            val normalizedPrice = normalizePrice(price)
                            val prodotto = ProdottoDto(
                                nome = productName,
                                descrizione = productDescription,
                                prezzo = normalizedPrice,
                                venduti = 0,
                                categoria = selectedCategory,
                                disponibilita = disponibilitàInt,
                                immagineProdotto = base64Image,
                                idrecensioni = null,
                                idutente = authViewModel.user.value?.id
                            )
                            coroutineScope.launch {
                                try {
                                    prodottoViewModel.addProduct(prodotto)
                                    isBannerVisible = true
                                    delay(2000L)
                                    isBannerVisible = false
                                    productName = ""
                                    productDescription = ""
                                    productCategory = ""
                                    price = ""
                                    disponibilità = ""
                                    capturedImageUri = null
                                    base64Image = null
                                } catch (e: Exception) {
                                    Log.e("AddProductError", "Errore nell'aggiunta del prodotto", e)
                                }
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Tutti i campi devono essere compilati!")
                            }

                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(top = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Green,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text("Aggiungi prodotto",
                        color = Color.Black,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
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
                CustomSnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter)

                )
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
            getContent.launch("image/*")
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(top = 10.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Cyan,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(5.dp)
    ) {
        Text("Seleziona immagine",
            color = Color.Black,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center )
    }
}

fun uriToBase64(uri: Uri, context: android.content.Context): String? {
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        // Ruota l'immagine di 90 gradi a sinistra
        val rotatedBitmap = rotateBitmapLeft(bitmap)

        val bytes = ByteArrayOutputStream()
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val base64 = Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT)
        bytes.close()
        return base64
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}


fun normalizePrice(price: String): String {
    return price.replace(",", ".")
}

fun rotateBitmapLeft(bitmap: Bitmap): Bitmap {
    val matrix = Matrix().apply { postRotate(+90f) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

