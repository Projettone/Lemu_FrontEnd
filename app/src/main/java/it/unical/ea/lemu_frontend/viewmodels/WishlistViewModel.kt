package it.unical.ea.lemu_frontend.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unical.ea.lemu_frontend.Wishlist
import it.unical.ea.lemu_frontend.WishlistItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.openapitools.client.apis.ProdottoControllerApi
import org.openapitools.client.apis.WishlistControllerApi
import org.openapitools.client.infrastructure.ClientException
import org.openapitools.client.models.WishlistDto

class WishlistViewModel(private val authViewModel: AuthViewModel) : ViewModel() {

    private val _wishlists = MutableStateFlow<List<Wishlist>>(emptyList())
    val wishlists: StateFlow<List<Wishlist>> = _wishlists

    private val _wishlistItems = MutableStateFlow<Map<Long, List<WishlistItem>>>(emptyMap())
    val wishlistItems: StateFlow<Map<Long, List<WishlistItem>>> = _wishlistItems

    private val wishlistControllerApi = WishlistControllerApi(authViewModel)

    private val prodottoControllerApi = ProdottoControllerApi(authViewModel)

    init {
        loadWishlists()
    }

    private fun loadWishlists() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val utenteId = authViewModel.user.value?.id ?: throw IllegalStateException("ID dell'utente non trovato")

                val wishlistDtos: List<WishlistDto> = try {
                    wishlistControllerApi.getAllWishlistByUtenteId(utenteId)
                } catch (e: ClientException) {
                    Log.e("WishlistViewModel", "Errore nel caricamento delle wishlist", e)
                    emptyList()
                }

                val wishlists = wishlistDtos.map { dto ->
                    Wishlist(
                        nome = dto.nome ?: "Nome non trovato",
                        id = dto.id ?: 0,
                        tipo = dto.tipo ?: "Sconosciuto"
                    )
                }

                withContext(Dispatchers.Main) {
                    _wishlists.value = wishlists
                }
            } catch (e: Exception) {
                Log.e("WishlistViewModel", "Errore generico nel caricamento delle wishlist", e)
            }
        }
    }

    fun addWishlist(nome: String, tipo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val utenteId = authViewModel.user.value?.id ?: throw IllegalStateException("ID dell'utente non trovato")

                val newWishlistDto = WishlistDto(
                    nome = nome,
                    tipo = tipo,
                    utenteId = utenteId
                )

                wishlistControllerApi.createWishlist(newWishlistDto)

                // Ricarica le wishlist per riflettere la nuova aggiunta
                loadWishlists()
            } catch (e: Exception) {
                Log.e("WishlistViewModel", "Errore nell'aggiunta della wishlist", e)
            }
        }
    }

    fun removeWishlist(wishlist: Wishlist) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Supponendo che Wishlist abbia un ID per poterla rimuovere
                val wishlistDtoId = wishlist.id ?: throw IllegalStateException("ID della wishlist non trovato")
                wishlistControllerApi.deleteWishlist(wishlistDtoId)

                // Ricarica le wishlist per riflettere la rimozione
                loadWishlists()
            } catch (e: Exception) {
                Log.e("WishlistViewModel", "Errore nella rimozione della wishlist", e)
            }
        }
    }

    fun getAllWishlistProdotti(wishlistId: Long){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val prodotti = wishlistControllerApi.getAllWishlistProdotti(wishlistId)
                val prodottiDettagli = prodotti.mapNotNull { wishlistProdotto ->
                    try {
                        val prodotto =
                            wishlistProdotto.prodottoId?.let { prodottoControllerApi.getById(id = it) }
                        prodotto?.let {
                            it.prezzo?.let { prezzo ->
                                WishlistItem(
                                    imageRes = it.immagineProdotto ?: "",
                                    name = it.nome ?: "Nome non trovato",
                                    price = prezzo.toFloat()
                                )
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(
                            "WishlistViewModel",
                            "Errore nel recupero del prodotto con ID ${wishlistProdotto.prodottoId}",
                            e
                        )
                        null
                    }
                }
                withContext(Dispatchers.Main) {
                    _wishlistItems.value = _wishlistItems.value.toMutableMap().apply {
                        this[wishlistId] = prodottiDettagli
                    }
                }
            } catch (e: Exception) {
                Log.e(
                    "WishlistViewModel",
                    "Errore nel caricamento dei prodotti della wishlist $wishlistId",
                    e
                )
            }
        }

    }

}
