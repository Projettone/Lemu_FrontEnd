package it.unical.ea.lemu_frontend.viewmodels

data class ProductInfo(
    val id: Int,
    val imageResId: Int,
    val name: String,
    val description: String,
    val price: String,
    val venduti: Int,
    val valutazione: Double,
    val numeroRecensioni: Int,
    val category: String,
    val seller: String,
    val availability: Boolean
)