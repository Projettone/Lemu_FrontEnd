package it.unical.ea.lemu_frontend

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.unical.ea.lemu_frontend.viewmodels.SearchedUserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun SearchedUser(
    searchedUserViewModel: SearchedUserViewModel,
    navController: NavController,
    ){
    var overlayText by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val listaUtentiCercati by searchedUserViewModel.searchedUsers.collectAsState()
    fun ricercaUtenti() {
        coroutineScope.launch(Dispatchers.IO) {
            searchedUserViewModel.searchUsers(overlayText)
        }
    }


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
                .fillMaxWidth(0.9f) // Slightly less than full width
                .height(600.dp)
                .padding(20.dp)
            // Column will grow to fit its content but not extend beyond the screen.
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
                        println("ciaoo " + overlayText)
                        ricercaUtenti()
                        //onSearch(overlayText)
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
                                    searchedUserViewModel.userProfile.value = user
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
                onClick = {navController.navigate("home")},
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp) // Add bottom padding to avoid overlap
            ) {
                Text("Chiudi")
            }
        }
    }
}
