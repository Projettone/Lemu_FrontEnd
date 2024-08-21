package it.unical.ea.lemu_frontend.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import org.openapitools.client.apis.UtenteControllerApi
import org.openapitools.client.models.UtenteDto
import org.openapitools.client.models.WishlistDto

class SearchedUserViewModel(private val authViewModel: AuthViewModel) {
    private val utenteControllerApi: UtenteControllerApi = UtenteControllerApi(authViewModel)

    var _userProfile = mutableStateOf<UtenteDto?>(null)

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

    init {
        loadStaticWishlists()
    }

    fun loadUserProfile() {
        _userProfile.value = authViewModel.user.value

    }

    fun loadWishlists(userId: Long) {
        // Già caricato con dati statici
    }


    fun loadWishlistDetails(wishlistId: Long) {
        val wishlist = _publicWishlists.value.find { it.id == wishlistId }
            ?: _sharedWishlists.value.find { it.id == wishlistId }
        _wishlistDetails.value = wishlist
    }

    private fun loadStaticWishlists() {
        // Dati statici delle wishlist
        val staticPublicWishlists = listOf(
            WishlistDto(
                id = 1,
                utenteId = 101,
                prodotti = listOf(1, 2, 3),
                nome = "Viaggi da Sogno",
                tipo = "pubblica",
                condivisiCon = emptyList()
            ),
            WishlistDto(
                id = 2,
                utenteId = 101,
                prodotti = listOf(4, 5),
                nome = "Gadget Tecnologici",
                tipo = "pubblica",
                condivisiCon = emptyList()
            )
        )

        val staticSharedWishlists = listOf(
            WishlistDto(
                id = 3,
                utenteId = 101,
                prodotti = listOf(6, 7, 8),
                nome = "Libri Consigliati",
                tipo = "condivisa",
                condivisiCon = listOf("utente_cercato")
            ),
            WishlistDto(
                id = 4,
                utenteId = 101,
                prodotti = listOf(9, 10),
                nome = "Esperienze Avventurose",
                tipo = "condivisa",
                condivisiCon = listOf("utente_cercato")
            )
        )

        //_publicWishlists.value = staticPublicWishlists
        _sharedWishlists.value = staticSharedWishlists
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
}
