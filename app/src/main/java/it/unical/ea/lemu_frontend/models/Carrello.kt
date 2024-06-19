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

import org.openapitools.client.models.CarrelloProdotti
import org.openapitools.client.models.Utente

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 *
 *
 * @param id
 * @param utente
 * @param carrelloProdotti
 */


data class Carrello (

    @Json(name = "id")
    val id: Long? = null,

    @Json(name = "utente")
    val utente: Utente? = null,

    @Json(name = "carrelloProdotti")
    val carrelloProdotti: List<CarrelloProdotti>? = null

)

