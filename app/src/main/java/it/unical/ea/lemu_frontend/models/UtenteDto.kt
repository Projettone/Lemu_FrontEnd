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

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 
 *
 * @param id 
 * @param nome 
 * @param cognome 
 * @param isAdmin 
 * @param immagineProfilo 
 * @param indirizzo 
 * @param username 
 * @param email 
 * @param saldo 
 */


data class UtenteDto (

    @Json(name = "id")
    val id: kotlin.Long? = null,

    @Json(name = "nome")
    val nome: kotlin.String? = null,

    @Json(name = "cognome")
    val cognome: kotlin.String? = null,

    @Json(name = "isAdmin")
    val isAdmin: kotlin.Boolean? = null,

    @Json(name = "immagineProfilo")
    val immagineProfilo: kotlin.String? = null,

    @Json(name = "indirizzo")
    val indirizzo: Indirizzo? = null,

    @Json(name = "username")
    val username: kotlin.String? = null,

    @Json(name = "email")
    val email: kotlin.String? = null,

    @Json(name = "saldo")
    val saldo: kotlin.Double? = null

) {


}

