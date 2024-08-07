/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package org.openapitools.client.models

import org.openapitools.client.models.Indirizzo
import org.openapitools.client.models.OrdineProdotto
import org.openapitools.client.models.Utente

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 
 *
 * @param id 
 * @param indirizzo 
 * @param utente 
 * @param dataAcquisto 
 * @param prezzoTotaleOrdine 
 * @param ordineProdotti 
 */


data class Ordine (

    @Json(name = "id")
    val id: kotlin.Long? = null,

    @Json(name = "indirizzo")
    val indirizzo: Indirizzo? = null,

    @Json(name = "utente")
    val utente: Utente? = null,

    @Json(name = "dataAcquisto")
    val dataAcquisto: java.time.LocalDate? = null,

    @Json(name = "prezzoTotaleOrdine")
    val prezzoTotaleOrdine: kotlin.Double? = null,

    @Json(name = "ordineProdotti")
    val ordineProdotti: kotlin.collections.List<OrdineProdotto>? = null

) {


}

