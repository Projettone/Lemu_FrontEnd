import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.unical.ea.lemu_frontend.R
import it.unical.ea.lemu_frontend.viewmodels.CarrelloViewModel

@Composable
fun CarrelloActivity(navController: NavController, carrelloViewModel: CarrelloViewModel) {
    val cartItems by carrelloViewModel.cartItems.collectAsState()
    val prezzoTotale by carrelloViewModel.totalPrice.collectAsState()  // Ottieni il totale dal ViewModel
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Carrello (${cartItems.sumOf { it.quantity }})",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        if (cartItems.isEmpty()) {
            Image(
                painter = painterResource(id = R.drawable.cart_logo),
                contentDescription = "Carrello Vuoto",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(cartItems) { cartItem ->
                    CartItemCard(
                        cartItem = cartItem,
                        carrelloViewModel = carrelloViewModel,
                        navController = navController
                    )
                }
            }

            if (cartItems.isNotEmpty()) {
                CheckoutSection(
                    totalPrice = prezzoTotale,
                    onCheckout = {
                        navController.navigate("checkout")
                    }
                )
            }
        }
    }
}

@Composable
fun CartItemCard(
    carrelloViewModel: CarrelloViewModel,
    cartItem: CartItem,
    navController: NavController
) {
    val imageBitmap = remember(cartItem.imageRes) {
        val decodedBytes = Base64.decode(cartItem.imageRes, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        bitmap?.asImageBitmap()
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("prodotto/${cartItem.prodottoId}")
            }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            imageBitmap?.let {
                Image(
                    bitmap = it,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .padding(8.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cartItem.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 13.5.sp,
                )
                Text(
                    text = "${cartItem.price} €",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quantità:",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { carrelloViewModel.decrementQuantity(cartItem = cartItem) }, modifier = Modifier.size(40.dp)) {
                        Text(
                            text = "-",
                            fontSize = 17.sp
                        )
                    }
                    Text(
                        text = cartItem.quantity.toString(),
                        fontSize = 12.5.sp
                    )
                    TextButton(onClick = { carrelloViewModel.incrementQuantity(cartItem = cartItem) }, modifier = Modifier.size(40.dp)) {
                        Text(
                            text = "+",
                            fontSize = 17.sp
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF228B22),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Disponibilità immediata",
                        fontSize = 10.sp,
                        color = Color(0xFF228B22)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF228B22),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Venduto e spedito da Lemu",
                        fontSize = 10.sp,
                        color = Color(0xFF228B22)
                    )
                }
            }
            Column {
                IconButton(onClick = {
                    carrelloViewModel.removeItem(cartItem = cartItem)
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Rimuovi",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun CheckoutSection(totalPrice: Double, onCheckout: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Totale: ${String.format("%.2f", totalPrice)}€",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "+ spedizione",
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF228B22),
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = "Pagamento sicuro",
                fontSize = 10.sp,
                color = Color(0xFF228B22)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF228B22),
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = "Resi gratuiti",
                fontSize = 10.sp,
                color = Color(0xFF228B22)
            )
        }
        Button(
            onClick = {onCheckout},
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Procedi all'acquisto")
        }
    }
}

data class CartItem(
    val carrelloProdottoId: Long,
    val prodottoId: Long,
    val imageRes: String,
    val name: String,
    val quantity: Int,
    val price: Float,
    val maxAvailability: Int
)
