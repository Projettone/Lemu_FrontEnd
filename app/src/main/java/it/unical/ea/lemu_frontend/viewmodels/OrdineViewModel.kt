package it.unical.ea.lemu_frontend.viewmodels

import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import org.openapitools.client.apis.OrdineControllerApi
import org.openapitools.client.models.OrdineDto
import org.openapitools.client.models.OrdineProdottoDto
import java.time.LocalDateTime
import java.util.Locale

class OrdineViewModel(private val authViewModel: AuthViewModel, private val carrelloViewModel: CarrelloViewModel) {
    private val api: OrdineControllerApi = OrdineControllerApi(authViewModel)


    private var _ordini = MutableStateFlow<List<OrdineDto>>(emptyList())
    val ordini: StateFlow<List<OrdineDto>> = _ordini



    private var _ordine = MutableStateFlow<OrdineDto?>(null)
    val ordine: MutableStateFlow<OrdineDto?> = _ordine

    private var _prodottoQuantitaList = MutableStateFlow<List<Pair<Long, Int>>>(emptyList())
    val prodottoQuantitaList: StateFlow<List<Pair<Long, Int>>> = _prodottoQuantitaList

    private var _prezzoTotaleOrdine = MutableStateFlow(0.0)
    val prezzoTotaleOrdine: StateFlow<Double> = _prezzoTotaleOrdine


    private var _dettagliOrdine = MutableStateFlow<List<OrdineProdottoDto>>(emptyList())
    val dettagliOrdine: StateFlow<List<OrdineProdottoDto>> = _dettagliOrdine


    fun filtraOrdiniPerData(criterio: String) {
        try {
            _ordini.value = _ordini.value.filter { ordine ->
                val dataAcquistoString = ordine.dataAcquisto?.toString() ?: ""

                val dataAcquistoMatch = dataAcquistoString.contains(criterio, ignoreCase = true)

                dataAcquistoMatch
            }
        } catch (e: Exception) {
            println("Errore durante il filtro: ${e.message}")
        }
    }



    suspend fun GetOrderbyIdUente(idUser: Long) {
        return withContext(Dispatchers.IO) {
            try {
                val fetchedProducts = api.getOrdinibyidUtente(idUser)
                _ordini.value = fetchedProducts ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    suspend fun GetOrderbyIdUenteAndData(data: String,idUser: Long) {
        return withContext(Dispatchers.IO) {
            try {
                val fetchedProducts = api.getOrdinibyidUtente(idUser)
                _ordini.value = fetchedProducts ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }



    suspend fun GetOrderbyId(idOrder: Long) {
        withContext(Dispatchers.IO) {
            try {
                val fetchedProducts = api.getById1(idOrder)
                _ordine.value = fetchedProducts
            } catch (e: Exception) {
                e.printStackTrace()
                _ordine.value = null
            }
        }
    }


    suspend fun GetDettagliOrdine(idOrder: Long?) {
        return withContext(Dispatchers.IO) {
            try {
                val fetchedProducts = idOrder?.let { api.getDettagliOrdineByIdOrder(it) }
                _dettagliOrdine.value = fetchedProducts ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }


    suspend fun addOrdineProdotto(idOrdine: Long, ordineProdotti: List<OrdineProdottoDto>) {
        return withContext(Dispatchers.IO) {
            api.updateOrdineProdotti(idOrdine, ordineProdotti)

        }
    }


   suspend fun createAndSendOrder() {
       return withContext(Dispatchers.IO) {
           if(carrelloViewModel.cartItems.value.isNotEmpty()){
               val dataAcquistoAttuale = LocalDateTime.now().toLocalDate()

               val cartItems = carrelloViewModel.cartItems.value

               val prodottoQuantita = cartItems.map { cartItem ->
                   Pair(cartItem.prodottoId, cartItem.quantity)
               }

               _prodottoQuantitaList.value = prodottoQuantita


               val prezzoTotale = carrelloViewModel.totalPrice.value
               val prezzoFormattato = String.format(Locale.US, "%.2f", prezzoTotale)
               val valoreDouble: Double = prezzoFormattato.toDouble()
               _prezzoTotaleOrdine.value = valoreDouble

               val ordineDto = OrdineDto(
                   id = null,
                   indirizzo = authViewModel.user.value?.indirizzo?.citta,
                   idutente = authViewModel.user.value?.id,
                   dataAcquisto = dataAcquistoAttuale,
                   prezzoTotaleOrdine = _prezzoTotaleOrdine.value,
                   ordineProdotti = null
               )

               val idOrdine: Long = api.add1(ordineDto)


               val ordineProdotti = prodottoQuantitaList.value.map { pair ->
                   OrdineProdottoDto(
                       id = null,
                       ordineId = idOrdine,
                       prodottoId = pair.first,
                       quantita = pair.second
                   )
               }



               addOrdineProdotto(idOrdine, ordineProdotti)


           }

       }
   }

}