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
 * @param id
 * @param utenteId
 * @param prodotti
 */


data class CarrelloDto (

    @Json(name = "id")
    val id: Long? = null,

    @Json(name = "utenteId")
    val utenteId: Long? = null,

    @Json(name = "prodotti")
    val prodotti: List<Long>? = null

)

