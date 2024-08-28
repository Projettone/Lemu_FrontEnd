package it.unical.ea.lemu_frontend

import CategoryViewModel
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.unical.ea.lemu_frontend.viewmodels.AuthViewModel
import kotlinx.coroutines.launch
import org.openapitools.client.models.ProdottoDto
import kotlin.io.encoding.ExperimentalEncodingApi

@Composable
fun ProductsCategory(
    category: String?,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val viewModel: CategoryViewModel = remember {
        CategoryViewModel(authViewModel)
    }
    val products by viewModel.products

    LaunchedEffect(category) {
        viewModel.fetchProductsByCategory(category)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(16.dp)
        ) {
            items(products) { prodottoDto ->
                ProductCard(
                    prodottoDto = prodottoDto,
                    navController = navController,
                    onAddToCartClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Prodotto aggiunto al carrello!")
                        }
                    }
                )
            }
        }
        CustomSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}


@Composable
fun CustomSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        snackbar = { data ->
            CustomSnackbar(data)
        },
        modifier = modifier
    )
}

@Composable
fun CustomSnackbar(data: SnackbarData) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF323232),
            contentColor = contentColorFor(backgroundColor = Color(0xFF323232))
        ),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.Green,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = data.visuals.message,
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun ProductCard(
    prodottoDto: ProdottoDto,
    navController: NavController,
    onAddToCartClick: () -> Unit
) {
    val imageBitmap = remember(prodottoDto.immagineProdotto) {
        decodeBase64Image(prodottoDto.immagineProdotto)
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("prodotto/${prodottoDto.id}")
            }
    ) {
        Column(
            modifier = Modifier.background(color = Color.White)
        ) {
            imageBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }

            prodottoDto.nome?.let {
                Text(
                    text = it,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = "${prodottoDto.venduti} Venduti",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
            )

            Text(
                text = "${prodottoDto.prezzo}€",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )

            Text(
                text = "Disponibilità: ${prodottoDto.disponibilita}",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
            )
        }
    }
}



@OptIn(ExperimentalEncodingApi::class)
fun decodeBase64Image(base64Str: String?): Bitmap? {
    return try {
        if (base64Str.isNullOrEmpty()) {
            null
        } else {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        }
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        null
    }
}

