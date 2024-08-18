package it.unical.ea.lemu_frontend

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import it.unical.ea.lemu_frontend.viewmodels.SearchedUserViewModel
import kotlinx.coroutines.launch
import org.openapitools.client.models.UtenteDto
import org.openapitools.client.models.WishlistDto

@Composable
fun SearchedUserProfileActivity(
    searchedUserViewModel: SearchedUserViewModel,
    navController: NavController,
    userId: Long
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val userProfile by remember { searchedUserViewModel._userProfile }
    val publicWishlists by searchedUserViewModel.publicWishlists.collectAsState()
    val sharedWishlists by searchedUserViewModel.sharedWishlists.collectAsState()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            searchedUserViewModel.loadUserProfile()
            searchedUserViewModel.loadWishlists(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        userProfile?.let {
            SearchedUserProfileHeader(it)
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Wishlist Pubbliche",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            WishlistList(wishlists = publicWishlists, navController = navController)

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Wishlist Condivise Con Te",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            WishlistList(wishlists = sharedWishlists, navController = navController)
        }
    }
}


@Composable
fun SearchedUserProfileHeader(user: UtenteDto) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(100.dp),
            shape = CircleShape,
            color = Color.Gray
        ) {
            user.immagineProfilo?.let { immagineProfilo ->
                if (immagineProfilo.startsWith("data:image/")) {
                    val base64String = immagineProfilo.substringAfter("base64,")
                    val imageBitmap = base64ToImageBitmap(base64String)
                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.placeholder),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(immagineProfilo),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${user.nome} ${user.cognome}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        user.email?.let { Text(text = it, fontSize = 20.sp, color = Color.Gray) }
    }
}

@Composable
fun WishlistList(wishlists: List<WishlistDto>, navController: NavController) {
    Column {
        if (wishlists.isEmpty()) {
            Text(
                text = "Nessuna wishlist disponibile",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            wishlists.forEach { wishlistItem ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            navController.navigate("wishlistDetail/${wishlistItem.id}")
                        },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = wishlistItem.nome ?: "Wishlist",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Prodotti: ${wishlistItem.prodotti?.size ?: 0}",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun WishlistDetailScreen(
    wishlistId: Long,
    searchedUserViewModel: SearchedUserViewModel
) {
    val wishlist by remember { searchedUserViewModel.wishlistDetails }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(wishlistId) {
        coroutineScope.launch {
            searchedUserViewModel.loadWishlistDetails(wishlistId)
        }
    }

    wishlist?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = it.nome ?: "Dettagli Wishlist",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            it.prodotti?.forEach { prodotto ->
                Text(
                    text = prodotto.toString(),
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
