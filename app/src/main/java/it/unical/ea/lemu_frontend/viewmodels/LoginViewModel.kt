package it.unical.ea.lemu_frontend.viewmodels

import it.unical.ea.lemu_frontend.apis.ApiResponse
import org.openapitools.client.apis.UtenteControllerApi
import org.openapitools.client.infrastructure.ClientError
import org.openapitools.client.infrastructure.ResponseType
import org.openapitools.client.infrastructure.Success
import org.openapitools.client.models.ApiResponseUtente
import org.openapitools.client.models.UtenteLoginDto
import org.openapitools.client.models.UtenteRegistrazioneDto

class LoginViewModel {


    fun registerUser(user: UtenteRegistrazioneDto, callback: (ApiResponse<ApiResponseUtente?>) -> Unit) {
        try {

            val response = UtenteControllerApi().registerWithHttpInfo(user)

            if (response.responseType == ResponseType.Success) {
                val apiResponse = response as Success<ApiResponseUtente?>
                callback(ApiResponse(true, "Registrazione avvenuta con successo", apiResponse.data))
            } else {
                val errorMessage = (response as? ClientError<*>)?.message ?: "Errore durante la registrazione"
                callback(ApiResponse(false, errorMessage, null))
            }
        } catch (e: Exception) {
            callback(ApiResponse(false, "Errore di rete durante la registrazione: ${e.message}", null))
        }
    }



    fun loginUser(user: UtenteLoginDto, callback: (ApiResponse<String>) -> Unit) {
        try {
            val response =
                user.credenzialiEmail?.let { UtenteControllerApi().authenticateWithHttpInfo(it, user.credenzialiPassword) }

            if (response != null) {
                if (response.responseType == ResponseType.Success) {
                    callback(ApiResponse(true, "Login avvenuto con successo", null))
                } else {
                    val errorMessage = (response as? ClientError<*>)?.message ?: "Errore durante il login"
                    callback(ApiResponse(false, errorMessage, null))
                }
            }
        } catch (e: Exception) {
            callback(ApiResponse(false, "Errore di rete durante il login: ${e.message}", null))
        }
    }


    fun validateCredenzialiEmail(email: String): Boolean {
        return email.isNotEmpty() && email.contains("@") && email.contains(".")
    }

    fun validateCredenzialiPassword(password: String): Boolean {
        return password.isNotEmpty() && password.length >= 8
    }

}



