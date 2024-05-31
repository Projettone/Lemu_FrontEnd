package it.unical.ea.lemu_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.unical.ea.lemu_frontend.ui.theme.Lemu_FrontEndTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lemu_FrontEndTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Start()
                }
            }
        }
    }
}

@Composable
fun Start(){
    val navController = rememberNavController()
    var isLogoVisible by rememberSaveable { mutableStateOf(true) }
    var isArrowVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomAppBarActivity(navController = navController)
        },
        topBar = {TopAppBarActivity(
            isLogoVisible = isLogoVisible,
            isArrowVisible = isArrowVisible,
            navController = navController
        )}
    ) { innerPadding ->
        NavHost(navController,startDestination = "home", modifier = Modifier.padding(innerPadding)) {
            composable("home") {

            }
        }
    }
}