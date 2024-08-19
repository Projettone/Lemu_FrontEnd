package it.unical.ea.lemu_frontend.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import it.unical.ea.lemu_frontend.Product

class CategoryViewModel : ViewModel() {
    // Stato che tiene traccia della categoria selezionata
    val selectedCategory = mutableStateOf<String?>(null)

    // Metodo per selezionare una categoria
    fun selectCategory(categoryName: String) {
        selectedCategory.value = categoryName
    }

    /*
    // Metodo per ottenere i prodotti per la categoria selezionata
    fun getProductsForSelectedCategory(): List<Product> {
        val category = selectedCategory.value
        return if (category != null) {
            // Filtro i prodotti per categoria
            products.filter { it.category == category }
        } else {
            emptyList()
        }
    }

     */
}
