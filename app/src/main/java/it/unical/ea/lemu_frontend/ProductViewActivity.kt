package it.unical.ea.lemu_frontend

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import it.unical.ea.lemu_frontend.viewmodels.ProdottoViewModel
import kotlinx.coroutines.launch
import org.openapitools.client.models.ProdottoDto

@Composable
fun ProductViewActivity(productIdString: String, navController: NavHostController,viewModel: ProdottoViewModel) {
    val MyYellow = Color(0xFFFFBE00)
    val MyBlue = Color(0xFF047FC6)
    val MyOrange = Color(0xFFFB8201)
    var quantity by remember { mutableStateOf(1) }
    var isFavorite by rememberSaveable { mutableStateOf(false) }
    var product by remember { mutableStateOf(ProdottoDto(null)) }
    val productId = productIdString.toLongOrNull()
    var loading by remember { mutableStateOf(true) } // Stato di caricamento
    val listaRecensioni by viewModel.listaRecensioni.collectAsState()






    LaunchedEffect(Unit) {
        launch {
            val products = viewModel.getProductById(productId)

            println("id prodotto "+ productId)
            viewModel.findRecensioniByidProdotto(productId)
            println("lgnhezza lista recensioni "+ listaRecensioni.size)
            if (products != null) {
                product = products
            }
            loading = false
        }
    }



    if (loading) {
        // Mostra una schermata di caricamento
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator() // Indica che i dati stanno venendo caricati
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
                    if (product != null) {
                        Text(
                            text = "V",
                            style = TextStyle(fontSize = 13.sp)
                        )
                    }
                    val myFloat = remember { mutableStateOf(0f) }

                    for (recensione in listaRecensioni) {
                        // Somma il valore di rating a myFloat
                        myFloat.value += recensione.rating
                    }

                    val number = myFloat.value
                    val integerPart = number.toInt()
                    val decimalPart = number - integerPart

                    repeat(5) { index ->
                        val starColor =
                            if (index < integerPart!! || (decimalPart!! > 0.5 && integerPart == index)) {
                                MyYellow
                            } else {
                                Color.Gray
                            }
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = starColor,
                            modifier = Modifier
                                //.width(10.dp)
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
                            //qui passimao il prodotto alla wishlist del database per vedere se è nei preferiti e quindi mettere il cuore rosso oppure no
                            tint = if (isFavorite) Color.Red else Color.Black,
                            modifier = Modifier
                                .size(50.dp) // Dimensione dell'icona
                                .align(Alignment.BottomStart) // Posiziona l'icona in basso a sinistra
                                .padding(8.dp) // Padding per distanziare l'icona dai bordi
                                .clickable {
                                    isFavorite = !isFavorite
                                }
                        )

                        val context = LocalContext.current
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share Icon",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(50.dp)
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .clickable {
                                    val content =
                                        "**Your content to be shared**" // Replace with your actual content (text, link, etc.)
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain" // Adjust based on content type
                                        putExtra(Intent.EXTRA_TEXT, content)
                                    }
                                    // Launch the activity chooser to select the sharing app
                                    context.startActivity(
                                        Intent.createChooser(
                                            shareIntent,
                                            "Condividi con"
                                        )
                                    )
                                }
                        )
                    }
                }


                println("ciaoooo"+ listaRecensioni.size)

                Text(text = "${product.prezzo}",style = TextStyle(fontSize = 30.sp))
                Text(text = "Resi GRATUITI", color = MyBlue)
                Text(text = "I prezzi degli articoli in vendita su Lemu includono l'IVA.")
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "234 disponibili", color = MyBlue)

                Spacer(modifier = Modifier.height(20.dp))

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
                        //.widthIn(max = 198.dp)  // Imposta una larghezza massima alla riga


                    ) {
                        Button(
                            modifier = Modifier.padding(end = 12.dp),
                            onClick = {
                                if (quantity > 1) quantity--
                            },
                            colors = ButtonDefaults.buttonColors(Color.White),
                            //border = BorderStroke(1.dp, Color.Black)
                        ) {
                            Text(text = "-", color = Color.Black)
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.width(30.dp)  // Imposta una larghezza fissa per il contenitore del testo della quantità
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
                            navController.navigate("addProduct")

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp),
                        colors = ButtonDefaults.buttonColors(MyYellow)

                    ) {
                        Text(text = "Aggiungi al carrello")
                    }

                    Button(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp),
                        colors = ButtonDefaults.buttonColors(MyOrange)

                    ) {
                        Text(text = "Aquista ora")
                    }
                }

                Row {
                    Text(text = "Spedizione", color = Color.Gray)
                    Spacer(modifier = Modifier.width(23.dp))
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
                    Spacer(modifier = Modifier.width(74.dp))
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
            /*
        items(listaRecensioni){recensioni ->
            RecensioniViewActivity(recensioni)
        }

             */



        }
    }
}

/*
@Composable
fun RecensioniViewActivity(recensioni: RecensioneDto) {


    val MyYellow = Color(0xFFFFBE00)


    Row (
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(
            modifier = Modifier
                .width(30.dp)
                .height(30.dp),
            painter = painterResource(id = R.drawable.user_logo),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
        Text(text = recensioni.userName)
    }
    Row (
        modifier = Modifier.padding(start = 1.dp)
    ){
        repeat(5) { index ->
            //val starColor = if (index < integerPart!! || (decimalPart!! > 0.5 && integerPart == index )) {
            //  MyYellow
            //} else {
            //  Color.Gray
            //}
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = MyYellow,
                modifier = Modifier
                    .width(18.dp)
                    .absoluteOffset(0.dp, 2.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
    Text(text = review.title)
    Text(text = "Recensito il ${review.date}", color = Color.Gray)
    Text(text = recensioni.commento!!)

    Spacer(modifier = Modifier.height(19.dp))

}


 */

