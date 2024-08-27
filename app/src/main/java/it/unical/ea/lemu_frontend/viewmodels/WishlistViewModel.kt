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
                Log.e(
                    "WishlistViewModel",
                    "Errore nel caricamento dei prodotti della wishlist $wishlistId",
                    e
                )
            }
        }

    }

    fun getSharedEmails(wishlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Recupera le condivisioni della wishlist usando l'API
                val condivisioni = wishlistCondivisioneControllerApi.getAllWishlistCondivisioniByWishlistId(wishlistId)

                // Mappa le condivisioni per ottenere le email, rimuovendo eventuali null
                val emails = condivisioni.mapNotNull { it.email }

                // Aggiorna lo stato con le email condivise sulla Main thread
                withContext(Dispatchers.Main) {
                    _sharedEmails.value = emails
                }
            } catch (e: Exception) {
                // Gestisci errori e aggiorna lo stato con una lista vuota in caso di errore
                withContext(Dispatchers.Main) {
                    _sharedEmails.value = emptyList()
                }
                // Log dell'errore per il debug
                Log.e("WishlistViewModel", "Errore durante il recupero delle condivisioni: ${e.message}")
            }
        }
    }

    fun addProductToWishlist(wishlistId: Long, prodottoDto: ProdottoDto) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Recupera i prodotti nella wishlist specificata
                val wishlistProdotti = wishlistControllerApi.getAllWishlistProdotti(wishlistId)

                // Controlla se il prodotto è già presente nella wishlist
                val existingWishlistItem = wishlistProdotti.find { it.prodottoId == prodottoDto.id }

                if (existingWishlistItem == null) {
                    // Il prodotto non è presente nella wishlist, quindi lo aggiungiamo
                    val newWishlistProdottiDto = WishlistProdottiDto(
                        wishlistId = wishlistId,
                        prodottoId = prodottoDto.id,
                    )
                    val createdWishlistProdottiDto = wishlistControllerApi.createWishlistProdotti(newWishlistProdottiDto)

                    // Aggiungi il nuovo prodotto alla mappa _wishlistItems
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
                // Crea un oggetto WishlistCondivisioneDto con l'ID della wishlist e l'email
                val condivisioneDto = WishlistCondivisioneDto(
                    wishlistId = wishlistId,
                    email = email
                )

                // Utilizza l'API per creare la nuova condivisione
                wishlistCondivisioneControllerApi.createEmailCondivisione(condivisioneDto)

                // Aggiorna la lista delle email condivise
                getSharedEmails(wishlistId)
            } catch (e: Exception) {
                Log.e("WishlistViewModel", "Errore nell'aggiunta dell'email alla wishlist", e)
            }
        }
    }


    fun removeEmailFromWishlist(wishlistId: Long, email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Recupera tutte le condivisioni per la wishlist specificata
                val condivisioni = wishlistCondivisioneControllerApi.getAllWishlistCondivisioniByWishlistId(wishlistId)

                // Trova la condivisione corrispondente all'email
                val condivisioneToRemove = condivisioni.find { it.email == email }

                if (condivisioneToRemove != null) {
                    // Se la condivisione esiste, rimuovila utilizzando l'ID della condivisione
                    wishlistCondivisioneControllerApi.deleteWishlistCondivisione(condivisioneToRemove.id!!)

                    // Aggiorna la lista delle email condivise
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
                // Chiama l'API per rimuovere l'articolo dalla wishlist
                wishlistControllerApi.deleteWishlistProdotti(wishlistId, wishlistItem.id)

                // Aggiorna la lista degli articoli della wishlist specifica
                val updatedItems = _wishlistItems.value.toMutableMap().apply {
                    this[wishlistId] = this[wishlistId]?.toMutableList()?.apply {
                        removeIf { it.id == wishlistItem.id }
                    } ?: emptyList()
                }

                withContext(Dispatchers.Main) {
                    _wishlistItems.value = updatedItems
                    // Puoi aggiungere ulteriori aggiornamenti se necessario
                }
            } catch (e: Exception) {
                Log.e("WishlistViewModel", "Errore nella rimozione dell'elemento dalla wishlist", e)
            }
        }
    }



}