package it.unical.ea.lemu_frontend


import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.unical.ea.lemu_frontend.viewmodels.OrdineViewModel
import it.unical.ea.lemu_frontend.viewmodels.ProdottoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DettagliOrdineActivity(idOrdine: String, viewModelOrder: OrdineViewModel, viewModelProduct: ProdottoViewModel) {

    val coroutineScope = rememberCoroutineScope()



    val prodotti by viewModelProduct.prodotti.collectAsState()
    val dettagliOrdine by viewModelOrder.dettagliOrdine.collectAsState()
    val idOrdine = idOrdine.toLongOrNull()
    val ordine by viewModelOrder.ordine.collectAsState()



    Spacer(modifier = Modifier.height(8.dp))

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            viewModelOrder.GetDettagliOrdine(idOrdine)
            if (idOrdine != null) {
                viewModelOrder.GetOrderbyId(idOrdine)
            }

            println("CIOAAOOOO SONO DETTAGLI ORDINE " + dettagliOrdine.size)


            dettagliOrdine.forEachIndexed { index, dettaglio ->
                viewModelProduct.loadProdotti(dettagliOrdine[index].prodottoId)
            }

        }
    }



    LazyColumn {
        item {
            Text(
                text = "Visualizza dettagli ordine",
                fontSize = 25.sp,
                modifier = Modifier.padding(9.dp),
                fontWeight = FontWeight.Bold,

                )
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .border(0.1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Row {
                    Text(
                        text = "Data dell'ordine",
                        style = TextStyle(fontSize = 15.sp),
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(40.dp))
                    Text(text = "${ordine?.dataAcquisto}", style = TextStyle(fontSize = 15.sp))
                }
                Row {
                    Text(
                        text = "Codice ordine",
                        style = TextStyle(fontSize = 15.sp),
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(50.dp))
                    Text(text = "${ordine?.id}", style = TextStyle(fontSize = 15.sp))
                }
                Row {
                    Text(
                        text = "Totale ordine",
                        style = TextStyle(fontSize = 15.sp),
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(54.dp))
                    Text(text = "EUR ${ordine?.prezzoTotaleOrdine}(${dettagliOrdine.size} articoli)", style = TextStyle(fontSize = 15.sp))
                }
            }

        }

        items(dettagliOrdine.size) { index ->
            val ordini = dettagliOrdine[index]
            println("ciao")

            val prodotto = prodotti[index]

            Row(
                modifier = Modifier
                    .padding(end = 12.dp, start = 12.dp, bottom = 19.dp)
                    .border(0.1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                    .padding(9.dp)
            ) {

                val base64WithoutPrefix = prodotto.immagineProdotto?.removePrefix("data:image/png;base64,")
                val imageBytes = Base64.decode(base64WithoutPrefix, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                val imageBitmap = bitmap.asImageBitmap()
                Image(
                    painter = BitmapPainter(imageBitmap),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(120.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = prodotto.descrizione!!, // Usa i dati dell'ordine qui
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Q.tà: ${ordini.quantita}", // Usa la quantità dell'ordine
                        style = TextStyle(fontSize = 15.sp),
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Venduto da: ${prodotto.prezzo}", // Usa il venditore dell'ordine
                        style = TextStyle(fontSize = 15.sp),
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "€${prodotto.prezzo}", // Usa il prezzo dell'ordine
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }

        // Aggiungi gli elementi statici fuori dal ciclo
        item {
            Text(
                text = "Riepilogo ordine",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(12.dp)
            )
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .border(0.1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Modalità di pagamento",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Postepay termina con 5225",
                    fontSize = 15.sp,
                )
            }
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .border(0.1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Indirizzo di spedizione",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${ordine?.indirizzo}",
                    fontSize = 15.sp,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp, start = 14.dp)
            ) {
                Text(
                    text = "Totale Ordine:",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(23.dp))
                Text(
                    text = "EUR ${ordine?.prezzoTotaleOrdine}",
                    modifier = Modifier.absoluteOffset(0.dp, 1.dp),
                    fontSize = 18.sp,
                    color = Color.Red
                )
            }
        }
    }

}
