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
 * Data class for RecensioneDto
 *
 * @param rating The rating given in the review
 * @param commento The comment text of the review
 * @param nomeProdotto The name of the product being reviewed
 */
@JsonClass(generateAdapter = true)
data class RecensioneDto (

    @Json(name = "id")
    val id: kotlin.Long,

    @Json(name = "rating")
    val rating: kotlin.Float,

    @Json(name = "commento")
    val commento: kotlin.String? = null,

    @Json(name = "nomeProdotto")
    val nomeProdotto: kotlin.String
)
