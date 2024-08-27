package it.unical.ea.lemu_frontend

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
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
import it.unical.ea.lemu_frontend.viewmodels.ProdottoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.openapitools.client.models.ProdottoDto

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter

import androidx.compose.runtime.saveable.rememberSaveable
import it.unical.ea.lemu_frontend.viewmodels.CarrelloViewModel

@Composable
fun HomePageActivity(navController: NavController, viewModel: ProdottoViewModel, isRicerca: Boolean, keyword: String,carrelloViewModel: CarrelloViewModel) {
    val customColor = Color(0xFFF8F3F3)
    val customColor1 = Color(0xFFFFF3E7)
    val LightGrayColor = Color(0xFFECECEC)
    val MyYellow = Color(0xFFFFBE00)
    val MyBlue = Color(0xFF047FC6)
    var currentIndex by remember { mutableStateOf(0) }
    val lazyListState = rememberLazyListState()
    val lazyListState2 = rememberLazyListState()
    val lazyListState3 = rememberLazyListState()
    var isLogged by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(0) }
    val productsPerPage = 5
    val listaProdottiCompleta by viewModel.listaProdotti.collectAsState()
    val listaRicercheCompleta by viewModel.listaRicercheCompleta.collectAsState()
    var visibile by rememberSaveable { mutableStateOf(false) }
    var pagineRaggiunte by rememberSaveable { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    val images = listOf(
        R.drawable.bellezza,
        R.drawable.sport,
        R.drawable.giardinaggio,
        R.drawable.informatica,
        R.drawable.abbigliamento
    )
    val stringList = listOf(
        "trucchi",
        "sport",
        "giardinaggio",
        "informatica",
        "abbigliamento",

    )
    val images2 = listOf(
        R.drawable.bellezza
    )

    fun loadProductsForPage() {
        coroutineScope.launch(Dispatchers.IO) {
            var start = 0
            if (currentPage == 0) {
                start = (productsPerPage * currentPage) + 1
            } else {
                start = productsPerPage * currentPage
            }
            val end = productsPerPage * (currentPage + 1)
            visibile = true
            if(isRicerca){
                viewModel.searchProducts(keyword)
            }else{
                viewModel.fetchAllProducts(start, end)
            }
            visibile = false
        }
    }
    LaunchedEffect(Unit) {
        if (pagineRaggiunte <= currentPage) {
            loadProductsForPage()
        }
    }

    val paginatedProductListSearh = remember(currentPage,listaRicercheCompleta) {
        listaRicercheCompleta.chunked(productsPerPage).getOrElse(currentPage) { emptyList() } ?: emptyList()
    }

    val paginatedProductList = remember(currentPage,listaProdottiCompleta) {
        listaProdottiCompleta.chunked(productsPerPage).getOrElse(currentPage) { emptyList() } ?: emptyList()
    }



    // Effetto per scrollare all'inizio quando cambia la pagina
    LaunchedEffect(currentPage) {
        lazyListState3.animateScrollToItem(0)
    }

    println("lunghezza lista " +listaProdottiCompleta.size)
    println("pagina corrente "+ currentPage)

    Surface(color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            LaunchedEffect(Unit) {
                while (true) {
                    delay(5000)
                    val nextIndex = (currentIndex + 1) % images.size
                    lazyListState.animateScrollToItem(nextIndex)
                    currentIndex = nextIndex
                }
            }

            LazyColumn(
                state = lazyListState3,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                item {
                    Box {
                        Row(
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
                            ) {
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
                                Row {
                                    Text(
                                        text = "Esclusivo per te",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(start = 5.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.weight(1f))
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
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
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
                                Row {
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
                                        .clickable {
                                            println(stringList[index])
                                            navController.navigate("ProductCategory/${stringList[index]}")
                                        }
                                        .aspectRatio(16 / 9f),
                                    contentAlignment = Alignment.Center

                                ) {
                                    Image(
                                        painter = painterResource(id = imageRes),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier
                                            .border(width = 2.dp, color = Color.White)
                                            .padding(5.dp)
                                    ) {
                                        Text(
                                            text = "Scopri di più", color = Color.White,
                                            fontSize = 45.sp,
                                            fontFamily = FontFamily.Serif
                                        )
                                    }
                                }

                            }
                        }
                    }//FINE BOX
                }//FINE ITEM


                //visualizzazione prodotti
                val productList = if (!isRicerca) paginatedProductList else paginatedProductListSearh
                items(productList) { productInfo ->
                    Box(){
                        Row(
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .padding(start = 5.dp)
                                .padding(end = 5.dp)
                                .clickable {
                                    navController.navigate("prodotto/${productInfo.id}")
                                }
                                .border(
                                    width = 0.5.dp,
                                    color = LightGrayColor,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .width(150.dp)
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
                                    .background(LightGrayColor)
                                ,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                val base64WithoutPrefix = productInfo.immagineProdotto?.removePrefix("data:image/png;base64,")
                                val imageBytes = Base64.decode(base64WithoutPrefix, Base64.DEFAULT)
                                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                val imageBitmap = bitmap.asImageBitmap()

                                Image(
                                    painter = BitmapPainter(imageBitmap),
                                    contentDescription = productInfo.descrizione,
                                    modifier = Modifier
                                )
                            }
                            Column(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                productInfo.descrizione?.let {
                                    Text(
                                        modifier = Modifier.padding(top = 23.dp),
                                        text = it,
                                        style = TextStyle(fontSize = 15.sp),
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))

                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${productInfo.venduti} + acquistati",
                                    style = TextStyle(fontSize = 13.sp),
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "${productInfo.prezzo}€",
                                    style = TextStyle(fontSize = 20.sp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Disponibilità: ${productInfo.disponibilita}",
                                    style = TextStyle(fontSize = 10.sp)
                                )
                                Spacer(modifier = Modifier.height(15.dp))

                                Button(
                                    onClick = {
                                        scope.launch {
                                            carrelloViewModel.addProductToCart(productInfo, 1)
                                            snackbarHostState.showSnackbar("Prodotto aggiunto al carrello!")
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 5.dp, end = 5.dp),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = MyYellow),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(text = "Aggiungi al carrello")
                                }


                            }

                        }
                        CustomSnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }




                if(visibile) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = Color.White)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Caricamento prodotti",
                                    color = MyBlue,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                CircularProgressIndicator(
                                    color = MyBlue,
                                    strokeWidth = 4.dp
                                )
                            }
                        }

                    }

                }
                if(!visibile) {

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    if (currentPage > 0) {
                                        currentPage--
                                    }
                                },
                                enabled = currentPage > 0,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(text = "Pagina Precedente")
                            }

                            val lista = if (!isRicerca) listaProdottiCompleta else listaRicercheCompleta


                            Button(

                                onClick = {

                                    if (currentPage < (lista.size / productsPerPage)) {
                                        if (pagineRaggiunte <= currentPage) {
                                            loadProductsForPage()
                                        }
                                        currentPage++
                                        pagineRaggiunte = currentPage


                                    }
                                },
                                enabled = currentPage < (lista.size / productsPerPage)
                            ) {
                                Text(text = "Pagina Successiva")
                            }
                        }
                    }
                }
            }
        }
    }
}
