package it.unical.ea.lemu_frontend.viewmodels

import it.unical.ea.lemu_frontend.apis.ApiResponse
import it.unical.ea.lemu_frontend.config.RetrofitClient
import it.unical.ea.lemu_frontend.models.UtenteLoginDto
import org.openapitools.client.apis.UtenteControllerApi
import org.openapitools.client.infrastructure.ClientError
import org.openapitools.client.infrastructure.ResponseType
import org.openapitools.client.infrastructure.Success
import org.openapitools.client.models.ApiResponseUtente
import org.openapitools.client.models.UtenteRegistrazioneDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LoginViewModel {
    private val userApiService: UtenteControllerApi by lazy {
        RetrofitClient.userApiService
    }

    /*
    suspend fun registerUser(user: UtenteRegistrazioneDto, callback: (ApiResponse<UtenteRegistrazioneDto>) -> Unit) {
        try {
            val response = userApiService.registerWithHttpInfo(user).await()

            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null) {
                    callback(apiResponse)
                } else {
                    callback(ApiResponse(false, "Errore: risposta vuota", null))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Errore durante la registrazione"
                callback(ApiResponse(false, errorBody, null))
            }
        } catch (e: Exception) {
            callback(ApiResponse(false, "Errore di rete durante la registrazione: ${e.message}", null))
        }
    }

     */


    suspend fun registerUser(user: UtenteRegistrazioneDto, callback: (ApiResponse<ApiResponseUtente?>) -> Unit) {
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

    fun validateCredenzialiEmail(email: String): Boolean {
        return email.isNotEmpty() && email.contains("@") && email.contains(".")
    }

    fun validateCredenzialiPassword(password: String): Boolean {
        return password.isNotEmpty() && password.length > 8
    }


    /*
    suspend fun loginUser(user: UtenteLoginDto, callback: (ApiResponse<String>) -> Unit) {
        try {
            val response = userApiService.loginUser(user).await()

            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null) {
                    callback(apiResponse)
                } else {
                    callback(ApiResponse(false, "Errore: risposta vuota", null))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Errore durante il login"
                callback(ApiResponse(false, errorBody, null))
            }
        } catch (e: Exception) {
            callback(ApiResponse(false, "Errore di rete durante il login: ${e.message}", null))
        }
    }

     */

    suspend fun loginUser(user: UtenteLoginDto, callback: (ApiResponse<String>) -> Unit) {
        try {
            val response = UtenteControllerApi().authenticateWithHttpInfo(user.credenzialiEmail, user.credenzialiPassword)

            if (response.responseType == ResponseType.Success) {
                callback(ApiResponse(true, "Login avvenuto con successo", null))
            } else {
                val errorMessage = (response as? ClientError<*>)?.message ?: "Errore durante il login"
                callback(ApiResponse(false, errorMessage, null))
            }
        } catch (e: Exception) {
            callback(ApiResponse(false, "Errore di rete durante il login: ${e.message}", null))
        }
    }


    private suspend fun <T : Any> Call<T>.await(): Response<T> {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    continuation.resume(response)
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}


/*
fun main() {
    val userApiClient = UserApiClient()

    val utenteRegistrazioneDto = UtenteRegistrationDto(
        credenzialiEmail = "email@example.com",
        credenzialiPassword = "password",
        nome = "Nome",
        cognome = "Cognome"
    )

    userApiClient.registerUser(utenteRegistrazioneDto) { apiResponse ->
        if (apiResponse.success) {
            val message = apiResponse.message
            val data = apiResponse.data

            println("Messaggio dal server: $message")

            data?.let {
            }
        } else {
            val errorMessage = apiResponse.message
            println("Errore dal server: $errorMessage")
        }
    }
}


 */



