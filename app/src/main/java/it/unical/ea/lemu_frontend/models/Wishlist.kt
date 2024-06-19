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

import org.openapitools.client.models.Utente
import org.openapitools.client.models.WishlistProdotti

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 *
 *
 * @param id
 * @param utente
 * @param wishlistProdotti
 */


data class Wishlist (

    @Json(name = "id")
    val id: Long? = null,

    @Json(name = "utente")
    val utente: Utente? = null,

    @Json(name = "wishlistProdotti")
    val wishlistProdotti: List<WishlistProdotti>? = null

)

