package it.unical.ea.lemu_frontend


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.unical.ea.lemu_frontend.viewmodels.WishlistViewModel


@Composable
fun MainScreen1(navController: NavController, viewModel: WishlistViewModel) {
    // Observe the wishlists from the ViewModel
    val wishlists by viewModel.wishlists.collectAsState()
    val wishlistItems by viewModel.wishlistItems.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    // Funzione per gestire il click sulla wishlist
    val onWishlistClick: (Long) -> Unit = { wishlistId ->
        viewModel.getAllWishlistProdotti(wishlistId) // Richiama il metodo nel ViewModel
        navController.navigate("ProductsWishlist/$wishlistId")
    }

    // Updated onAddWishlist to use ViewModel
    val onAddWishlist: (String, String) -> Unit = { name, type ->
        viewModel.addWishlist(name, type)
        showDialog = false
        snackbarMessage = "Wishlist aggiunta con successo!"
        showSnackbar = true
    }

    // Updated onRemoveWishlist to use ViewModel
    val onRemoveWishlist: (Wishlist) -> Unit = { wishlist ->
        viewModel.removeWishlist(wishlist)
        snackbarMessage = "Wishlist rimossa con successo!"
        showSnackbar = true
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        WishlistActivity(
            wishlists = wishlists,
            onRemoveWishlist = onRemoveWishlist,
            onAddWishlist = { showDialog = true },
            onWishlistClick = onWishlistClick,
            modifier = Modifier.padding(paddingValues)
        )

        if (showDialog) {
            WishlistDialog(
                onDismiss = { showDialog = false },
                onConfirm = onAddWishlist
            )
        }

        if (showSnackbar) {
            LaunchedEffect(snackbarMessage) {
                snackbarHostState.showSnackbar(snackbarMessage)
                showSnackbar = false
            }
        }
    }
}

@Composable
fun WishlistActivity(
    wishlists: List<Wishlist>,
    onRemoveWishlist: (Wishlist) -> Unit,
    onAddWishlist: () -> Unit,
    onWishlistClick: (Long) -> Unit,
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
                text = "Wishlist (${wishlists.size})",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
            IconButton(onClick = onAddWishlist) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Wishlist"
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(wishlists) { wishlist ->
                WishlistCard(
                    wishlist = wishlist,
                    onRemoveWishlist = { onRemoveWishlist(wishlist) },
                    onWishlistClick = { onWishlistClick(wishlist.id) }
                )
            }
        }
    }
}

@Composable
fun WishlistCard(
    wishlist: Wishlist,
    onRemoveWishlist: () -> Unit,
    onWishlistClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onWishlistClick)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.cart_logo),
                contentDescription = "Immagine Wishlist",
                modifier = Modifier.size(56.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Text(
                    text = wishlist.nome,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Prodotti: ${wishlist.id}",
                    fontSize = 14.sp
                )
                Text(
                    text = "Tipo: ${wishlist.tipo}",
                    fontSize = 14.sp,
                    color = when (wishlist.tipo) {
                        "pubblica" -> Color.Green
                        "privata" -> Color.Red
                        else -> Color.Blue
                    }
                )
            }

            IconButton(onClick = onRemoveWishlist) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Rimuovi",
                    tint = Color.Red
                )
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Aggiungi Wishlist") },
        text = {
            Column {
                // Input per il nome della wishlist
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { /* Handle the done action */ }
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Tipo")
                Column(modifier = Modifier.fillMaxWidth()) {
                    RadioButtonWithText(
                        text = "Pubblica",
                        selected = type == "pubblica",
                        onClick = { type = "pubblica" }
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    RadioButtonWithText(
                        text = "Privata",
                        selected = type == "privata",
                        onClick = { type = "privata" }
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    RadioButtonWithText(
                        text = "Condivisa",
                        selected = type == "condivisa",
                        onClick = { type = "condivisa" }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank()) {
                    onConfirm(name, type)
                }
            }) {
                Text("Conferma")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Annulla")
            }
        }
    )
}

@Composable
fun RadioButtonWithText(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        androidx.compose.material3.RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(text = text)
    }
}



@Preview
@Composable
fun WishlistDialogPreview() {
    WishlistDialog(
        onDismiss = { /* No-op */ },
        onConfirm = { name, type -> /* Handle confirm */ }
    )
}


data class Wishlist(
    val nome: String,
    val id: Long,
    val tipo: String // "pubblica", "privata", o "condivisa"
)