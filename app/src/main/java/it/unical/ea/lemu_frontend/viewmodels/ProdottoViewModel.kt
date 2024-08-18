package it.unical.ea.lemu_frontend.viewmodels

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import org.openapitools.client.apis.ProdottoControllerApi
import org.openapitools.client.apis.RecensioneControllerApi
import org.openapitools.client.models.ProdottoDto
import org.openapitools.client.models.RecensioneDto
import java.io.IOException
import java.util.concurrent.TimeoutException

class ProdottoViewModel (private val authViewModel: AuthViewModel){
    private val api: ProdottoControllerApi = ProdottoControllerApi()
    private val apiRecensioni: RecensioneControllerApi = RecensioneControllerApi(authViewModel)

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    private var _loading = MutableStateFlow<Boolean>(false)
    val loading: StateFlow<Boolean> = _loading

    private var _listaProdotti = MutableStateFlow<List<ProdottoDto>>(emptyList())
    val listaProdotti: StateFlow<List<ProdottoDto>> = _listaProdotti

    private var _prodotto = MutableStateFlow<List<ProdottoDto>>(emptyList())
    val prodotto: StateFlow<List<ProdottoDto>> = _prodotto

    private var _listaRicercheCompleta = MutableStateFlow<List<ProdottoDto>>(emptyList())
    val listaRicercheCompleta: StateFlow<List<ProdottoDto>> = _listaRicercheCompleta


    private val _prodotti = MutableStateFlow<List<ProdottoDto>>(emptyList())
    val prodotti: StateFlow<List<ProdottoDto>> = _prodotti

    private var _listaRecensioni = MutableStateFlow<List<RecensioneDto>>(emptyList())
    val listaRecensioni: StateFlow<List<RecensioneDto>> = _listaRecensioni


    //prendi le recensioni in base all'id del prodotto
    suspend fun findRecensioniByidProdotto(idProduct: Long?) {
        return withContext(Dispatchers.IO) {
            try {
                val fetchedProducts = apiRecensioni.getRecensioniByProdottoId(idProduct!!)
                _listaRecensioni.value = fetchedProducts ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // Carica tutti i prodotti
    suspend fun fetchAllProducts(start:Int , end:Int) {
        return withContext(Dispatchers.IO) {
            try {
                val fetchedProducts = api.findAll(start,end)
                _prodotti.value = fetchedProducts ?: emptyList()
                aggiornamentoListaProdottiCompleta()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    suspend fun loadProdotti(idProduct: Long?) {
        withContext(Dispatchers.IO) {
            try {
                val fetchedProducts = getProductById(idProduct)

                if (fetchedProducts != null) {
                    // Aggiungi i nuovi prodotti alla lista esistente
                    val updatedList = _prodotto.value.toMutableList()
                    updatedList.addAll(listOf(fetchedProducts))

                    // Aggiorna lo StateFlow con la nuova lista
                    _prodotto.value = updatedList
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    // Cerca prodotti
    suspend fun searchProducts(keyword: String){
        return withContext(Dispatchers.IO) {
            try {
                val fetchedProducts = api.searchProdotti(keyword)

                _listaRicercheCompleta.value = fetchedProducts ?: emptyList()
                println("Dimensione ricerca lista " + _listaRicercheCompleta.value.size)

            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }


    suspend fun aggiornamentoListaProdottiCompleta() {
        return withContext(Dispatchers.IO) {
            try {
                val currentList = _listaProdotti.value
                val newProducts = _prodotti.value
                val filteredNewProducts = newProducts.filter { newProduct ->
                    currentList.none { existingProduct -> existingProduct.id == newProduct.id }
                }
                val updatedList = currentList + filteredNewProducts
                _listaProdotti.value = updatedList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    // Aggiungi un prodotto
    suspend fun addProduct(prodottoDto: ProdottoDto) {
        withContext(Dispatchers.IO) {
            try {
                api.add(prodottoDto)
            } catch (e: IOException) {
                println("Errore di I/O durante la chiamata API: ${e.message}")
            } catch (e: TimeoutException) {
                println("Timeout durante la chiamata API: ${e.message}")
            } catch (e: Exception) {
                println("Errore generico durante la chiamata API: ${e.javaClass.simpleName}: ${e.message}")
            }
        }
    }


    // Ottieni prodotto per ID
    suspend fun getProductById(idProduct: Long?): ProdottoDto? {
        return withContext(Dispatchers.IO) {
            try {
                api.getById(idProduct!!)
            } catch (e: IOException) {
                println("Errore di I/O durante la chiamata API: ${e.message}")
                null
            } catch (e: TimeoutException) {
                println("Timeout durante la chiamata API: ${e.message}")
                null
            } catch (e: Exception) {
                println("Errore generico durante la chiamata API: ${e.javaClass.simpleName}: ${e.message}")
                e.printStackTrace()
                null
            }
        }
    }
}