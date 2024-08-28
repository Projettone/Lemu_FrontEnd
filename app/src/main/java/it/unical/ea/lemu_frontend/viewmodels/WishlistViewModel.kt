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
import org.openapitools.client.apis.WishlistCondivisioneControllerApi
import org.openapitools.client.apis.WishlistControllerApi
import org.openapitools.client.infrastructure.ClientException
import org.openapitools.client.models.ProdottoDto
import org.openapitools.client.models.WishlistCondivisioneDto
import org.openapitools.client.models.WishlistDto
import org.openapitools.client.models.WishlistProdottiDto

class WishlistViewModel(private val authViewModel: AuthViewModel) : ViewModel() {

    private val _wishlists = MutableStateFlow<List<Wishlist>>(emptyList())
    val wishlists: StateFlow<List<Wishlist>> = _wishlists

    private val _wishlistItems = MutableStateFlow<Map<Long, List<WishlistItem>>>(emptyMap())
    val wishlistItems: StateFlow<Map<Long, List<WishlistItem>>> = _wishlistItems

    private val _sharedEmails = MutableStateFlow<List<String>>(emptyList())
    val sharedEmails: StateFlow<List<String>> = _sharedEmails


    private val wishlistControllerApi = WishlistControllerApi(authViewModel)
    private val wishlistCondivisioneControllerApi = WishlistCondivisioneControllerApi(authViewModel)

    private val prodottoControllerApi = ProdottoControllerApi(authViewModel)

    init {
        loadWishlists()
    }

    fun loadWishlists() {
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
                withContext(Dispatchers.Main) {
                    _wishlists.value = emptyList()
                }
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

                loadWishlists()
            } catch (e: Exception) {
                Log.e("WishlistViewModel", "Errore nell'aggiunta della wishlist", e)
            }
        }
    }

    fun removeWishlist(wishlist: Wishlist) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val wishlistDtoId = wishlist.id ?: throw IllegalStateException("ID della wishlist non trovato")
                wishlistControllerApi.deleteWishlist(wishlistDtoId)

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
                                    id = it.id ?: 0,
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
                Log.e("WishlistViewModel", "Errore nel caricamento dei prodotti della wishlist $wishlistId", e
                )
            }
        }

    }

    fun getSharedEmails(wishlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val condivisioni = wishlistCondivisioneControllerApi.getAllWishlistCondivisioniByWishlistId(wishlistId)

                val emails = condivisioni.mapNotNull { it.email }

                withContext(Dispatchers.Main) {
                    _sharedEmails.value = emails
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _sharedEmails.value = emptyList()
                }
                Log.e("WishlistViewModel", "Errore durante il recupero delle condivisioni: ${e.message}")
            }
        }
    }

    fun addProductToWishlist(wishlistId: Long, prodottoDto: ProdottoDto) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val wishlistProdotti = wishlistControllerApi.getAllWishlistProdotti(wishlistId)

                val existingWishlistItem = wishlistProdotti.find { it.prodottoId == prodottoDto.id }

                if (existingWishlistItem == null) {
                    val newWishlistProdottiDto = WishlistProdottiDto(
                        wishlistId = wishlistId,
                        prodottoId = prodottoDto.id,
                    )
                    val createdWishlistProdottiDto = wishlistControllerApi.createWishlistProdotti(newWishlistProdottiDto)

                    val newWishlistItem = WishlistItem(
                        id = createdWishlistProdottiDto.id ?: throw IllegalStateException("ID del WishlistProdotto non trovato"),
                        imageRes = prodottoDto.immagineProdotto ?: "",
                        name = prodottoDto.nome ?: "Nome non trovato",
                        price = prodottoDto.prezzo?.toFloat() ?: 0f
                    )
                    withContext(Dispatchers.Main) {
                        _wishlistItems.value = _wishlistItems.value.toMutableMap().apply {
                            val updatedList = this[wishlistId]?.toMutableList() ?: mutableListOf()
                            updatedList.add(newWishlistItem)
                            this[wishlistId] = updatedList
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("WishlistViewModel", "Errore nell'aggiunta del prodotto alla wishlist", e)
            }
        }
    }

    fun addEmailToWishlist(wishlistId: Long, email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val condivisioneDto = WishlistCondivisioneDto(
                    wishlistId = wishlistId,
                    email = email
                )

                wishlistCondivisioneControllerApi.createEmailCondivisione(condivisioneDto)

                getSharedEmails(wishlistId)
            } catch (e: Exception) {
                Log.e("WishlistViewModel", "Errore nell'aggiunta dell'email alla wishlist", e)
            }
        }
    }


    fun removeEmailFromWishlist(wishlistId: Long, email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val condivisioni = wishlistCondivisioneControllerApi.getAllWishlistCondivisioniByWishlistId(wishlistId)

                val condivisioneToRemove = condivisioni.find { it.email == email }

                if (condivisioneToRemove != null) {
                    wishlistCondivisioneControllerApi.deleteWishlistCondivisione(condivisioneToRemove.id!!)

                    getSharedEmails(wishlistId)
                } else {
                    Log.w("WishlistViewModel", "Condivisione non trovata per l'email: $email")
                }
            } catch (e: Exception) {
                Log.e("WishlistViewModel", "Errore nella rimozione dell'email dalla wishlist", e)
            }
        }
    }

    fun removeItem(wishlistId: Long, wishlistItem: WishlistItem) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                wishlistControllerApi.deleteWishlistProdotti(wishlistId, wishlistItem.id)

                val updatedItems = _wishlistItems.value.toMutableMap().apply {
                    this[wishlistId] = this[wishlistId]?.toMutableList()?.apply {
                        removeIf { it.id == wishlistItem.id }
                    } ?: emptyList()
                }

                withContext(Dispatchers.Main) {
                    _wishlistItems.value = updatedItems
                }
            } catch (e: Exception) {
                Log.e("WishlistViewModel", "Errore nella rimozione dell'elemento dalla wishlist", e)
            }
        }
    }



}