package it.unical.ea.lemu_frontend


import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.unical.ea.lemu_frontend.viewmodels.WishlistViewModel

@Composable
fun MainScreen2(wishlistId: Long,  wishlistViewModel: WishlistViewModel) {
    println(wishlistId)
    val wishlistItems by wishlistViewModel.wishlistItems.collectAsState()

    // Usa ViewModel per recuperare i dati dei prodotti basati su wishlistId
    LaunchedEffect(wishlistId) {
        wishlistViewModel.getAllWishlistProdotti(wishlistId)
    }

    val emails = remember { mutableStateListOf<String>() }
    var showEmailDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            Button(
                onClick = { /* Implement add product logic here */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Product"
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text("Aggiungi Prodotto")
            }
        }
    ) { paddingValues ->
        WishlistActivity(
            wishlistItems = wishlistItems[wishlistId] ?: emptyList(),
            onRemoveItem = { item -> /* Handle remove item */ },
            onViewUsers = { showEmailDialog = true },
            modifier = Modifier.padding(paddingValues)
        )

        if (showEmailDialog) {
            EmailDialog(
                emails = emails,
                onDismiss = { showEmailDialog = false },
                onAddEmail = { email ->
                    if (isValidEmail(email)) {
                        emails.add(email)
                    }
                }
            )
        }
    }
}


@Composable
fun WishlistActivity(
    wishlistItems: List<WishlistItem>,
    onRemoveItem: (WishlistItem) -> Unit,
    onViewUsers: () -> Unit, // Added to handle the user icon click
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Wishlist (${wishlistItems.size})",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
            IconButton(onClick = onViewUsers) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "View Users"
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(wishlistItems) { wishlistItem ->
                WishlistItemCard(
                    wishlistItem = wishlistItem,
                    onRemoveItem = { onRemoveItem(wishlistItem) }
                )
            }
        }
    }
}

@Composable
fun WishlistItemCard(
    wishlistItem: WishlistItem,
    onRemoveItem: () -> Unit
) {
    val imageBitmap = remember(wishlistItem.imageRes) {
        val decodedBytes = Base64.decode(wishlistItem.imageRes, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        bitmap?.asImageBitmap()
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
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
                    text = wishlistItem.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 13.5.sp,
                )
                Text(
                    text = "${wishlistItem.price} €",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
            Column() {
                IconButton(onClick = { /* Implement share logic here */ }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.Black
                    )
                }
                IconButton(onClick = onRemoveItem) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun EmailDialog(
    emails: List<String>,
    onDismiss: () -> Unit,
    onAddEmail: (String) -> Unit
) {
    var emailInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Gestisci Utenti") },
        text = {
            Column {
                Text("Lista Email:")
                LazyColumn {
                    items(emails) { email ->
                        Text(email, fontSize = 14.sp, modifier = Modifier.padding(2.dp))
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
                TextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Nuova Email") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Email
                    )
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (isValidEmail(emailInput)) {
                    onAddEmail(emailInput)
                    emailInput = "" // Clear the input field
                }
            }) {
                Text("Aggiungi")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Chiudi")
            }
        }
    )
}

fun isValidEmail(email: String): Boolean {
    return email.contains("@") && email.contains(".")
}

data class WishlistItem(
    val imageRes: String,
    val name: String,
    val price: Float,
)
