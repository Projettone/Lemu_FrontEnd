package it.unical.ea.lemu_frontend.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.openapitools.client.apis.ProdottoControllerApi
import org.openapitools.client.apis.RecensioneControllerApi
import org.openapitools.client.apis.UtenteControllerApi
import org.openapitools.client.apis.WishlistCondivisioneControllerApi
import org.openapitools.client.apis.WishlistControllerApi
import org.openapitools.client.models.ProdottoDto
import org.openapitools.client.models.RecensioneDto
import org.openapitools.client.models.UtenteDto
import org.openapitools.client.models.WishlistDto
import org.openapitools.client.models.WishlistProdottiDto

class SearchedUserViewModel(private val authViewModel: AuthViewModel) {
    private val utenteControllerApi: UtenteControllerApi = UtenteControllerApi(authViewModel)
    private val wishlistControllerApi: WishlistControllerApi = WishlistControllerApi(authViewModel)
    private val wishlistCondivisioneControllerApi: WishlistCondivisioneControllerApi = WishlistCondivisioneControllerApi(authViewModel)
    private val prodottoControllerApi: ProdottoControllerApi = ProdottoControllerApi(authViewModel)
    private val recensioneControllerApi: RecensioneControllerApi = RecensioneControllerApi(authViewModel)
    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _userProfile = MutableStateFlow<UtenteDto?>(null)
    val userProfile: MutableStateFlow<UtenteDto?> = _userProfile

    private val _publicWishlists = MutableStateFlow<List<WishlistDto>>(emptyList())
    val publicWishlists: StateFlow<List<WishlistDto>> = _publicWishlists

    private val _sharedWishlists = MutableStateFlow<List<WishlistDto>>(emptyList())
    val sharedWishlists: StateFlow<List<WishlistDto>> = _sharedWishlists

    private val _wishlistDetails = mutableStateOf<WishlistDto?>(null)
    val wishlistDetails: MutableState<WishlistDto?> get() = _wishlistDetails

    private val _searchedUsers = MutableStateFlow<List<UtenteDto>>(emptyList())
    val searchedUsers: StateFlow<List<UtenteDto>> get() = _searchedUsers

    private val _showOverlay = MutableStateFlow(false)
    val showOverlay: StateFlow<Boolean> get() = _showOverlay

    fun showOverlayfalse () {
        _showOverlay.value = false
    }

    private val _recensioni = MutableStateFlow<List<RecensioneDto>>(emptyList())
    val recensioni: StateFlow<List<RecensioneDto>> get() = _recensioni.asStateFlow()

    private val _wishlistProductDetails = mutableStateListOf<ProdottoDto>()
    val wishlistProductDetails: List<ProdottoDto> get() = _wishlistProductDetails

    fun loadProductDetails(productIds: List<Long>) {
        viewModelScope.launch {
            for (productId in productIds) {
                try {
                    val productDetail = prodottoControllerApi.getById(productId)
                    _wishlistProductDetails.add(productDetail)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    var currentPageRecensioni = 0
    val pageSize = 1
    var totalPagesRecensioni by mutableStateOf(0)


    suspend fun loadWishlists() {
        try {
            clearWishlists()
            val publicWishlistsResponse = withContext(Dispatchers.IO) {
                _userProfile.value?.id?.let { wishlistControllerApi.getPublicWishlistsByUserId(it) }
            }
            if (publicWishlistsResponse != null) {
                _publicWishlists.value = publicWishlistsResponse
            }

            val sharedWishlistsResponse = withContext(Dispatchers.IO) {
                _userProfile.value?.id?.let { authViewModel.user.value?.email?.let { it1 ->
                    wishlistCondivisioneControllerApi.getWishlistsCondiviseConEmail(it, it1)
                } }
            }
            if (sharedWishlistsResponse != null) {
                _sharedWishlists.value = sharedWishlistsResponse
            }

            loadProductsForWishlists()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private suspend fun loadProductsForWishlists() {
        try {
            val allWishlists = _publicWishlists.value + _sharedWishlists.value

            val updatedWishlists = mutableListOf<WishlistDto>()

            for (wishlist in allWishlists) {
                val wishlistId = wishlist.id ?: continue
                val productsResponse = withContext(Dispatchers.IO) {
                    wishlistControllerApi.getAllWishlistProdotti(wishlistId)
                }

                val productIds = productsResponse.mapNotNull { it.prodottoId }

                val updatedWishlist = wishlist.copy(prodotti = productIds)
                updatedWishlists.add(updatedWishlist)
            }

            _publicWishlists.value = updatedWishlists.filter { it.tipo == "pubblica" }
            _sharedWishlists.value = updatedWishlists.filter { it.tipo == "condivisa" }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadWishlistDetails(wishlistId: Long) {
        viewModelScope.launch {
            val wishlist = _publicWishlists.value.find { it.id == wishlistId }
                ?: _sharedWishlists.value.find { it.id == wishlistId }
            _wishlistDetails.value = wishlist

            wishlist?.prodotti?.let { productIds ->
                loadProductDetailsForWishlist(productIds)
            }
        }
    }

    private suspend fun loadProductDetailsForWishlist(productIds: List<Long>) {
        try {
            _wishlistProductDetails.clear()
            for (productId in productIds) {
                try {
                    val productDetail = withContext(Dispatchers.IO) {
                        prodottoControllerApi.getById(productId)
                    }
                    _wishlistProductDetails.add(productDetail)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearWishlists() {
        _publicWishlists.value = emptyList()
        _sharedWishlists.value = emptyList()
        _wishlistDetails.value = null
        _wishlistProductDetails.clear()
    }


    suspend fun searchUsers(keyword: String) {
        try {
            val response = withContext(Dispatchers.IO) {
                utenteControllerApi.searchUsers(keyword)
            }
            _searchedUsers.value = response
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadNextPageReviews() {
        viewModelScope.launch {
            if (currentPageRecensioni < totalPagesRecensioni - 1) {
                currentPageRecensioni += 1
                getPagedReviews()
            }
        }
    }

    fun loadPreviousPageReviews() {
        if (currentPageRecensioni > 0) {
            viewModelScope.launch {
                currentPageRecensioni -= 1
                getPagedReviews()
            }
        }
    }

    suspend fun getPagedReviews() {
        try {
            val response = withContext(Dispatchers.IO) {
                userProfile.value?.id?.let {
                    recensioneControllerApi.getRecensioniByUtenteRicercato(
                        it,
                        currentPageRecensioni,
                        pageSize
                    )
                }
            }
            if (response != null) {
                _recensioni.value = response.content ?: emptyList()
            }
            if (response != null) {
                totalPagesRecensioni = response.totalPages ?: 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
