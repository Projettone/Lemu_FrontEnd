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
                        
                    }//FINE BOX
                }//FINE ITEM


            }// FINE LAZYCOLUM INIZIALE
        }
    }
}



