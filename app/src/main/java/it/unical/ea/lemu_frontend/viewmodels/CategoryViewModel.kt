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

class CategoryViewModel(private val authViewModel: AuthViewModel) : ViewModel() {

    private val prodottoControllerApi = ProdottoControllerApi(authViewModel)  // Instantiate the API controller

    private val _products = mutableStateOf<List<ProdottoDto>>(emptyList())
    val products: State<List<ProdottoDto>> = _products

    fun fetchProductsByCategory(category: String?) {
        viewModelScope.launch {
            try {
                val productList = withContext(Dispatchers.IO) {
                    prodottoControllerApi.getProdutCategory(category ?: "")
                }
                _products.value = productList
            } catch (e: Exception) {
                _products.value = emptyList()
                e.printStackTrace()
            }
        }
    }


}
