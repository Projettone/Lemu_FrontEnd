package it.unical.ea.lemu_frontend.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Recensione(

    val id: Long? = null,
    val rating: Float,
    val commento: String? = null,
    @Json(name = "destinatario")
    val destinatario: Utente? = null,
    @Json(name = "autore")
    var autore: Utente? = null,
    //val ordine: Ordine? = null
)