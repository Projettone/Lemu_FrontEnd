package it.unical.ea.lemu_frontend.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.openapitools.client.apis.CouponControllerApi
import org.openapitools.client.apis.RecensioneControllerApi
import org.openapitools.client.apis.UtenteControllerApi
import org.openapitools.client.models.CouponDto
import org.openapitools.client.models.RecensioneDto

class UserProfileViewModel(private val authViewModel: AuthViewModel) {
    private val utenteControllerApi: UtenteControllerApi = UtenteControllerApi(authViewModel)
    private val couponControllerApi: CouponControllerApi = CouponControllerApi(authViewModel)
    private val recensioniControllerApi: RecensioneControllerApi = RecensioneControllerApi(authViewModel)


    private val _coupons = MutableStateFlow<List<CouponDto>>(emptyList())
    val coupons: StateFlow<List<CouponDto>> get() = _coupons.asStateFlow()


    private val _recensioni = MutableStateFlow<List<RecensioneDto>>(emptyList())
    val recensioni: StateFlow<List<RecensioneDto>> get() = _recensioni.asStateFlow()


    private val _saldo = MutableStateFlow<Double>(0.0)
    val saldo: StateFlow<Double> get() = _saldo.asStateFlow()


    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var currentPageCoupon = 0
    var currentPageRecensioni = 0
    val pageSize = 1
    var totalPagesCoupon by mutableStateOf(0)
    var totalPagesRecensioni by mutableStateOf(0)


    init {
        viewModelScope.launch {
            _saldo.value = getSaldoCoupon() ?: 0.0
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

    suspend fun createCoupon(value: Double) {
        apiCall {
            couponControllerApi.creaCoupon(value)
        }
        getPagedCoupons()
    }

    suspend fun redeemCoupon(voucherCode: String) {
        apiCall {
            couponControllerApi.riscattaCoupon(voucherCode)
        }
        getPagedCoupons()
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


    suspend fun getPagedCoupons(): Boolean {
        return apiCall {
            val response = couponControllerApi.getPagedCoupons(currentPageCoupon,pageSize)
            _coupons.value = response.content!!
            totalPagesCoupon = response.totalPages!!
        }
    }

    fun loadNextPageCoupons() {
        viewModelScope.launch {
            currentPageCoupon += 1
            getPagedCoupons()
        }
    }

    fun loadPreviousPageCoupons() {
        if (currentPageCoupon > 0) {
            viewModelScope.launch {
                currentPageCoupon -= 1
                getPagedCoupons()
            }
        }
    }


    suspend fun getPagedReviews(): Boolean {
        return apiCall {
            val response = recensioniControllerApi.getRecensioniByUtente(currentPageRecensioni,pageSize)
            _recensioni.value = response.content!!
            totalPagesRecensioni = response.totalPages!!
            println(_recensioni.value)
        }
    }


    fun loadNextPageReviews() {
        viewModelScope.launch {
            if (currentPageRecensioni < totalPagesRecensioni - 1) {
                currentPageRecensioni += 1
                getPagedReviews()
            }
        }
    }

    fun loadPreviousPageReviews() {
        if (currentPageRecensioni > 0) {
            viewModelScope.launch {
                currentPageRecensioni -= 1
                getPagedReviews()
            }
        }
    }

    suspend fun deleteReview(id: Long){
        apiCall {
            recensioniControllerApi.deleteReview(id)
        }
    }
}
