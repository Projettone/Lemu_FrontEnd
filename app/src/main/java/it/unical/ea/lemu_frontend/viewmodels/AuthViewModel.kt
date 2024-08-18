package it.unical.ea.lemu_frontend.viewmodels

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import it.unical.ea.lemu_frontend.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.gms.common.api.ApiException
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.openapitools.client.apis.UtenteControllerApi
import org.openapitools.client.models.UtenteDto
import org.openapitools.client.models.UtenteRegistrazioneDto
import com.google.gson.Gson
import org.openapitools.client.models.ApiResponseString
import org.openapitools.client.models.Indirizzo
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

class AuthViewModel (private val activity: Activity) {

    private val api: UtenteControllerApi = UtenteControllerApi(this)
    val isLoggedIn = mutableStateOf(false)
    var user = mutableStateOf<UtenteDto?>(null)

    private val userSharedPreferences: SharedPreferences = activity.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val authSharedPreferences: SharedPreferences = activity.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading


    init {
        loadUserData()
    }


    private val callbackManager = CallbackManager.Factory.create()


    private val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(
        activity,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.client_id))
            .requestEmail()
            .build()
    )

    fun signInWithGoogle(signInLauncher: ActivityResultLauncher<Intent>) {
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            signInLauncher.launch(signInIntent)
        }
    }


    fun handleSignInResult(data: Intent?): Boolean {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            return handleSignInResult(account)
        } catch (e: ApiException) {
            Log.w("AuthViewModel", "Google sign in failed", e)
        }
        return false
    }

    private fun handleSignInResult(account: GoogleSignInAccount?): Boolean {
        account?.let {
            if (it.idToken != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    val response = api.googleAuthentication(it.idToken!!)
                    if(response.message == "403"){
                        response.success
                    } else if (response.data != null){
                        saveToken(response.data)
                        isLoggedIn.value = true
                        withContext(Dispatchers.Main){
                            getUserData()
                        }
                    }
                    response.success
                }
            }

        } ?: run {
            Log.w("AuthViewModel", "Google sign in failed, account is null")
        }
        return false
    }




    fun checkAuthentication(): Boolean {
        val token = getToken()
        return token != null
    }


    suspend fun register(name: String, surname: String, email: String, password: String): ApiResponseString {
        _isLoading.value = true
        val registrazioneDto = UtenteRegistrazioneDto(
            nome = name,
            cognome = surname,
            credenzialiEmail = email,
            credenzialiPassword = password
        )
        return try {
            val response = api.registerUser(registrazioneDto)

            if (response.success == true) {
                response.data?.let { saveToken(it) }
                isLoggedIn.value = true
                withContext(Dispatchers.Main) {
                    getUserData()
                }
            }
            response
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResponseString(false, "500", "Internal server error")
        } finally {
            _isLoading.value = false
        }
    }




    suspend fun authenticate(email: String, password: String): ApiResponseString {
        _isLoading.value = true
        return try {
            val response = api.authenticate(email, password)

            if (response.success == true) {
                response.data?.let { saveToken(it) }
                isLoggedIn.value = true
                withContext(Dispatchers.Main) {
                    getUserData()
                }
            }
            response
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResponseString(false, "500", "Internal server error");
        } finally {
            _isLoading.value = false
        }
    }



    suspend fun changePassword(password: String): Boolean {
        _isLoading.value = true
        return withContext(Dispatchers.IO) {
            try {
                api.updatePassword(password)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun updateAddress(address: Indirizzo): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                api.updateShippingAddress(address)
                getUserData()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }




    fun logout() {
        clearToken()
        isLoggedIn.value = false
        this.user.value = null;
        clearUserData()
    }


    fun validateCredenzialiEmail(email: String): Boolean {
        return email.isNotEmpty() && email.contains("@") && email.contains(".")
    }

    fun validateCredenzialiPassword(password: String): Boolean {
        return password.isNotEmpty() && password.length >= 1
    }

    private fun saveToken(token: String) {
        authSharedPreferences.edit().putString("jwt_token", token).apply()
    }

    fun getToken(): String? {
        return authSharedPreferences.getString("jwt_token", null)
    }

    private fun clearToken() {
        authSharedPreferences.edit().remove("jwt_token").apply()
    }

    suspend fun getUserData() {
        withContext(Dispatchers.IO) {
            try {
                val response = api.getUserData()
                user.value = response.data
                saveUserData(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveUserData(user: UtenteDto?) {
        userSharedPreferences.edit().putString("user_data", gson.toJson(user)).apply()
    }

    private fun clearUserData(){
        userSharedPreferences.edit().remove("user_data").apply()
    }

    private fun loadUserData() {
        val userDataJson = userSharedPreferences.getString("user_data", null)
        if (userDataJson != null) {
            user.value = gson.fromJson(userDataJson, UtenteDto::class.java)
        }
    }



    fun signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(activity, listOf("email", "public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val accessToken = result.accessToken
                handleFacebookAccessToken(accessToken)
            }

            override fun onCancel() {
                Log.d("AuthViewModel", "Facebook login canceled")
            }

            override fun onError(error: FacebookException) {
                Log.e("AuthViewModel", "Facebook login error", error)
            }
        })
    }




    private fun handleFacebookAccessToken(token: AccessToken?) {
        token?.let {
            CoroutineScope(Dispatchers.IO).launch {
                val response = api.facebookAuthentication(token.token)

                if (response.data != null){
                    saveToken(response.data)
                    isLoggedIn.value = true
                    withContext(Dispatchers.Main){
                        getUserData()
                    }
                }
            }
        }
    }


    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }


    suspend fun sendPasswordResetEmail(email: String) {
        _isLoading.value = true
        return withContext(Dispatchers.IO) {
            try {
                api.sendPasswordResetEmail(email)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }




}