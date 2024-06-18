package it.unical.ea.lemu_frontend

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.unical.ea.lemu_frontend.viewModels.ProductInfo
import kotlinx.coroutines.delay

@Composable
fun HomePageActivity(navController: NavController, productList: List<ProductInfo>?) {
    val customColor = Color(0xFFF8F3F3)
    val customColor1 = Color(0xFFFFF3E7)
    val LightGrayColor = Color(0xFFECECEC)
    val MyYellow = Color(0xFFFFBE00)
    val MyBlue = Color(0xFF047FC6)
    var currentIndex by remember { mutableStateOf(0) }
    val lazyListState = rememberLazyListState()
    val lazyListState2 = rememberLazyListState()
    var isLogged by remember { mutableStateOf(false) }

    val images = listOf(
        R.drawable.trucchi,
        R.drawable.sport,
        R.drawable.giardinaggio,
        R.drawable.informatica,
        R.drawable.abbigliamento
    )
    val images2 = listOf(
        R.drawable.trucchi
    )

    Surface(color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            LaunchedEffect(Unit) {
                while (true) {
                    delay(5000) // Cambia immagine ogni 5 secondi
                    val nextIndex = (currentIndex + 1) % images.size
                    lazyListState.animateScrollToItem(nextIndex)
                    currentIndex = nextIndex
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                item {
                    Box{
                        //barra superiore Info spedizione consegna
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .padding(6.dp)
                                .border(0.dp, Color.Transparent, RoundedCornerShape(9.dp))
                                .clip(RoundedCornerShape(8.dp))
                                .background(customColor1),
                            horizontalArrangement = Arrangement.Center,

                            )
                        {
                            Column(
                                modifier = Modifier
                                    .padding(start = 13.dp)
                            ){
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.truck),
                                        contentDescription = "Truck Icon",
                                        modifier = Modifier.size(30.dp)
                                    )
                                    Text(
                                        text = "Spedizione gratuita",
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Row{
                                    Text(
                                        text = "Esclusivo per te",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(start = 5.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            // Divider verticale
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .fillMaxHeight()
                                    .padding(top = 15.dp)
                                    .padding(bottom = 50.dp)
                                    .background(Color.Gray)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Column(
                                modifier = Modifier.padding(end = 10.dp)
                            ){
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    Image(
                                        painter = painterResource(id = R.drawable.truckconfirm),
                                        contentDescription = "Truck Icon",
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Text(
                                        text = "Garanzia di consegna",
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Row{
                                    Text(
                                        text = "Rimbordo per quasiasi problema",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(start = 5.dp)
                                    )
                                }
                            }
                        }
                        //immagini categorie "scopri di piu"
                        LazyRow(
                            state = lazyListState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomStart)
                                .padding(top = 55.dp)
                        ) {
                            itemsIndexed(images) { index, imageRes ->
                                Box(
                                    modifier = Modifier
                                        .fillParentMaxWidth()
                                        .aspectRatio(16 / 9f),
                                    contentAlignment = Alignment.Center

                                ) {
                                    Image(
                                        painter = painterResource(id = imageRes),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Row (
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier
                                            .border(width = 2.dp, color = Color.White)
                                            .padding(5.dp)


                                    ){
                                        Text(text = "Scopri di più"
                                            , color = Color.White,
                                            fontSize = 45.sp,
                                            fontFamily = FontFamily.Serif

                                        )
                                    }
                                }

                            }
                        }
                    }//FINE BOX
                }//FINE ITEM

                //selezionati per te
                if(isLogged) {
                    item {
                        Column(
                            modifier = Modifier
                                .background(customColor)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(top = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Selezionati per te..",
                                    modifier = Modifier.padding(start = 6.dp),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold

                                )
                            }
                            Spacer(modifier = Modifier.height(9.dp))
                            LaunchedEffect(Unit) {
                                //delay(5000)
                                while (true) {
                                    lazyListState2.animateScrollBy(20.dp.value) // Scorrimento di 20dp
                                    delay(1000)
                                }
                            }
                            LazyRow(
                                state = lazyListState2,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            ) {
                                items(images2) { imageRes ->
                                    Box(
                                        modifier = Modifier
                                            .border(
                                                0.5.dp,
                                                LightGrayColor,
                                                RoundedCornerShape(9.dp)
                                            )
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(color = Color.White)
                                            .padding(20.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = imageRes),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(100.dp)
                                                .aspectRatio(1f),
                                            contentScale = ContentScale.Fit
                                        )


                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                }

                            }
                        }

                    }
                }//fine selezionati per te

                //visualizzazione prodotti
                if (productList != null) {
                    items(productList.chunked(1)) { rowImages ->

                        rowImages.forEach { productInfo ->
                            Row(
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .padding(start = 5.dp)
                                    .padding(end = 5.dp)
                                    .weight(1f)
                                    .clickable {
                                        navController.navigate("prodotto/${productInfo.id}") // Interpola l'ID del prodotto nel percorso di navigazione
                                    }
                                    .border(
                                        width = 0.5.dp,
                                        color = LightGrayColor,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                            ) {
                                Column(
                                    modifier = Modifier

                                        .width(150.dp) // Dimensione del Box (più grande dell'immagine)
                                        .height(270.dp)
                                        .border(2.dp, Color.Transparent, RoundedCornerShape(9.dp))
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 8.dp,
                                                bottomStart = 8.dp,
                                                topEnd = 0.dp,
                                                bottomEnd = 0.dp
                                            )
                                        )
                                        .background(LightGrayColor) // Sfondo del Box
                                    ,

                                    horizontalAlignment = Alignment.CenterHorizontally, // Allinea la colonna orizzontalmente al centro
                                    verticalArrangement = Arrangement.Center
                                ) {

                                    Image(
                                        painter = painterResource(id = productInfo.imageResId),
                                        contentDescription = productInfo.description,
                                        modifier = Modifier
                                        //.fillMaxSize() // Adatta l'immagine alla dimensione del Box
                                        //.align(Alignment.Center) // Allinea l'immagine al centro del Box
                                    )
                                }
                                Column(
                                    modifier = Modifier.padding(8.dp) // Rimuovi il padding interno alla colonna
                                ) {
                                    Text(
                                        modifier = Modifier.padding(top = 23.dp),
                                        text = productInfo.description,
                                        style = TextStyle(fontSize = 15.sp),
                                        maxLines = 3, // Limita il testo a un massimo di tre righe
                                        overflow = TextOverflow.Ellipsis // Aggiungi puntini sospensivi se il testo è troppo lungo
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    val number = productInfo?.valutazione
                                    val integerPart = number?.toInt()
                                    val decimalPart = integerPart?.let { number.minus(it) }
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (productInfo != null) {
                                            Text(
                                                text = productInfo.valutazione.toString(),
                                                style = TextStyle(fontSize = 13.sp),
                                                color = MyBlue
                                            )
                                        }
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
                                                    .size(15.dp)
                                                //.absoluteOffset(0.dp, 2.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        if (productInfo != null) {
                                            Text(
                                                text = "(${productInfo.numeroRecensioni})",
                                                style = TextStyle(fontSize = 12.sp),
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${productInfo.venduti} + acquistati",
                                        style = TextStyle(fontSize = 13.sp),
                                        color = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = productInfo.price,
                                        style = TextStyle(fontSize = 20.sp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = "Disponibilità: 45",
                                        style = TextStyle(fontSize = 10.sp)
                                    )
                                    Spacer(modifier = Modifier.height(15.dp))

                                    Button(
                                        onClick = { /* Azione da eseguire al clic del pulsante */ },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 5.dp, end = 5.dp),
                                        colors = ButtonDefaults.buttonColors(backgroundColor = MyYellow),
                                        shape = RoundedCornerShape(10.dp) // Arrotonda i bordi con un raggio di 8dp
                                    ) {
                                        Text(text = "Aggiungi al carrello")
                                    }

                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }// FINE visualizzazione prodotti

            }// FINE LAZYCOLUM INIZIALE
        }
    }
}



