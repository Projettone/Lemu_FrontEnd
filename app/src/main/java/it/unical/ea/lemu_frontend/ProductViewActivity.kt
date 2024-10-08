package it.unical.ea.lemu_frontend

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.autoSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import it.unical.ea.lemu_frontend.viewmodels.AuthViewModel
import it.unical.ea.lemu_frontend.viewmodels.CarrelloViewModel
import it.unical.ea.lemu_frontend.viewmodels.ProdottoViewModel
import it.unical.ea.lemu_frontend.viewmodels.WishlistViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.openapitools.client.models.ProdottoDto
import org.openapitools.client.models.RecensioneDto

@Composable
fun ProductViewActivity(productIdString: String, navController: NavHostController,viewModel: ProdottoViewModel, carrelloViewModel: CarrelloViewModel, authViewModel: AuthViewModel, wishlistViewModel: WishlistViewModel) {
    val MyYellow = Color(0xFFFFBE00)
    val MyBlue = Color(0xFF047FC6)
    val MyOrange = Color(0xFFFB8201)
    var quantity by remember { mutableStateOf(1) }
    var isFavorite by rememberSaveable { mutableStateOf(false) }
    var product by remember { mutableStateOf(ProdottoDto(null)) }
    val productId = productIdString.toLongOrNull()
    var loading by remember { mutableStateOf(true) }
    val listaRecensioni by viewModel.listaRecensioni.collectAsState()
    val listaWishlist by viewModel.wishlists.collectAsState()
    var isBannerVisible by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(0) }

    var reviewText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0f) }
    val productsPerPage = 2

    var showDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current




    val paginatedListaRecensioni = remember(currentPage,listaRecensioni) {
        listaRecensioni.chunked(productsPerPage).getOrElse(currentPage) { emptyList() } ?: emptyList()
    }

    LaunchedEffect(Unit) {
        launch {
            val products = viewModel.getProductById(productId)
            viewModel.findRecensioniByidProdotto(productId)
            if (products != null) {
                product = products
            }
            loading = false
        }
    }

    fun loadRecensioni(){
        coroutineScope.launch(Dispatchers.IO) {
            viewModel.findRecensioniByidProdotto(productId)
        }
    }



    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else{
        LazyColumn(
            modifier = Modifier.padding(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Marca: ${product.categoria}",
                        style = TextStyle(fontSize = 13.sp),
                        color = MyBlue
                    )

                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    val averageRating = remember(listaRecensioni) {
                        listaRecensioni
                            .map { it.rating }
                            .average()
                            .toFloat()
                    }
                    val formattedNumber = remember(averageRating) {
                        String.format("%.1f", averageRating)
                    }
                    Text(
                        text = formattedNumber,
                        style = TextStyle(fontSize = 13.sp)
                    )

                    val integerPart = averageRating.toInt()
                    val decimalPart = averageRating - integerPart

                    repeat(5) { index ->
                        val starColor =
                            if (index < integerPart || (decimalPart > 0.5 && integerPart == index)) {
                                MyYellow
                            } else {
                                Color.Gray
                            }
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = starColor,
                            modifier = Modifier
                                .size(13.dp)
                                .absoluteOffset(0.dp, 2.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    if (product != null) {
                        Text(
                            text = "(${listaRecensioni.size})",
                            style = TextStyle(fontSize = 12.sp),
                            color = MyBlue
                        )
                    }
                }

                }
                Spacer(modifier = Modifier.height(16.dp))
                product.nome?.let {
                    Text(
                        text = it,
                        fontWeight = FontWeight.Bold,
                        fontSize = 23.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (product != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {

                        val base64WithoutPrefix =
                            product.immagineProdotto?.removePrefix("data:image/png;base64,")
                        val decodedBytes = Base64.decode(base64WithoutPrefix, Base64.DEFAULT)
                        val bitmap =
                            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        val imageBitmap = bitmap.asImageBitmap()
                        Image(
                            painter = BitmapPainter(imageBitmap),
                            contentDescription = null,
                            modifier = Modifier
                                .size(300.dp)
                                .align(Alignment.Center),
                            contentScale = ContentScale.Fit
                        )


                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Heart Icon",
                            tint = if (isFavorite) Color.Red else Color.Black,
                            modifier = Modifier
                                .size(50.dp)
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                                .clickable {
                                    coroutineScope.launch(Dispatchers.IO) {
                                        if (authViewModel.checkAuthentication()) {
                                            viewModel.findWishlistByIdUtente()
                                            showDialog = true
                                        } else {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Devi fare l'accesso per aggiungere ai preferiti!")
                                            }
                                        }


                                    }


                                }
                        )
                    }
                }

                if (showDialog) {
                    AlertDialog(
                        modifier = Modifier
                            .height(500.dp)
                            .background(color = Color.Gray),
                        onDismissRequest = { showDialog = false },

                        text = {
                            Column(modifier = Modifier.fillMaxHeight()) {
                                if(listaWishlist.isEmpty()){
                                    Text(
                                        text = "Devi creare prima almeno una wishlist.",
                                        style = MaterialTheme.typography.body1,
                                        modifier = Modifier.padding(vertical = 16.dp)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Empty wishlist icon",
                                        modifier = Modifier
                                            .size(400.dp)
                                            .padding(vertical = 1.dp),
                                        tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                    )
                                }else{
                                    Text(
                                        text = "Scegli la wishlist",
                                        style = MaterialTheme.typography.h6,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))

                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    items(listaWishlist) { item ->
                                        item.nome?.let { itemName ->
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        coroutineScope.launch(Dispatchers.IO) {
                                                            item.id?.let {
                                                                wishlistViewModel.addProductToWishlist(
                                                                    it,
                                                                    product
                                                                )
                                                                showDialog = false
                                                                scope.launch {
                                                                    snackbarHostState.showSnackbar("Prodotto aggiunto alla wishlist ${item.nome}!")
                                                                }
                                                            }
                                                        }
                                                        Log.d(
                                                            "Wishlist",
                                                            "Hai cliccato su: $itemName"
                                                        )
                                                    }
                                                    .padding(8.dp),
                                                elevation = 4.dp,
                                                shape = RoundedCornerShape(12.dp),
                                                backgroundColor = MaterialTheme.colors.surface
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(16.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = itemName,
                                                        style = MaterialTheme.typography.body1,
                                                        modifier = Modifier.weight(1f)
                                                    )

                                                    Icon(
                                                        imageVector = Icons.Default.FavoriteBorder,
                                                        contentDescription = "Wishlist Icon",
                                                        tint = MaterialTheme.colors.primary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Button(onClick = { showDialog = false }) {
                                Text("Chiudi")
                            }
                        }
                    )
                }




                Text(text = "${product.prezzo}€",style = TextStyle(fontSize = 30.sp))
                Text(text = "Resi GRATUITI", color = MyBlue)
                Text(text = "I prezzi degli articoli in vendita su Lemu includono l'IVA.")
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "${product.disponibilita} disponibili", color = MyBlue)

                Spacer(modifier = Modifier.height(20.dp))
                Box {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Absolute.Center,
                            modifier = Modifier
                                .border(
                                    1.5.dp,
                                    Color.LightGray,
                                    shape = RoundedCornerShape(30.dp)
                                )
                                .height(35.dp)
                                .padding(start = 10.dp, end = 10.dp)


                        ) {
                            Button(
                                modifier = Modifier.padding(end = 12.dp),
                                onClick = {
                                    if (quantity > 1) quantity--
                                },
                                colors = ButtonDefaults.buttonColors(Color.White),
                            ) {
                                Text(text = "-", color = Color.Black)
                            }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.width(30.dp)
                            ) {
                                Text(
                                    text = "${quantity.coerceIn(1, 99)}",
                                    fontSize = 19.sp,
                                    color = Color.Black,
                                )

                            }
                            Button(
                                modifier = Modifier.padding(start = 12.dp),

                                onClick = {
                                    if (quantity < 99) quantity++
                                },
                                colors = ButtonDefaults.buttonColors(Color.White),
                            ) {
                                Text(text = "+", color = Color.Black)
                            }

                        }
                        Spacer(modifier = Modifier.height(5.dp))

                        Button(
                            onClick = {
                                if (!authViewModel.checkAuthentication()){
                                    Toast.makeText(context, "Effettuare il login per aggiungere un prodotto al carrello", Toast.LENGTH_SHORT).show()
                                } else {
                                    scope.launch {
                                        carrelloViewModel.addProductToCart(product, quantity)
                                        snackbarHostState.showSnackbar("Prodotto aggiunto al carrello!")
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp),
                            colors = ButtonDefaults.buttonColors(MyYellow)

                        ) {
                            Text(text = "Aggiungi al carrello")
                        }


                    }
                    CustomSnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }


                Row {
                    Text(text = "Spedizione", color = Color.Gray)
                    Spacer(modifier = Modifier.width(24.dp))
                    Text(text = "Lemu")
                }
                Row {
                    Text(text = "Venditore", color = Color.Gray)
                    Spacer(modifier = Modifier.width(33.dp))
                    if (product != null) {
                        Text(text = "venditore", color = MyBlue)
                    }
                }
                Row {
                    Text(text = "Resi", color = Color.Gray)
                    Spacer(modifier = Modifier.width(65.dp))
                    Text(text = "Restituibile, entro 14 giorni dalla consegna", color = MyBlue)
                }
                Row {
                    Text(text = "Pagamento", color = Color.Gray)
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(text = "Transazione sicura", color = MyBlue)
                }

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "Recensione cliente!",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

            }

//caricamento recensioni

        items(paginatedListaRecensioni){recensioni ->
            RecensioniViewActivity(recensioni)
        }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    androidx.compose.material.Button(
                        onClick = {
                            if (currentPage > 0) {
                                currentPage--
                            }
                        },
                        enabled = currentPage > 0,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .width(150.dp)
                            .height(50.dp)
                    ) {
                        Text(text = "Pagina Precedente", fontSize = 16.sp)
                    }

                    val lista = listaRecensioni

                    androidx.compose.material.Button(
                        onClick = {
                            if (currentPage < (lista.size / productsPerPage)) {
                                currentPage++
                            }
                        },
                        enabled = currentPage < (lista.size / productsPerPage),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .width(150.dp)
                            .height(50.dp)
                    ) {
                        Text(text = "Pagina Successiva", fontSize = 16.sp) 
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Aggiungi una nuova recensione",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text("Scrivi la tua recensione") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(text = "Valutazione: ")
                    RatingBar(
                        rating = rating,
                        onRatingChanged = { newRating ->
                            rating = newRating
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (!authViewModel.checkAuthentication()){
                            Toast.makeText(context, "Effettuare il login per inserire una recensione", Toast.LENGTH_SHORT).show()
                        } else{
                            coroutineScope.launch(Dispatchers.IO){
                                try {
                                    viewModel.addRecensione(rating, reviewText, productId, product)
                                    reviewText = ""
                                    rating = 0f

                                    isBannerVisible = true
                                    loadRecensioni()
                                    delay(2000L)
                                    isBannerVisible = false


                                } catch (e: Exception) {
                                    Log.e("Errore nell'aggiunta della recensione", e.toString())
                                }

                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(MyOrange),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = "Invia recensione")
                }
            }
        }
    }
    Box(){
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
                androidx.compose.material3.Text(
                    text = "Recensione aggiunta con successo",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }

}



@Composable
fun RecensioniViewActivity(recensioni: RecensioneDto) {


    val MyYellow = Color(0xFFFFBE00)


    Row (
        verticalAlignment = Alignment.CenterVertically
    ){

        recensioni.immagineProfiloAutore?.let { immagineProfilo ->
            if (immagineProfilo.startsWith("data:image/")) {
                val base64String = immagineProfilo.substringAfter("base64,")
                val imageBitmap = base64ToImageBitmap(base64String)
                if (imageBitmap != null) {
                    Image(
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp),
                        bitmap = imageBitmap,
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds

                    )
                } else {
                    Image(
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp),
                        painter = painterResource(id = R.drawable.placeholder),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds

                    )
                }
            }
        }

        Text(text = recensioni.credenzialiEmailAutore)
    }
    Row (
        modifier = Modifier.padding(start = 1.dp)
    ){
        val number = recensioni.rating
        val integerPart = number.toInt()
        val decimalPart = number - integerPart
        repeat(5) { index ->
            val starColor =
                if (index < integerPart || (decimalPart > 0.5 && integerPart == index)) {
                    MyYellow
                } else {
                    Color.Gray
                }
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = starColor,
                modifier = Modifier
                    .size(13.dp)
                    .absoluteOffset(0.dp, 2.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
    Text(text = recensioni.commento)

    Spacer(modifier = Modifier.height(19.dp))

}


@Composable
fun RatingBar(rating: Float, onRatingChanged: (Float) -> Unit) {
    Row {
        repeat(5) { index ->
            val starColor = if (index < rating) Color(0xFFFFBE00) else Color.Gray
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = starColor,
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        onRatingChanged(index + 1f)
                    }
            )
        }
    }
}




