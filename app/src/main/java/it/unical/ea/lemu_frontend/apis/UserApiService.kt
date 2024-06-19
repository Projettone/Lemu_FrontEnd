package it.unical.ea.lemu_frontend.apis

import org.openapitools.client.models.UtenteRegistrazioneDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {
    @POST("api/v1/register")
    fun registerUser(@Body user: UtenteRegistrazioneDto): Call<ApiResponse<UtenteRegistrazioneDto>>

    @POST("api/v1/login")
    fun loginUser(@Body credentials: UtenteRegistrazioneDto): Call<ApiResponse<UtenteRegistrazioneDto>>
}
