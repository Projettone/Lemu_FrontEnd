package it.unical.ea.lemu_frontend

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.navigation.NavController
import it.unical.ea.lemu_frontend.viewmodels.WishlistViewModel

@Composable
fun MainScreen2(
    wishlistId: Long,
    wishlistType: String,
    wishlistViewModel: WishlistViewModel,
    navController: NavController
) {
    println(wishlistId)
    val wishlistItems by wishlistViewModel.wishlistItems.collectAsState()
    val emails by wishlistViewModel.sharedEmails.collectAsState()

    LaunchedEffect(wishlistId) {
        wishlistViewModel.getAllWishlistProdotti(wishlistId)
        wishlistViewModel.getSharedEmails(wishlistId)
    }

    var showEmailDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            Button(
                onClick = { navController.navigate("categorie") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Aggiungi Prodotto"
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text("Aggiungi Prodotto")
            }
        }
    ) { paddingValues ->
        ProductsWishlistActivity(
            wishlistId = wishlistId,
            wishlistItems = wishlistItems[wishlistId] ?: emptyList(),
            wishlistType = wishlistType,
            onRemoveItem = { item ->
                wishlistViewModel.removeItem(wishlistId, item)
            },
            onViewUsers = { showEmailDialog = true },
            modifier = Modifier.padding(paddingValues),
            navController = navController
        )

        if (showEmailDialog) {
            EmailDialog(
                emails = emails,
                onDismiss = { showEmailDialog = false },
                onAddEmail = { email ->
                    if (isValidEmail(email)) {
                        wishlistViewModel.addEmailToWishlist(wishlistId, email)
                        wishlistViewModel.getSharedEmails(wishlistId)
                    }
                },
                onRemoveEmail = { email ->
                    wishlistViewModel.removeEmailFromWishlist(wishlistId, email)
                    wishlistViewModel.getSharedEmails(wishlistId)
                }
            )
        }
    }
}


@Composable
fun ProductsWishlistActivity(
    wishlistId: Long,
    wishlistItems: List<WishlistItem>,
    wishlistType: String,
    onRemoveItem: (WishlistItem) -> Unit,
    onViewUsers: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController
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
            if (wishlistType == "condivisa") {
                IconButton(onClick = onViewUsers) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "View Users"
                    )
                }
            }
        }

        if (wishlistItems.isEmpty()) {
            Image(
                painter = painterResource(id = R.drawable.wishlistprodottivuota),
                contentDescription = "Nessun Prodotto nella Wishlist",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(wishlistItems) { wishlistItem ->
                    WishlistItemCard(
                        wishlistItem = wishlistItem,
                        onRemoveItem = onRemoveItem,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun WishlistItemCard(
    wishlistItem: WishlistItem,
    onRemoveItem: (WishlistItem) -> Unit,
    navController: NavController
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
            .clickable {
                navController.navigate("prodotto/${wishlistItem.id}")
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
                IconButton(onClick = { onRemoveItem(wishlistItem) } ) {
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
    onAddEmail: (String) -> Unit,
    onRemoveEmail: (String) -> Unit
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(2.dp)
                        ) {
                            Text(
                                email,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                            )
                            IconButton(onClick = { onRemoveEmail(email) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove Email",
                                    tint = Color.Red
                                )
                            }
                        }
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
    val id: Long,
    val imageRes: String,
    val name: String,
    val price: Float
)
