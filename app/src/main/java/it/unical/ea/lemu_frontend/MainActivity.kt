package it.unical.ea.lemu_frontend

import CarrelloActivity
import LoginActivity
import RegistrationActivity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.facebook.FacebookSdk
import it.unical.ea.lemu_frontend.ui.theme.Lemu_FrontEndTheme
import it.unical.ea.lemu_frontend.viewmodels.AuthViewModel
import it.unical.ea.lemu_frontend.viewmodels.CarrelloViewModel
import it.unical.ea.lemu_frontend.viewmodels.OrdineViewModel
import it.unical.ea.lemu_frontend.viewmodels.PaymentViewModel
import it.unical.ea.lemu_frontend.viewmodels.ProdottoViewModel
import it.unical.ea.lemu_frontend.viewmodels.SearchedUserViewModel
import it.unical.ea.lemu_frontend.viewmodels.UserProfileViewModel
import it.unical.ea.lemu_frontend.viewmodels.WishlistViewModel
import org.openapitools.client.models.Utente

class MainActivity : ComponentActivity() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var userProfileViewModel: UserProfileViewModel
    private lateinit var paymentViewModel: PaymentViewModel
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>
    private lateinit var prodottoViewModel: ProdottoViewModel
    private lateinit var carrelloViewModel: CarrelloViewModel
    private lateinit var ordineViewModel: OrdineViewModel
    private lateinit var searchedUserViewModel: SearchedUserViewModel




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = AuthViewModel(this)
        userProfileViewModel = UserProfileViewModel(authViewModel)
        paymentViewModel = PaymentViewModel(authViewModel)
        prodottoViewModel = ProdottoViewModel(authViewModel)
        carrelloViewModel = CarrelloViewModel(authViewModel)
        ordineViewModel = OrdineViewModel(authViewModel,carrelloViewModel)
        searchedUserViewModel = SearchedUserViewModel(authViewModel)
        signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            authViewModel.handleSignInResult(result.data)
        }
        FacebookSdk.sdkInitialize(applicationContext)

        setContent {
            Lemu_FrontEndTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Start(authViewModel, userProfileViewModel, signInLauncher, paymentViewModel, prodottoViewModel, ordineViewModel, carrelloViewModel, searchedUserViewModel)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        authViewModel.handleActivityResult(requestCode, resultCode, data)
    }
}

@Composable
fun Start( authViewModel: AuthViewModel,
           userProfileViewModel: UserProfileViewModel,
           signInLauncher: ActivityResultLauncher<Intent>,
           paymentViewModel: PaymentViewModel,
           prodottoViewModel: ProdottoViewModel,
           ordineViewModel: OrdineViewModel,
           carrelloViewModel: CarrelloViewModel,
           searchedUserViewModel: SearchedUserViewModel){
    val navController = rememberNavController()
    var isLogoVisible by rememberSaveable { mutableStateOf(true) }
    var isArrowVisible by rememberSaveable { mutableStateOf(false) }
    var isSearchBarVisible by rememberSaveable { mutableStateOf(true) }
    val isLoggedIn by authViewModel.isLoggedIn
    var searchKeyword by remember { mutableStateOf("") }
    var selectedIconIndex by remember { mutableStateOf(1) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    selectedIconIndex = when (currentRoute) {
        "profile" -> 3
        "checkout" -> 4
        "home" -> 1
        "categorie" -> 2
        "ProductsWishlist/{wishlistId}/{wishlistType}" -> 5

        else -> 1
    }
    //val carrelloViewModel = remember { CarrelloViewModel(authViewModel = authViewModel)}
    val wishlistViewModel = remember { WishlistViewModel(authViewModel)}

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate("profile")
        }
    }



    Scaffold(
        bottomBar = {
            BottomAppBarActivity(navController = navController, authViewModel = authViewModel, carrelloViewModel = carrelloViewModel, wishlistViewModel = wishlistViewModel)
        },
        topBar = {TopAppBarActivity(
            isLogoVisible = isLogoVisible,
            isArrowVisible = isArrowVisible,
            isSearchBarVisible= isSearchBarVisible,
            navController = navController,
            searchedUserViewModel = searchedUserViewModel,
            onSearch = { keyword ->
                searchKeyword = keyword
                navController.navigate("homeSearch")
            }
        )}
    ) { innerPadding ->
        NavHost(navController,startDestination = "home", modifier = Modifier.padding(innerPadding)) {
            composable("home") {
                HomePageActivity(navController = navController, viewModel = prodottoViewModel,isRicerca = false, keyword = "*", carrelloViewModel = carrelloViewModel)
            }
            composable("homeSearch"){
                HomePageActivity(navController = navController, viewModel = prodottoViewModel, isRicerca = true, keyword = searchKeyword, carrelloViewModel = carrelloViewModel)
            }
            composable("addProduct"){
                AddProductActivity(authViewModel = authViewModel, navController = navController, prodottoViewModel = prodottoViewModel)
            }
            composable("prodotto/{productId}") { backStackEntry ->
                val productIdString = backStackEntry.arguments?.getString("productId")

                if (productIdString != null) {
                    ProductViewActivity(productIdString = productIdString, navController = navController , viewModel = prodottoViewModel, carrelloViewModel = carrelloViewModel, authViewModel = authViewModel, wishlistViewModel = wishlistViewModel)
                }
            }
            composable("ordini"){
                OrdiniActivity(navController = navController, viewModel = ordineViewModel)
            }
            composable("dettagliOrdine/{id}") {backStackEntry ->
                val ordineIdString = backStackEntry.arguments?.getString("id")

                if (ordineIdString != null) {
                    DettagliOrdineActivity(idOrdine = ordineIdString, viewModelProduct = prodottoViewModel, viewModelOrder = ordineViewModel, navController = navController)
                }
            }
            composable("categorie"){
                CategoryActivity(navController = navController, authViewModel = authViewModel)
            }
            composable("ProductCategory/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category")
                ProductsCategory(category = category, navController = navController, authViewModel = authViewModel)
            }
            composable("login"){
                isLogoVisible = true
                isArrowVisible = true
                LoginActivity(navController = navController,
                    authViewModel = authViewModel,
                    signInLauncher = signInLauncher)
            }
            composable("registration") {
                isLogoVisible = true
                isArrowVisible = true
                RegistrationActivity(navController = navController,
                    authViewModel = authViewModel)
            }
            composable("profile") {
                isLogoVisible = true
                isArrowVisible = true
                UserProfileActivity(authViewModel = authViewModel, userProfileViewModel = userProfileViewModel, navController = navController)
            }
            composable("checkout") {
                CheckoutActivity(authViewModel = authViewModel, carrelloViewModel = carrelloViewModel, navController = navController, paymentViewModel = paymentViewModel, ordineViewModel = ordineViewModel)
            }
            composable("searchedUser") {
                SearchedUserProfileActivity(navController = navController, searchedUserViewModel = searchedUserViewModel
                )
            }
            composable("wishlist") {
               MainScreen1(navController = navController, viewModel = wishlistViewModel)
            }
            composable("ProductsWishlist/{wishlistId}/{wishlistType}") { backStackEntry ->
                val wishlistId = backStackEntry.arguments?.getString("wishlistId")?.toLongOrNull() ?: 0
                val wishlistType = backStackEntry.arguments?.getString("wishlistType") ?: "default"
                MainScreen2(wishlistId, wishlistType, wishlistViewModel, navController)
            }
            composable("wishlistDetail/{wishlistId}") { backStackEntry ->
                val wishlistId = backStackEntry.arguments?.getString("wishlistId")?.toLong() ?: return@composable
                isLogoVisible = true
                isArrowVisible = true
                WishlistDetailScreen(
                    wishlistId = wishlistId,
                    searchedUserViewModel = searchedUserViewModel,
                    navController = navController
                )
            }
            composable("carrello") {
                CarrelloActivity(navController = navController, carrelloViewModel = carrelloViewModel)
            }
            composable("ricercaUtente"){
                SearchedUser(searchedUserViewModel = searchedUserViewModel, navController = navController )
            }
            composable("productUser"){
                ProductUserActivity(authViewModel = authViewModel, viewModel = prodottoViewModel, navController = navController)
            }
        }
    }
}