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


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 
 *
 * @param rating 
 * @param commento 
 * @param nomeProdotto 
 * @param prodottoId 
 * @param credenzialiEmailAutore 
 * @param id 
 * @param immagineProfiloAutore 
 */


data class RecensioneDto (

    @Json(name = "rating")
    val rating: kotlin.Float,

    @Json(name = "commento")
    val commento: kotlin.String,

    @Json(name = "nomeProdotto")
    val nomeProdotto: kotlin.String,

    @Json(name = "prodottoId")
    val prodottoId: kotlin.Long,

    @Json(name = "credenzialiEmailAutore")
    val credenzialiEmailAutore: kotlin.String,

    @Json(name = "id")
    val id: kotlin.Long? = null,

    @Json(name = "immagineProfiloAutore")
    val immagineProfiloAutore: kotlin.String? = null

) {


}

