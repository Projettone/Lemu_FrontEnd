package it.unical.ea.lemu_frontend

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.unical.ea.lemu_frontend.viewmodels.SearchedUserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openapitools.client.models.UtenteDto

@Composable
fun TopAppBarActivity(
    isLogoVisible: Boolean,
    isArrowVisible: Boolean,
    navController: NavController,
    isSearchBarVisible: Boolean,
    searchedUserViewModel: SearchedUserViewModel,
    onSearch: (String) -> Unit,
) {
    var text by remember { mutableStateOf("") }
    //var showOverlay by rememberSaveable { mutableStateOf(false) }
    var overlayText by remember { mutableStateOf("") }

    val showOverlay by searchedUserViewModel.showOverlay.collectAsState()

    val startColor = Color(0xFF0077B6) // Celeste scuro
    val endColor = Color(0xFF83F5F9) // Celeste più chiaro
    val coroutineScope = rememberCoroutineScope()
    val listaUtentiCercati by searchedUserViewModel.searchedUsers.collectAsState()

    DisposableEffect(Unit) {

        onDispose {
            searchedUserViewModel.showOverlayfalse()
        }
    }

    fun ricercaUtenti() {
        coroutineScope.launch(Dispatchers.IO) {
            searchedUserViewModel.searchUsers(overlayText)
        }
    }

    Column {
        if (isLogoVisible) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                startColor,
                                endColor
                            )
                        )
                    )
            ){

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    startColor,
                                    endColor
                                )
                            )
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logolemu),
                        contentDescription = null,
                        modifier = Modifier
                            .height(55.dp)
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .clickable {
                                navController.navigate("home")
                            }

                    )
                }
            }
        }
        if(isSearchBarVisible) {
            Row(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                endColor, // Colore di inizio
                                startColor // Colore di fine
                            )
                        )
                    )
                    .padding(start = 10.dp, bottom = 5.dp, end = 10.dp, top = 5.dp),
                verticalAlignment = Alignment.CenterVertically, // Allinea verticalmente al centro
                horizontalArrangement = Arrangement.Start // Allinea orizzontalmente all'inizio
            ) {
                if (isArrowVisible) {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back Arrow",
                            tint = Color.Black,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(5.dp)) // Aggiungi uno spazio tra l'icona e il TextField
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween // Spazia gli elementi

                ) {
                    TextField(
                        modifier = Modifier
                            .weight(1f) // Occupa tutto lo spazio disponibile
                            .height(50.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = Color.White),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = startColor
                        ),
                        value = text,
                        onValueChange = { newText ->
                            text = newText
                        },
                        placeholder = {
                            Text("Cerca qui...", color = Color.Gray)
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                // Action when search button is pressed on the keyboard
                                onSearch(text)
                            }
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search Icon",
                                tint = Color.Black
                            )
                        }
                    )
                    Spacer(modifier = Modifier.width(9.dp))


                    Image(
                        painter = painterResource(id = R.drawable.ricercautente),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(35.dp)
                            .clickable { navController.navigate("ricercaUtente") } // Rendi l'icona cliccabile per mostrare l'overlay
                    )
                }
            }
        }

        if (showOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.1f)) // Semi-transparent background
                    .wrapContentSize(align = Alignment.Center)
                    .padding(bottom = 30.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .fillMaxWidth(0.9f)
                        .height(600.dp)
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Cerca Utente",
                        color = Color.Black,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    TextField(
                        value = overlayText,
                        onValueChange = { newText ->
                            overlayText = newText
                        },
                        placeholder = {
                            Text("Cerca utente...")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search Icon",
                                tint = Color.Black
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                ricercaUtenti()

                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Box to contain the scrollable content
                    Box(
                        modifier = Modifier
                            .weight(1f) // Takes up remaining space
                            .fillMaxWidth()
                    ) {
                        LazyColumn {
                            items(listaUtentiCercati) { user ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .clickable {
                                            searchedUserViewModel._userProfile.value = user
                                            searchedUserViewModel.showOverlayfalse()
                                            navController.navigate("searchedUser")
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.user_logo), // Replace with actual user image resource
                                        contentDescription = "User Image",
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(Color.Gray)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = user.email!!,
                                        style = MaterialTheme.typography.body1
                                    )
                                }
                            }
                        }

                    }
                    Spacer(modifier = Modifier.height(50.dp))

                    // The Close button is positioned outside the scrollable content
                    Button(
                        onClick = { searchedUserViewModel.showOverlayfalse()},
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 16.dp) // Add bottom padding to avoid overlap
                    ) {
                        Text("Chiudi")
                    }
                }
            }
        }
    }
}



@Composable
fun UserListItem(user: UtenteDto, searchedUserViewModel: SearchedUserViewModel, navController: NavController) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                searchedUserViewModel._userProfile.value = user
                searchedUserViewModel.showOverlayfalse()
                navController.navigate("searchedUser")
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.user_logo), // Replace with actual user image resource
            contentDescription = "User Image",
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.Gray)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = user.email!!,
            style = MaterialTheme.typography.body1
        )
    }
}

// Define a data class for User
data class User(val name: String)
