package it.unical.ea.lemu_frontend

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import it.unical.ea.lemu_frontend.viewmodels.SearchedUserViewModel
import it.unical.ea.lemu_frontend.viewmodels.UserProfileViewModel
import kotlinx.coroutines.launch
import org.openapitools.client.models.ProdottoDto
import org.openapitools.client.models.RecensioneDto
import org.openapitools.client.models.UtenteDto
import org.openapitools.client.models.WishlistDto

@Composable
fun SearchedUserProfileActivity(
    searchedUserViewModel: SearchedUserViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val userProfile by searchedUserViewModel.userProfile.collectAsState();
    val publicWishlists by searchedUserViewModel.publicWishlists.collectAsState()
    val sharedWishlists by searchedUserViewModel.sharedWishlists.collectAsState()
    val recensioni by searchedUserViewModel.recensioni.collectAsState()


    LaunchedEffect(Unit) {
        coroutineScope.launch {
            searchedUserViewModel.loadWishlists()
            searchedUserViewModel.getPagedReviews()
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
            Spacer(modifier = Modifier.height(16.dp))
            UserReviews(reviews = recensioni, searchedUserViewModel = searchedUserViewModel)
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
    searchedUserViewModel: SearchedUserViewModel,
    navController: NavController
) {
    val wishlist by remember { searchedUserViewModel.wishlistDetails }
    val productDetails by remember { mutableStateOf(searchedUserViewModel.wishlistProductDetails) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(wishlistId) {
        coroutineScope.launch {
            searchedUserViewModel.loadWishlistDetails(wishlistId)
            wishlist?.prodotti?.let { productIds ->
                searchedUserViewModel.loadProductDetails(productIds)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        wishlist?.let {
            Text(
                text = it.nome ?: "Dettagli Wishlist",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (productDetails.isEmpty()) {
                Text(
                    text = "Nessun prodotto disponibile",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            } else {
                LazyColumn {
                    items(productDetails) { prodotto ->
                        ProductCard(prodotto = prodotto, navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(prodotto: ProdottoDto, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                navController.navigate("prodotto/${prodotto.id}")
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            prodotto.immagineProdotto?.let { base64Image ->
                val base64String = if (base64Image.startsWith("data:image/")) {
                    base64Image.substringAfter("base64,")
                } else {
                    base64Image
                }
                val imageBitmap = base64ToImageBitmap(base64String)
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = prodotto.nome,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.placeholder),
                        contentDescription = prodotto.nome,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = prodotto.nome ?: "Nome prodotto",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                prodotto.descrizione?.let {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                prodotto.prezzo?.let {
                    Text(
                        text = "Prezzo: $it",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}






@Composable
fun UserReviews(
    reviews: List<RecensioneDto>,
    searchedUserViewModel: SearchedUserViewModel
) {

    LaunchedEffect(Unit) {
        searchedUserViewModel.getPagedReviews()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Recensioni utente",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (reviews.isEmpty()) {
                Text(
                    text = "Nessuna recensione disponibile",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray
                )
            } else {
                reviews.forEachIndexed { index, review ->
                    ReviewItem(
                        index = searchedUserViewModel.currentPageRecensioni * searchedUserViewModel.pageSize + index + 1,
                        rating = review.rating,
                        name = review.nomeProdotto,
                        comment = review.commento,
                        false,
                        onDeleteClick = { }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { searchedUserViewModel.loadPreviousPageReviews() },
                    enabled = searchedUserViewModel.currentPageRecensioni > 0
                ) {
                    Text("Precedente")
                }

                Button(
                    onClick = { searchedUserViewModel.loadNextPageReviews() },
                    enabled = searchedUserViewModel.currentPageRecensioni < searchedUserViewModel.totalPagesRecensioni - 1
                ) {
                    Text("Successivo")
                }
            }
        }
    }
}

