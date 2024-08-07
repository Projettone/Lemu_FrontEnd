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

import org.openapitools.client.models.Carrello
import org.openapitools.client.models.Prodotto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 
 *
 * @param id 
 * @param carrello 
 * @param prodotto 
 * @param quantita 
 */


data class CarrelloProdotti (

    @Json(name = "id")
    val id: kotlin.Long? = null,

    @Json(name = "carrello")
    val carrello: Carrello? = null,

    @Json(name = "prodotto")
    val prodotto: Prodotto? = null,

    @Json(name = "quantita")
    val quantita: kotlin.Int? = null

) {


}

