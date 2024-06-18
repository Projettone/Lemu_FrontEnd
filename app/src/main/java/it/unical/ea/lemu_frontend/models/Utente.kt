package it.unical.ea.lemu_frontend.models

import android.graphics.Bitmap

data class Utente(
    val id: Long? = null,
    val nome: String? = null,
    val cognome: String? = null,
    val immagine: Bitmap? = null,
    val indirizzo: Indirizzo? = null,
    val recensioniRicevute: Array<Recensione>? = null,
    //val prodotti: Array<Prodotto>? = null,
    //val preferiti: Array<Prodotto>? = null,
    val credenziali: Credenziali,
    val isAdmin: Boolean? = null,
    //val ordiniUtente: Array<Ordine>? = null,
    val ratingGenerale: Float? = null
)