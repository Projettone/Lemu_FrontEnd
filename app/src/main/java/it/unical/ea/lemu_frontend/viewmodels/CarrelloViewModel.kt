package it.unical.ea.lemu_frontend.viewmodels

import CartItem
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.openapitools.client.apis.CarrelloControllerApi
import org.openapitools.client.apis.ProdottoControllerApi
import org.openapitools.client.infrastructure.ClientException
import org.openapitools.client.models.CarrelloDto
import org.openapitools.client.models.CarrelloProdottiDto
import org.openapitools.client.models.ProdottoDto

class CarrelloViewModel(private val authViewModel: AuthViewModel) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice

    private val carrelloControllerApi = CarrelloControllerApi(authViewModel)
    private val prodottoControllerApi = ProdottoControllerApi(authViewModel)

    init {
        loadCartItems()
    }

    fun loadCartItems() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val utenteId = authViewModel.user.value?.id ?: throw IllegalStateException("ID dell' Utente non trovato")

                val carrelloDto = try {
                    carrelloControllerApi.getCarrelloByUtenteId(utenteId = utenteId)
                } catch (e: ClientException) {
                    if (e.statusCode == 403) {
                        val nuovoCarrello = CarrelloDto(utenteId = utenteId)
                        carrelloControllerApi.createCarrello(nuovoCarrello)
                    } else {
                        throw e
                    }
                }

                val carrelloId = carrelloDto.id ?: throw IllegalArgumentException("ID del carrello non trovato")

                val carrelloProdotti: List<CarrelloProdottiDto> = carrelloControllerApi.getAllCarrelloProdottiByCarrelloId(carrelloId)

                val prodottiDettagli = carrelloProdotti.mapNotNull { carrelloProdotto ->
                    try {
                        val prodotto = carrelloProdotto.prodottoId?.let { prodottoControllerApi.getById(id = it) }
                        prodotto?.let {
                            it.prezzo?.let { prezzo ->
                                CartItem(
                                    carrelloProdottoId = carrelloProdotto.id ?: throw IllegalStateException("ID del CarrelloProdotto non trovato"),
                                    prodottoId = carrelloProdotto.prodottoId,
                                    imageRes =it.immagineProdotto ?: "",
                                    name = it.nome ?: "Nome non trovato",
                                    quantity = carrelloProdotto.quantita ?: throw IllegalStateException("Quantità non trovata"),
                                    price = prezzo.toFloat(),
                                    maxAvailability = it.disponibilita ?: throw IllegalStateException("Disponibilità non trovata")
                                )
                            }
                        }
                    } catch (e: Exception) {
                        null
                    }
                }

                withContext(Dispatchers.Main) {
                    _cartItems.value = prodottiDettagli
                    calculateTotalPrice()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _cartItems.value = emptyList()
                }
                Log.e("CarrelloViewModel", "Errore nel caricamento del carrello", e)
            }
        }
    }


    private fun calculateTotalPrice() {
        val total = _cartItems.value.sumOf { it.price * it.quantity.toDouble() }
        _totalPrice.value = total
    }

    fun removeItem(cartItem: CartItem) {
        cartItem.carrelloProdottoId.let { carrelloProdottoId ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    carrelloControllerApi.deleteCarrelloProdotti(carrelloProdottoId)
                    val updatedItems = _cartItems.value.toMutableList().apply { remove(cartItem) }
                    withContext(Dispatchers.Main) {
                        _cartItems.value = updatedItems
                        calculateTotalPrice()
                    }
                } catch (e: Exception) {
                    Log.e("CarrelloViewModel", "Errore nella rimozione dell'elemento", e)
                }
            }
        }
    }

    fun incrementQuantity(cartItem: CartItem) {
        val index = _cartItems.value.indexOf(cartItem)
        if (index != -1) {
            val updatedItem = cartItem.copy(quantity = cartItem.quantity + 1)
            updateCartItemInDatabase(updatedItem)
        }
    }

    fun decrementQuantity(cartItem: CartItem) {
        val index = _cartItems.value.indexOf(cartItem)
        if (index != -1) {
            if (cartItem.quantity > 1) {
                val updatedItem = cartItem.copy(quantity = cartItem.quantity - 1)
                updateCartItemInDatabase(updatedItem)
            } else {
                removeItem(cartItem)
            }
        }
    }

    private fun updateCartItemInDatabase(cartItem: CartItem) {
        cartItem.carrelloProdottoId.let { carrelloProdottoId ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val carrelloProdottiDto = CarrelloProdottiDto(
                        id = carrelloProdottoId,
                        prodottoId = cartItem.prodottoId,
                        quantita = cartItem.quantity
                    )
                    carrelloControllerApi.updateCarrelloProdotti(carrelloProdottoId, carrelloProdottiDto)
                    val updatedItems = _cartItems.value.toMutableList().apply {
                        val index = indexOfFirst { it.carrelloProdottoId == cartItem.carrelloProdottoId }
                        if (index != -1) {
                            set(index, cartItem)
                        }
                    }
                    withContext(Dispatchers.Main) {
                        _cartItems.value = updatedItems
                        calculateTotalPrice()
                    }
                } catch (e: Exception) {
                    Log.e("CarrelloViewModel", "Errore nell'aggiornamento del database", e)
                }
            }
        }
    }

    fun addProductToCart(prodottoDto: ProdottoDto, quantita: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val utenteId = authViewModel.user.value?.id ?: throw IllegalStateException("ID dell'utente non trovato")
                val carrelloDto = carrelloControllerApi.getCarrelloByUtenteId(utenteId)
                val carrelloId = carrelloDto.id ?: throw IllegalArgumentException("ID del carrello non trovato")
                val carrelloProdotti = carrelloControllerApi.getAllCarrelloProdottiByCarrelloId(carrelloId)

                val existingCartItem = carrelloProdotti.find { it.prodottoId == prodottoDto.id }

                if (existingCartItem != null) {
                    val currentQuantity = existingCartItem.quantita ?: 0
                    val newQuantity = currentQuantity+ quantita
                    val updatedCarrelloProdottiDto = existingCartItem.copy(quantita = newQuantity)
                    carrelloControllerApi.updateCarrelloProdotti(existingCartItem.id!!, updatedCarrelloProdottiDto)

                    val updatedItems = _cartItems.value.map { item ->
                        if (item.prodottoId == prodottoDto.id) {
                            item.copy(quantity = newQuantity)
                        } else {
                            item
                        }
                    }
                    withContext(Dispatchers.Main) {
                        _cartItems.value = updatedItems
                    }
                } else {
                    val newCarrelloProdottiDto = CarrelloProdottiDto(
                        carrelloId = carrelloId,
                        prodottoId = prodottoDto.id,
                        quantita = quantita
                    )
                    val createdCarrelloProdottiDto = carrelloControllerApi.createCarrelloProdotti(newCarrelloProdottiDto)

                    val newCartItem = CartItem(
                        carrelloProdottoId = createdCarrelloProdottiDto.id ?: throw IllegalStateException("ID del CarrelloProdotto non trovato"),
                        prodottoId = prodottoDto.id ?: throw IllegalStateException("ID del CarrelloProdotto non trovato"),
                        imageRes = prodottoDto.immagineProdotto ?: "",
                        name = prodottoDto.nome ?: "Nome non trovato",
                        quantity = quantita,
                        price = prodottoDto.prezzo.toFloat(),
                        maxAvailability = prodottoDto.disponibilita ?: throw IllegalStateException("Disponibilità non trovata")
                    )
                    withContext(Dispatchers.Main) {
                        _cartItems.value = _cartItems.value + newCartItem
                    }
                }

                withContext(Dispatchers.Main) {
                    calculateTotalPrice()
                }

            } catch (e: Exception) {
                Log.e("CarrelloViewModel", "Errore nell'aggiunta del prodotto al carrello", e)
            }
        }
    }

    fun deleteAllItemsForUser(utenteId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val carrelloDto = carrelloControllerApi.getCarrelloByUtenteId(utenteId)

                val carrelloId = carrelloDto.id ?: throw IllegalArgumentException("ID del carrello non trovato")

                deleteAllItemsInCart(carrelloId)

            } catch (e: Exception) {
                Log.e("CarrelloViewModel", "Errore durante la cancellazione di tutti i prodotti nel carrello per l'utente $utenteId", e)
            }
        }
    }

    fun deleteAllItemsInCart(carrelloId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                carrelloControllerApi.deleteAllCarrelloProdottiByCarrelloId(carrelloId)

                withContext(Dispatchers.Main) {
                    _cartItems.value = emptyList()
                    _totalPrice.value = 0.0
                }
            } catch (e: Exception) {
                Log.e("CarrelloViewModel", "Errore durante la cancellazione di tutti i prodotti nel carrello", e)
            }
        }
    }



}
