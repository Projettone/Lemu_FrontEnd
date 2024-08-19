package it.unical.ea.lemu_frontend

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun ProductsCategory(category: String?) {
    // Filtra i prodotti in base alla categoria selezionata, se applicabile
    val products = listOf(
        Product(R.drawable.cart_logo, "Prodotto 1", 100, 4.5, 50, 10.00, "Categoria1"),
        Product(R.drawable.cart_logo, "Prodotto 2", 80, 4.0, 30, 20.00, "Categoria2"),
        Product(R.drawable.cart_logo, "Prodotto 3", 120, 4.2, 60, 15.00, "Categoria1"),
        Product(R.drawable.cart_logo, "Prodotto 4", 100, 4.5, 50, 10.00, "Categoria2"),
        Product(R.drawable.cart_logo, "Prodotto 5", 80, 4.0, 30, 20.00, "Categoria1"),
        Product(R.drawable.cart_logo, "Prodotto 6", 120, 4.2, 60, 15.00, "Categoria2"),
        // Aggiungi altri prodotti qui...
    ).filter { it.category == category}

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(16.dp)
        ) {
            items(products) { product ->
                ProductCard(product) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Prodotto aggiunto al carrello!")
                    }
                }
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
                text = data.visuals.message, // Aggiorna qui l'accesso al messaggio
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun ProductCard(product: Product, onAddToCartClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.background(color = Color.White)
        ) {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Text(
                text = product.name,
                fontSize = 16.sp,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = "${product.sold} Venduti | ★${product.rating}(${product.reviews})",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
            )
            Text(
                text = "${product.price}€",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End // Allinea gli elementi alla destra della Row
            ) {
                IconButton(
                    onClick = { onAddToCartClick() },
                    modifier = Modifier.size(24.dp) // Riduci la dimensione dell'IconButton
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Add to Cart",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp) // Riduci la dimensione dell'Icon
                    )
                }
                Spacer(modifier = Modifier.width(8.dp)) // Aggiungi uno spazio tra le icone
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.size(24.dp) // Riduci la dimensione dell'IconButton
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp) // Riduci la dimensione dell'Icon
                    )
                }
            }
        }
    }
}

data class Product(
    val imageRes: Int,
    val name: String,
    val sold: Int,
    val rating: Double,
    val reviews: Int,
    val price: Double,
    val category: String // Aggiungi il campo categoria
)

