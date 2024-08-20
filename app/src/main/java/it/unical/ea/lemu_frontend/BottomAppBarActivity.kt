package it.unical.ea.lemu_frontend

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import it.unical.ea.lemu_frontend.viewmodels.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun BottomAppBarActivity(navController: NavController, authViewModel: AuthViewModel) {
    val colorDivider = Color(0xFF0077B6)
    var selectedIconIndex by remember { mutableStateOf(1) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    selectedIconIndex = when (currentRoute) {
        "profile" -> 3
        "login" -> 3
        "ordini" -> 4
        "home" -> 1
        "categories" -> 2
        "wishlist" -> 5
        "dettagliOrdine/{id}" -> 4
        "carrello" -> 4
        else -> 1
    }

    NavigationBar(
        modifier = Modifier
            .height(56.dp)
            .background(Color.Gray)
    ) {
        NavigationBarItem(
            selected = selectedIconIndex == 1,
            onClick = {
                navController.navigate("home")
            },
            icon = {
                IconWithIndicator(
                    isSelected = selectedIconIndex == 1,
                    painterResource(id = R.drawable.homepage_logo),
                    colorDivider,
                    "Icona personalizzata"
                )
            }
        )
        NavigationBarItem(
            selected = selectedIconIndex == 2,
            onClick = {
                navController.navigate("Categorie")
            },
            icon = {
                IconWithIndicator(
                    isSelected = selectedIconIndex == 2,
                    painterResource(id = R.drawable.categoria_logo),
                    colorDivider,
                    "Icona personalizzata"
                )
            }
        )
        NavigationBarItem(
            selected = selectedIconIndex == 3,
            onClick = {
                if (authViewModel.checkAuthentication()) {
                    navController.navigate("profile")
                } else {
                    navController.navigate("login")
                }
            },
            icon = {
                IconWithIndicator(
                    isSelected = selectedIconIndex == 3,
                    painterResource(id = R.drawable.user_logo),
                    colorDivider,
                    "Icona personalizzata"
                )
            }
        )
        NavigationBarItem(
            selected = selectedIconIndex == 4,
            onClick = {
                navController.navigate("Carrello")
            },
            icon = {
                IconWithIndicator(
                    isSelected = selectedIconIndex == 4,
                    painterResource(id = R.drawable.cart_logo),
                    colorDivider,
                    "Icona personalizzata"
                )
            }
        )
        NavigationBarItem(
            selected = selectedIconIndex == 5,
            onClick = {
                navController.navigate("Wishlist")
            },
            icon = {
                IconWithIndicator(
                    isSelected = selectedIconIndex == 5,
                    painterResource(id = R.drawable.wishlist_logo),
                    colorDivider,
                    "Icona personalizzata"
                )
            }
        )
    }
}


@Composable
fun IconWithIndicator(
    isSelected: Boolean,
    painter: Painter,
    colorDivider: Color,
    contentDescription: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .height(5.dp)
                    .width(40.dp)
                    .background(colorDivider, shape = RoundedCornerShape(4.dp))
            )
        }
        Image(
            painter = painter,
            contentDescription = contentDescription,
            colorFilter = if (isSelected) ColorFilter.tint(colorDivider) else null,
            modifier = Modifier
                .size(35.dp)
        )
    }
}