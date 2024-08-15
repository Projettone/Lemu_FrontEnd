package it.unical.ea.lemu_frontend.viewmodels

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.openapitools.client.apis.PaymentControllerApi

class PaymentViewModel(private val authViewModel: AuthViewModel) {
    private val api: PaymentControllerApi = PaymentControllerApi(authViewModel)


    suspend fun checkOut(total: Double): Boolean {
        return try {
            val response = withContext(Dispatchers.IO) {
                api.processPayment(total)
            }
            println("RISPOSTA: $response")
            response == "Pagamento_effettuato_con_successo"
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}