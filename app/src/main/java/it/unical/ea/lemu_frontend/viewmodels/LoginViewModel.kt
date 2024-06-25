package it.unical.ea.lemu_frontend.viewmodels

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.openapitools.client.apis.UtenteControllerApi
import org.openapitools.client.infrastructure.ApiClient
import org.openapitools.client.infrastructure.ApiResponse as OpenApiResponse
import org.openapitools.client.infrastructure.ClientError
import org.openapitools.client.infrastructure.ResponseType
import org.openapitools.client.infrastructure.ServerError
import org.openapitools.client.infrastructure.Success
import org.openapitools.client.models.ApiResponseString
import org.openapitools.client.models.ApiResponseUtente
import org.openapitools.client.models.UtenteLoginDto
import org.openapitools.client.models.UtenteRegistrazioneDto
class LoginViewModel {


    private val api: UtenteControllerApi = UtenteControllerApi()

    fun login(email: String, password: String): ApiResponseString {
        val loginDto = UtenteLoginDto(credenzialiEmail = email, credenzialiPassword = password)

        try {
            val response = api.loginWithHttpInfo(loginDto)

            return when (response.responseType) {
                ResponseType.Success -> {
                    if (response is Success<*>) {
                        val responseData = (response as Success<*>).data
                        if (responseData is ApiResponseString) {
                            println("Login successful. Token: ${responseData.data}")
                            responseData
                        } else {
                            ApiResponseString(
                                success = false,
                                message = "Dati di risposta non validi"
                            )
                        }
                    } else {
                        ApiResponseString(
                            success = false,
                            message = "Tipo di risposta non gestito"
                        )
                    }
                }

                ResponseType.ClientError, ResponseType.ServerError -> {
                    ApiResponseString(
                        success = false,
                        message = "Errore durante il login: ${response.statusCode}"
                    )
                }

                else -> {
                    ApiResponseString(
                        success = false,
                        message = "Errore sconosciuto durante il login"
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ApiResponseString(
                success = false,
                message = "Errore durante il login: ${e.message}"
            )
        }
    }


    fun register(name: String, surname: String, email: String, password: String): ApiResponseUtente {
        val registrazioneDto = UtenteRegistrazioneDto(nome = name, cognome = surname, credenzialiEmail = email, credenzialiPassword = password)
        return api.register(registrazioneDto)
    }

    fun validateCredenzialiEmail(email: String): Boolean {
        return email.isNotEmpty() && email.contains("@") && email.contains(".")
    }

    fun validateCredenzialiPassword(password: String): Boolean {
        return password.isNotEmpty() && password.length >= 8
    }

}