import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import it.unical.ea.lemu_frontend.viewmodels.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.openapitools.client.apis.ProdottoControllerApi
import org.openapitools.client.models.ProdottoDto

class ProductsViewModel(private val authViewModel: AuthViewModel) : ViewModel() {

    private val prodottoControllerApi = ProdottoControllerApi(authViewModel)  // Instantiate the API controller

    private val _products = mutableStateOf<List<ProdottoDto>>(emptyList())
    val products: State<List<ProdottoDto>> = _products

    fun fetchProductsByCategory(category: String?) {
        println("sono dentro")
        println(category)
        viewModelScope.launch {
            try {
                val productList = withContext(Dispatchers.IO) {
                    // Usa la funzione di rete sincrona qui
                    prodottoControllerApi.getProdutCategory(category ?: "")
                }
                _products.value = productList
                println("Numero di prodotti: ${productList.size}")
            } catch (e: Exception) {
                _products.value = emptyList()
                println("Numero di prodotti: 0 (nessun prodotto trovato o errore)")
                e.printStackTrace()
            }
        }
    }


}
