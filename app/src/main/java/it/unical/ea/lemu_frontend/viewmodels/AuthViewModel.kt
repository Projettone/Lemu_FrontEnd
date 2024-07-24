package it.unical.ea.lemu_frontend.viewmodels

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import it.unical.ea.lemu_frontend.R
import android.app.Activity
import android.content.Intent
import com.google.android.gms.common.api.ApiException
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openapitools.client.apis.UtenteControllerApi
import org.openapitools.client.models.ApiResponseString
import org.openapitools.client.models.UtenteRegistrazioneDto
class AuthViewModel (private val activity: Activity) {

    private val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(
        activity,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.client_id))
            .requestEmail()
            .build()
    )

    fun signInWithGoogle(signInLauncher: ActivityResultLauncher<Intent>) {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    fun handleSignInResult(resultCode: Int, data: Intent?): ApiResponseString? {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            return handleSignInResult(account)
        } catch (e: ApiException) {
            Log.w("AuthViewModel", "Google sign in failed", e)
        }
        return null
    }

    private fun handleSignInResult(account: GoogleSignInAccount?): ApiResponseString? {
        account?.let {
            val idToken = it.idToken
            val email = it.email
            val displayName = it.displayName
            val givenName = it.givenName
            val familyName = it.familyName
            val photoUrl = it.photoUrl.toString()
            Log.d("AuthViewModel", "ID Token: $idToken")
            Log.d("AuthViewModel", "Email: $email")
            Log.d("AuthViewModel", "Display Name: $displayName")
            Log.d("AuthViewModel", "Given Name: $givenName")
            Log.d("AuthViewModel", "Family Name: $familyName")
            Log.d("AuthViewModel", "Photo URL: $photoUrl")

            if (idToken != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    val response = api.googleAuthentication(idToken)
                    println("SUCCESS: " + response.success)
                    println("MESSAGE: " + response.message)
                    println("DATA: " + response.data)
                    response
                }
            }

        } ?: run {
            Log.w("AuthViewModel", "Google sign in failed, account is null")
        }
        return null
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }








    private val api: UtenteControllerApi = UtenteControllerApi()

    /*
    fun login(email: String, password: String): Boolean? {
        val loginDto = UtenteLoginDto(credenzialiEmail = email, credenzialiPassword = password)
        return try {
            val response = api.login(loginDto)
            println("SUCCESS: " + response.success)
            println("MESSAGE: " + response.message)
            println("DATA: " + response.data)
            response.success
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

     */


    fun register(name: String, surname: String, email: String, password: String): Boolean? {
        val registrazioneDto = UtenteRegistrazioneDto(nome = name, cognome = surname, credenzialiEmail = email, credenzialiPassword = password)
        return try {
            val response = api.registerUser(registrazioneDto)
            println("SUCCESS: " + response.success)
            println("MESSAGE: " + response.message)
            println("DATA: " + response.data)
            response.success
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun authenticate(email: String, password: String): Boolean? {
        return try {
            val response = api.authenticate(email, password)
            println("SUCCESS: " + response.success)
            println("MESSAGE: " + response.message)
            println("DATA: " + response.data)
            response.success
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

/*
    fun google_authenticate(email: String, password: String): Boolean? {
        return try {
            val response = api.googleAuthentication(email, password)
            println("SUCCESS: " + response.success)
            println("MESSAGE: " + response.message)
            println("DATA: " + response.data)
            response.success
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

 */


    fun validateCredenzialiEmail(email: String): Boolean {
        return email.isNotEmpty() && email.contains("@") && email.contains(".")
    }

    fun validateCredenzialiPassword(password: String): Boolean {
        return password.isNotEmpty() && password.length >= 1
    }

}