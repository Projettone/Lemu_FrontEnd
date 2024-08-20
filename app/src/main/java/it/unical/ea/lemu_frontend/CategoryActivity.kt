package it.unical.ea.lemu_frontend

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun CategoryActivity(navController: NavController) {
    // Usa rememberSaveable per memorizzare la categoria selezionata
    var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }

    // Se una categoria è stata selezionata, mostra la schermata dei prodotti
    if (selectedCategory != null) {
        ProductsCategory(category = selectedCategory)
    } else {
        // Altrimenti, mostra la schermata delle categorie
        val numberOfRows = 6 // Numero di righe per colonna
        val numberOfImagesPerRow = 3 // Numero di immagini per riga
        val spacingBetweenImages = 16.dp // Spazio tra le immagini

        // Lista di risorse delle immagini
        val imageList = listOf(
            R.drawable.abbigliamento,
            R.drawable.abbigliamento,
            R.drawable.abbigliamento,
            R.drawable.abbigliamento,
            R.drawable.abbigliamento,
            R.drawable.abbigliamento,
            R.drawable.abbigliamento,
            R.drawable.abbigliamento,
            R.drawable.giardinaggio,
            R.drawable.abbigliamento,
            R.drawable.abbigliamento,
            R.drawable.abbigliamento,
            R.drawable.informatica,
            R.drawable.abbigliamento,
            R.drawable.sport
            // Aggiungi altre risorse delle immagini qui...
        )

        // Lista di testi associati alle immagini
        val textList = listOf(
            "Abbigliamento",
            "Auto e moto",
            "Bellezza",
            "Borse",
            "Cancelleria",
            "Casa",
            "Elettronica",
            "Fai da te",
            "Giardinaggio",
            "Giochi",
            "Gioielli",
            "Illuminazione",
            "Informatica",
            "Scarpe",
            "Sport"
            // Aggiungi altri testi qui...
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items((0 until numberOfRows).toList()) { rowIndex ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacingBetweenImages),
                    horizontalArrangement = Arrangement.spacedBy(spacingBetweenImages)
                ) {
                    repeat(numberOfImagesPerRow) { imageIndex ->
                        val imageResourceIndex = rowIndex * numberOfImagesPerRow + imageIndex
                        if (imageResourceIndex < imageList.size) {
                            val imageResource = imageList[imageResourceIndex]
                            val text = textList[imageResourceIndex]
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color.Black),
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(0.8f)
                                    .clickable {
                                        // Quando una categoria è cliccata, selezionala e naviga alla schermata dei prodotti
                                        selectedCategory = text
                                        navController.navigate("ProductCategory/${text}") // Passa la categoria come argomento
                                    }
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = text,
                                        textAlign = TextAlign.Center,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                    Image(
                                        painter = painterResource(id = imageResource),
                                        contentDescription = null,
                                        contentScale = ContentScale.FillBounds,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
