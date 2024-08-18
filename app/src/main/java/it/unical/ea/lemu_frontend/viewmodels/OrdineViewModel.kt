package it.unical.ea.lemu_frontend.viewmodels

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import org.openapitools.client.apis.OrdineControllerApi
import org.openapitools.client.models.OrdineDto
import org.openapitools.client.models.OrdineProdottoDto

class OrdineViewModel(private val authViewModel: AuthViewModel) {
    private val api: OrdineControllerApi = OrdineControllerApi(authViewModel)


    private var _ordini = MutableStateFlow<List<OrdineDto>>(emptyList())
    val ordini: StateFlow<List<OrdineDto>> = _ordini



    private var _ordine = MutableStateFlow<OrdineDto?>(null)
    val ordine: MutableStateFlow<OrdineDto?> = _ordine


    private var _dettagliOrdine = MutableStateFlow<List<OrdineProdottoDto>>(emptyList())
    val dettagliOrdine: StateFlow<List<OrdineProdottoDto>> = _dettagliOrdine


    fun filtraOrdiniPerData(criterio: String) {
        println("Chiamata a filtraOrdiniPerData con criterio: $criterio")
        try {
            _ordini.value = _ordini.value.filter { ordine ->
                val dataAcquistoString = ordine.dataAcquisto?.toString() ?: ""
                println("Filtrando ordine con data: $dataAcquistoString")

                // Verifica se il criterio è contenuto nella data di acquisto
                val dataAcquistoMatch = dataAcquistoString.contains(criterio, ignoreCase = true)
                println("Data Acquisto Match: $dataAcquistoMatch")

                dataAcquistoMatch
            }
            println("Filtro completato")
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
                _ordine.value = null  // Se vuoi aggiornare l'ordine con `null` in caso di errore
            }
        }
    }


    suspend fun GetDettagliOrdine(idOrder: Long?) {
        return withContext(Dispatchers.IO) {
            try {
                val fetchedProducts = idOrder?.let { api.getDettagliOrdineByIdOrder(it) }
                if (fetchedProducts != null) {
                    println("porcone maledetto "+ fetchedProducts.size)
                }
                _dettagliOrdine.value = fetchedProducts ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }


    //metodo per creare un nuovo ordine
    fun createAndSendOrder(
        idUtente: Long,
        prezzoTotaleOrdine: Double,
        prodottoQuantitaList: List<Pair<Long, Int>>
    ) {
        val ordineDto = OrdineDto(
            id = null,
            indirizzo = null,
            idutente = idUtente,
            dataAcquisto = null,
            prezzoTotaleOrdine = prezzoTotaleOrdine,
            ordineProdotti = null
        )
        val ordineProdotti = prodottoQuantitaList.mapIndexed { index, pair ->
            OrdineProdottoDto(
                id = null,
                ordineId = ordineDto.id,
                prodottoId = pair.first,
                quantita = pair.second
            )
        }
        ordineDto.ordineProdotti = ordineProdotti
        // Invia l'ordine tramite l'API
        api.add1(ordineDto)
    }
}