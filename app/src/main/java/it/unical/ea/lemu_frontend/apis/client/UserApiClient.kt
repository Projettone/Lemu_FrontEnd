package it.unical.ea.lemu_frontend.apis.client

import it.unical.ea.lemu_frontend.apis.ApiResponse
import it.unical.ea.lemu_frontend.config.RetrofitClient
import it.unical.ea.lemu_frontend.models.UtenteRegistrationDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserApiClient {

    fun registerUser(
        user: UtenteRegistrationDto,
        callback: (ApiResponse<UtenteRegistrationDto>) -> Unit
    ) {
        RetrofitClient.userApiService.registerUser(user)
            .enqueue(object : Callback<ApiResponse<UtenteRegistrationDto>> {
                override fun onResponse(
                    call: Call<ApiResponse<UtenteRegistrationDto>>,
                    response: Response<ApiResponse<UtenteRegistrationDto>>
                ) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        if (apiResponse != null) {
                            callback(apiResponse)
                        } else {
                            callback(ApiResponse(false, "Errore: risposta vuota", null))
                        }
                    } else {
                        val errorBody =
                            response.errorBody()?.string() ?: "Errore durante la registrazione"
                        callback(ApiResponse(false, errorBody, null))
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse<UtenteRegistrationDto>>,
                    t: Throwable
                ) {
                    callback(ApiResponse(false, "Errore di rete durante la registrazione: ${t.message}", null))
                }
            })
    }
}

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
