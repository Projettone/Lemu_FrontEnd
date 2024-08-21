package it.unical.ea.lemu_frontend

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import it.unical.ea.lemu_frontend.viewmodels.OrdineViewModel
import it.unical.ea.lemu_frontend.viewmodels.ProdottoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun OrdiniActivity(navController: NavHostController, viewModel: OrdineViewModel) {

    val coroutineScope = rememberCoroutineScope()
    val orders by viewModel.ordini.collectAsState()
    var text by remember { mutableStateOf("") }


    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            viewModel.GetOrderbyIdUente(1L)

        }
    }

    fun fintOrderByData(data: String){
        coroutineScope.launch(Dispatchers.IO) {
            if(data != ""){
                viewModel.filtraOrdiniPerData(data)
            }else{
                viewModel.GetOrderbyIdUente(1L)
            }

        }
    }

    Column{
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "I miei ordini", style = TextStyle(fontSize = 25.sp),
            modifier = Modifier.padding(9.dp),
            fontWeight = FontWeight.Bold,

            )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(0.1.dp, Color.Gray)
                .height(49.dp)
                .fillMaxWidth()
        ) {
            TextField(
                value = text,
                placeholder = {
                    Text("Ricerca tutti gli ordini in base alla data", color = Color.Gray, style = TextStyle(fontSize = 13.sp),
                        modifier = Modifier.fillMaxSize()
                    )
                },
                onValueChange = { newText -> text = newText
                                fintOrderByData(text)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search Icon",
                        tint = Color.Black
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()

            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(orders) { order ->

                Column(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .background(color = Color.White)
                        .padding(start = 10.dp, end = 10.dp)
                        //.border(0.1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                        .padding(5.dp)
                        .fillMaxWidth()

                ) {
                    Divider(
                        color = Color.Gray,
                        modifier = Modifier
                            .height(0.4.dp)
                            .fillMaxWidth()
                    )
                    Text(text = "Numero ordine: ${order.id}",
                        color = Color.Gray
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Data di acquisto: ${order.dataAcquisto}")
                        Spacer(modifier = Modifier.weight(1f))


                        Divider(
                            color = Color.Gray,
                            modifier = Modifier
                                .height(25.dp)
                                .width(0.6.dp)
                                .absoluteOffset(0.dp,-7.dp)
                        )
                        Spacer(modifier = Modifier.weight(1f))


                        Row(
                            Modifier.clickable {
                                navController.navigate("dettagliOrdine/${order.id}")
                            }
                        ){
                            Text(text = "Dettagli ordine")
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Arrow Forward",
                                tint = Color.Black,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .size(20.dp)
                            )
                        }


                    }
                    Divider(
                        color = Color.Gray,
                        modifier = Modifier
                            .height(0.4.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}


