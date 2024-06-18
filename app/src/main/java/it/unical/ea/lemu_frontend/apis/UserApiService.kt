package it.unical.ea.lemu_frontend.apis

import it.unical.ea.lemu_frontend.models.UtenteRegistrationDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {
    @POST("api/v1/register")
    fun registerUser(@Body user: UtenteRegistrationDto): Call<ApiResponse<UtenteRegistrationDto>>

    @POST("api/v1/login")
    fun loginUser(@Body credentials: UtenteRegistrationDto): Call<ApiResponse<UtenteRegistrationDto>>
}