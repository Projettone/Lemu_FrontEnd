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
 * @param codice
 * @param valore
 */

@JsonClass(generateAdapter = true)
data class CouponDto (

    @Json(name = "codice")
    val codice: kotlin.String? = null,

    @Json(name = "valore")
    val valore: kotlin.Double? = null

)