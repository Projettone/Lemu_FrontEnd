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
 * @param nome 
 * @param cognome 
 * @param credenzialiEmail 
 * @param credenzialiPassword 
 */


data class UtenteRegistrazioneDto (

    @Json(name = "nome")
    val nome: kotlin.String? = null,

    @Json(name = "cognome")
    val cognome: kotlin.String? = null,

    @Json(name = "credenzialiEmail")
    val credenzialiEmail: kotlin.String? = null,

    @Json(name = "credenzialiPassword")
    val credenzialiPassword: kotlin.String? = null

)

