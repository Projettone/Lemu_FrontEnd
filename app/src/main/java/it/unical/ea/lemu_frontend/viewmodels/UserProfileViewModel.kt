package it.unical.ea.lemu_frontend.viewmodels

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.openapitools.client.apis.CouponControllerApi
import org.openapitools.client.apis.UtenteControllerApi
import org.openapitools.client.models.CouponDto

class UserProfileViewModel(private val authViewModel: AuthViewModel) {
    private val utenteControllerApi: UtenteControllerApi = UtenteControllerApi(authViewModel)
    private val couponControllerApi: CouponControllerApi = CouponControllerApi(authViewModel)

    private val _coupons = MutableStateFlow<List<CouponDto>>(emptyList())
    val coupons: StateFlow<List<CouponDto>> get() = _coupons.asStateFlow()

    private val _saldo = MutableStateFlow<Double>(0.0)
    val saldo: StateFlow<Double> get() = _saldo.asStateFlow()


    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        viewModelScope.launch {
            _saldo.value = getSaldoCoupon() ?: 0.0
            getActiveCoupons()
            updateSaldo()
        }
    }

    private suspend fun <T> apiCall(call: suspend () -> T): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                call()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun banUser(email: String): Boolean {
        return apiCall { utenteControllerApi.banUser(email) }
    }

    suspend fun unbanUser(email: String): Boolean {
        return apiCall { utenteControllerApi.unbanUser(email) }
    }

    suspend fun makeAdmin(email: String): Boolean {
        return apiCall { utenteControllerApi.makeAdmin(email) }
    }

    suspend fun revokeAdmin(email: String): Boolean {
        return apiCall { utenteControllerApi.revokeAdmin(email) }
    }

    suspend fun getActiveCoupons(): Boolean {
        return apiCall {
            val response = couponControllerApi.getAllValid()
            _coupons.value = response
        }
    }

    suspend fun createCoupon(value: Double) {
        apiCall {
            couponControllerApi.creaCoupon(value)
        }
        getActiveCoupons()
    }

    suspend fun redeemCoupon(voucherCode: String) {
        apiCall {
            couponControllerApi.riscattaCoupon(voucherCode)
        }
        getActiveCoupons()
        updateSaldo()
    }

    suspend fun updateSaldo() {
        _saldo.value = getSaldoCoupon() ?: 0.0
    }

    suspend fun getSaldoCoupon(): Double? {
        return try {
            authViewModel.getUserData()
            return authViewModel.user.value?.saldo
        } catch (e: Exception) {
            0.0
        }
    }
}
