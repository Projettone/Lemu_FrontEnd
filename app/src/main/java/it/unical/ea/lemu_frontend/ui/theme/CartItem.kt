package it.unical.ea.lemu_frontend.ui.theme

data class CartItem(
    val carrelloProdottoId: Long,
    val prodottoId: Long,// ID del prodotto nel carrello
    val imageRes: String,
    val name: String,
    val quantity: Int,
    val price: Float,
    val maxAvailability: Int
)

