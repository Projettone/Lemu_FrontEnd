package it.unical.ea.lemu_frontend

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.unical.ea.lemu_frontend.viewmodels.AuthViewModel
import it.unical.ea.lemu_frontend.viewmodels.ProdottoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ProductUserActivity(authViewModel: AuthViewModel, viewModel: ProdottoViewModel, navController: NavController) {
    val listaProdottiUtente by viewModel.listaProdottiUser.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var visibile by rememberSaveable { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        authViewModel.user.value?.id?.let { viewModel.getProductByIdUser(it) }
        loading = false
    }

    fun load() {
        coroutineScope.launch(Dispatchers.IO) {
            authViewModel.user.value?.id?.let { viewModel.getProductByIdUser(it) }
        }
        loading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "I tuoi prodotti in vendita..",
            fontSize = 28.sp,
            color = Color(0xFF333333),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF6200EE))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                if(listaProdottiUtente.isNotEmpty()) {
                    items(listaProdottiUtente) { prodotto ->
                        val coroutineScope = rememberCoroutineScope()

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { navController.navigate("prodotto/${prodotto.id}") }
                                .padding(1.dp)
                                .shadow(4.dp)
                                .border(1.dp, Color.Black)
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val base64WithoutPrefix =
                                        prodotto.immagineProdotto?.removePrefix("data:image/png;base64,")
                                    val imageBytes = Base64.decode(base64WithoutPrefix, Base64.DEFAULT)
                                    val bitmap =
                                        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                    val imageBitmap = bitmap.asImageBitmap()

                                    Image(
                                        painter = BitmapPainter(imageBitmap),
                                        contentDescription = prodotto.descrizione,
                                        modifier = Modifier
                                            .size(72.dp)
                                            .padding(end = 12.dp)
                                            .border(2.dp, Color(0xFF6200EE))
                                            .padding(4.dp),
                                        contentScale = ContentScale.Crop
                                    )

                                    Column {
                                        prodotto.nome?.let {
                                            Text(
                                                text = it,
                                                fontSize = 20.sp,
                                                color = Color(0xFF6200EE),
                                                modifier = Modifier.padding(bottom = 4.dp)
                                            )
                                        }
                                        prodotto.prezzo?.let {
                                            Text(
                                                text = "$it€",
                                                fontSize = 18.sp,
                                                color = Color(0xFF666666)
                                            )
                                        }
                                    }
                                }

                                Button(
                                    onClick = {
                                        coroutineScope.launch(Dispatchers.IO) {
                                            viewModel.deleteProduct(prodotto.id!!)
                                            load()
                                        }
                                    },
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Text(text = "Elimina", color = Color.White)
                                }
                            }
                        }
                    }
                }else{
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            androidx.compose.material.Text(
                                text = "Non hai ancora prodotti in vendità",
                                fontSize = 24.sp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }


            }
        }
    }
}
