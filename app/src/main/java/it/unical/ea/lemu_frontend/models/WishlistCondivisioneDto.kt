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
 * @param wishlistId 
 * @param email 
 */


data class WishlistCondivisioneDto (

    @Json(name = "id")
    val id: kotlin.Long? = null,

    @Json(name = "wishlistId")
    val wishlistId: kotlin.Long? = null,

    @Json(name = "email")
    val email: kotlin.String? = null

)

