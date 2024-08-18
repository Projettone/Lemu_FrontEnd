package it.unical.ea.lemu_frontend

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
import it.unical.ea.lemu_frontend.viewmodels.PaymentViewModel
import it.unical.ea.lemu_frontend.viewmodels.SearchedUserViewModel
import it.unical.ea.lemu_frontend.viewmodels.UserProfileViewModel
import org.openapitools.client.models.Utente

class MainActivity : ComponentActivity() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var userProfileViewModel: UserProfileViewModel
    private lateinit var paymentViewModel: PaymentViewModel
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = AuthViewModel(this)
        userProfileViewModel = UserProfileViewModel(authViewModel)
        paymentViewModel = PaymentViewModel(authViewModel)
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
                    Start(authViewModel, userProfileViewModel, signInLauncher, paymentViewModel)
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
           paymentViewModel: PaymentViewModel){
    val navController = rememberNavController()
    var isLogoVisible by rememberSaveable { mutableStateOf(true) }
    var isArrowVisible by rememberSaveable { mutableStateOf(false) }
    var isSearchBarVisible by rememberSaveable { mutableStateOf(true) }
    val isLoggedIn by authViewModel.isLoggedIn
    val searchedUserViewModel = SearchedUserViewModel(authViewModel)

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate("profile")
        }
    }



    Scaffold(
        bottomBar = {
            BottomAppBarActivity(navController = navController, authViewModel = authViewModel)
        },
        topBar = {TopAppBarActivity(
            isLogoVisible = isLogoVisible,
            isArrowVisible = isArrowVisible,
            isSearchBarVisible= isSearchBarVisible,
            navController = navController
        )}
    ) { innerPadding ->
        NavHost(navController,startDestination = "home", modifier = Modifier.padding(innerPadding)) {
            composable("home") {
                //HomePageActivity(navController = navController, productList = null )
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
                CheckoutActivity(authViewModel = authViewModel, navController = navController, paymentViewModel = paymentViewModel)
            }
            composable("searchedUser") {
                SearchedUserProfileActivity(navController = navController, searchedUserViewModel = searchedUserViewModel,
                    userId = 1
                )
            }
            composable("wishlistDetail/{wishlistId}") { backStackEntry ->
                val wishlistId = backStackEntry.arguments?.getString("wishlistId")?.toLong() ?: return@composable
                isLogoVisible = true
                isArrowVisible = true
                WishlistDetailScreen(
                    wishlistId = wishlistId,
                    searchedUserViewModel = searchedUserViewModel
                )
            }
        }
    }
}