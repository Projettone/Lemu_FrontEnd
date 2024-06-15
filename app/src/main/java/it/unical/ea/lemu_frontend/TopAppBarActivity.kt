package it.unical.ea.lemu_frontend

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun TopAppBarActivity(isLogoVisible: Boolean, isArrowVisible: Boolean,navController: NavController, ) {
    var text by remember { mutableStateOf("") }
    val startColor = Color(0xFF0077B6) // Celeste scuro
    val endColor = Color(0xFF83F5F9) // Celeste più chiaro

    Column{
        if (isLogoVisible) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                        .padding(top = 5.dp)
                )
            }
        }


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
                .padding(start = 10.dp, bottom = 5.dp, end = 10.dp, top = 5.dp ),
            //.padding(horizontal = 16.dp, vertical = 8.dp), // Aggiungi padding interno alla Row
            verticalAlignment = Alignment.CenterVertically, // Allinea verticalmente al centro
            horizontalArrangement = Arrangement.Start // Allinea orizzontalmente all'inizio
        ) {
            if(isArrowVisible) {
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

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = Color.White)
                ,
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
                        // For example, trigger search functionality
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
        }
    }
}

