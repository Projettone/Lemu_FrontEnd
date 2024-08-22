/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package org.openapitools.client.apis

import java.io.IOException
import okhttp3.OkHttpClient
import okhttp3.HttpUrl
import org.openapitools.client.models.CarrelloDto
import org.openapitools.client.models.CarrelloProdottiDto
import com.squareup.moshi.Json
import it.unical.ea.lemu_frontend.viewmodels.AuthViewModel
import org.openapitools.client.infrastructure.ApiClient
import org.openapitools.client.infrastructure.ApiResponse
import org.openapitools.client.infrastructure.ClientException
import org.openapitools.client.infrastructure.ClientError
import org.openapitools.client.infrastructure.ServerException
import org.openapitools.client.infrastructure.ServerError
import org.openapitools.client.infrastructure.MultiValueMap
import org.openapitools.client.infrastructure.RequestConfig
import org.openapitools.client.infrastructure.RequestMethod
import org.openapitools.client.infrastructure.ResponseType
import org.openapitools.client.infrastructure.Success

class CarrelloControllerApi(private val authViewModel: AuthViewModel, basePath: kotlin.String = defaultBasePath, client: OkHttpClient = ApiClient.defaultClient) : ApiClient(basePath, client) {
    companion object {
        @JvmStatic
        val defaultBasePath: String by lazy {
            System.getProperties().getProperty(ApiClient.baseUrlKey, "http://192.168.1.9:8080")
        }
    }

    /**
     * 
     * 
     * @param carrelloDto 
     * @return CarrelloDto
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ClientException If the API returns a client error response
     * @throws ServerException If the API returns a server error response
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class, UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun createCarrello(carrelloDto: CarrelloDto) : CarrelloDto {
        val localVarResponse = createCarrelloWithHttpInfo(carrelloDto = carrelloDto)

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as CarrelloDto
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()} ${localVarError.body}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
     * 
     * 
     * @param carrelloDto 
     * @return ApiResponse<CarrelloDto?>
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class)
    fun createCarrelloWithHttpInfo(carrelloDto: CarrelloDto) : ApiResponse<CarrelloDto?> {
        val localVariableConfig = createCarrelloRequestConfig(carrelloDto = carrelloDto)

        return request<CarrelloDto, CarrelloDto>(
            localVariableConfig
        )
    }

    /**
     * To obtain the request config of the operation createCarrello
     *
     * @param carrelloDto 
     * @return RequestConfig
     */
    fun createCarrelloRequestConfig(carrelloDto: CarrelloDto) : RequestConfig<CarrelloDto> {
        val localVariableBody = carrelloDto
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        localVariableHeaders["Content-Type"] = "application/json"

        authViewModel.getToken()?.let { token ->
            localVariableHeaders["Authorization"] = "Bearer $token"
        }
        
        return RequestConfig(
            method = RequestMethod.POST,
            path = "/carrello-api/add",
            query = localVariableQuery,
            headers = localVariableHeaders,
            requiresAuthentication = false,
            body = localVariableBody
        )
    }

    /**
     * 
     * 
     * @param carrelloProdottiDto 
     * @return CarrelloProdottiDto
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ClientException If the API returns a client error response
     * @throws ServerException If the API returns a server error response
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class, UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun createCarrelloProdotti(carrelloProdottiDto: CarrelloProdottiDto) : CarrelloProdottiDto {
        val localVarResponse = createCarrelloProdottiWithHttpInfo(carrelloProdottiDto = carrelloProdottiDto)

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as CarrelloProdottiDto
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()} ${localVarError.body}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
     * 
     * 
     * @param carrelloProdottiDto 
     * @return ApiResponse<CarrelloProdottiDto?>
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class)
    fun createCarrelloProdottiWithHttpInfo(carrelloProdottiDto: CarrelloProdottiDto) : ApiResponse<CarrelloProdottiDto?> {
        val localVariableConfig = createCarrelloProdottiRequestConfig(carrelloProdottiDto = carrelloProdottiDto)

        return request<CarrelloProdottiDto, CarrelloProdottiDto>(
            localVariableConfig
        )
    }

    /**
     * To obtain the request config of the operation createCarrelloProdotti
     *
     * @param carrelloProdottiDto 
     * @return RequestConfig
     */
    fun createCarrelloProdottiRequestConfig(carrelloProdottiDto: CarrelloProdottiDto) : RequestConfig<CarrelloProdottiDto> {
        val localVariableBody = carrelloProdottiDto
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        localVariableHeaders["Content-Type"] = "application/json"

        authViewModel.getToken()?.let { token ->
            localVariableHeaders["Authorization"] = "Bearer $token"
        }
        
        return RequestConfig(
            method = RequestMethod.POST,
            path = "/carrello-api/prodotti/add",
            query = localVariableQuery,
            headers = localVariableHeaders,
            requiresAuthentication = false,
            body = localVariableBody
        )
    }

    /**
     * 
     * 
     * @param id 
     * @return void
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ClientException If the API returns a client error response
     * @throws ServerException If the API returns a server error response
     */
    @Throws(IllegalStateException::class, IOException::class, UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun deleteCarrello(id: kotlin.Long) : Unit {
        val localVarResponse = deleteCarrelloWithHttpInfo(id = id)

        return when (localVarResponse.responseType) {
            ResponseType.Success -> Unit
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()} ${localVarError.body}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
     * 
     * 
     * @param id 
     * @return ApiResponse<Unit?>
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     */
    @Throws(IllegalStateException::class, IOException::class)
    fun deleteCarrelloWithHttpInfo(id: kotlin.Long) : ApiResponse<Unit?> {
        val localVariableConfig = deleteCarrelloRequestConfig(id = id)

        return request<Unit, Unit>(
            localVariableConfig
        )
    }

    /**
     * To obtain the request config of the operation deleteCarrello
     *
     * @param id 
     * @return RequestConfig
     */
    fun deleteCarrelloRequestConfig(id: kotlin.Long) : RequestConfig<Unit> {
        val localVariableBody = null
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()

        authViewModel.getToken()?.let { token ->
            localVariableHeaders["Authorization"] = "Bearer $token"
        }
        
        return RequestConfig(
            method = RequestMethod.DELETE,
            path = "/carrello-api/delete/{id}".replace("{"+"id"+"}", encodeURIComponent(id.toString())),
            query = localVariableQuery,
            headers = localVariableHeaders,
            requiresAuthentication = false,
            body = localVariableBody
        )
    }

    /**
     * 
     * 
     * @param id 
     * @return void
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ClientException If the API returns a client error response
     * @throws ServerException If the API returns a server error response
     */
    @Throws(IllegalStateException::class, IOException::class, UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun deleteCarrelloProdotti(id: kotlin.Long) : Unit {
        val localVarResponse = deleteCarrelloProdottiWithHttpInfo(id = id)

        return when (localVarResponse.responseType) {
            ResponseType.Success -> Unit
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()} ${localVarError.body}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
     * 
     * 
     * @param id 
     * @return ApiResponse<Unit?>
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     */
    @Throws(IllegalStateException::class, IOException::class)
    fun deleteCarrelloProdottiWithHttpInfo(id: kotlin.Long) : ApiResponse<Unit?> {
        val localVariableConfig = deleteCarrelloProdottiRequestConfig(id = id)

        return request<Unit, Unit>(
            localVariableConfig
        )
    }

    /**
     * To obtain the request config of the operation deleteCarrelloProdotti
     *
     * @param id 
     * @return RequestConfig
     */
    fun deleteCarrelloProdottiRequestConfig(id: kotlin.Long) : RequestConfig<Unit> {
        val localVariableBody = null
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()

        authViewModel.getToken()?.let { token ->
            localVariableHeaders["Authorization"] = "Bearer $token"
        }
        
        return RequestConfig(
            method = RequestMethod.DELETE,
            path = "/carrello-api/prodotti/delete/{id}".replace("{"+"id"+"}", encodeURIComponent(id.toString())),
            query = localVariableQuery,
            headers = localVariableHeaders,
            requiresAuthentication = false,
            body = localVariableBody
        )
    }

    /**
     * 
     * 
     * @param carrelloid 
     * @return kotlin.collections.List<CarrelloProdottiDto>
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ClientException If the API returns a client error response
     * @throws ServerException If the API returns a server error response
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class, UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun getAllCarrelloProdottiByCarrelloId(carrelloid: kotlin.Long) : kotlin.collections.List<CarrelloProdottiDto> {
        val localVarResponse = getAllCarrelloProdottiByCarrelloIdWithHttpInfo(carrelloid = carrelloid)

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as kotlin.collections.List<CarrelloProdottiDto>
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()} ${localVarError.body}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
     * 
     * 
     * @param carrelloid 
     * @return ApiResponse<kotlin.collections.List<CarrelloProdottiDto>?>
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class)
    fun getAllCarrelloProdottiByCarrelloIdWithHttpInfo(carrelloid: kotlin.Long) : ApiResponse<kotlin.collections.List<CarrelloProdottiDto>?> {
        val localVariableConfig = getAllCarrelloProdottiByCarrelloIdRequestConfig(carrelloid = carrelloid)

        return request<Unit, kotlin.collections.List<CarrelloProdottiDto>>(
            localVariableConfig
        )
    }

    /**
     * To obtain the request config of the operation getAllCarrelloProdottiByCarrelloId
     *
     * @param carrelloid 
     * @return RequestConfig
     */
    fun getAllCarrelloProdottiByCarrelloIdRequestConfig(carrelloid: kotlin.Long) : RequestConfig<Unit> {
        val localVariableBody = null
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()

        authViewModel.getToken()?.let { token ->
            localVariableHeaders["Authorization"] = "Bearer $token"
        }

        return RequestConfig(
            method = RequestMethod.GET,
            path = "/carrello-api/prodotti/get/{carrelloid}/all".replace("{"+"carrelloid"+"}", encodeURIComponent(carrelloid.toString())),
            query = localVariableQuery,
            headers = localVariableHeaders,
            requiresAuthentication = false,
            body = localVariableBody
        )
    }

    /**
     * 
     * 
     * @param utenteId 
     * @return CarrelloDto
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ClientException If the API returns a client error response
     * @throws ServerException If the API returns a server error response
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class, UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun getCarrelloByUtenteId(utenteId: kotlin.Long) : CarrelloDto {
        val localVarResponse = getCarrelloByUtenteIdWithHttpInfo(utenteId = utenteId)

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as CarrelloDto
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()} ${localVarError.body}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
     * 
     * 
     * @param utenteId 
     * @return ApiResponse<CarrelloDto?>
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class)
    fun getCarrelloByUtenteIdWithHttpInfo(utenteId: kotlin.Long) : ApiResponse<CarrelloDto?> {
        val localVariableConfig = getCarrelloByUtenteIdRequestConfig(utenteId = utenteId)

        return request<Unit, CarrelloDto>(
            localVariableConfig
        )
    }

    /**
     * To obtain the request config of the operation getCarrelloByUtenteId
     *
     * @param utenteId 
     * @return RequestConfig
     */
    fun getCarrelloByUtenteIdRequestConfig(utenteId: kotlin.Long) : RequestConfig<Unit> {
        val localVariableBody = null
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()

        authViewModel.getToken()?.let { token ->
            localVariableHeaders["Authorization"] = "Bearer $token"
        }

        return RequestConfig(
            method = RequestMethod.GET,
            path = "/carrello-api/getByUtente/{utenteId}".replace("{"+"utenteId"+"}", encodeURIComponent(utenteId.toString())),
            query = localVariableQuery,
            headers = localVariableHeaders,
            requiresAuthentication = false,
            body = localVariableBody
        )
    }

    /**
     * 
     * 
     * @param id 
     * @return CarrelloProdottiDto
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ClientException If the API returns a client error response
     * @throws ServerException If the API returns a server error response
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class, UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun getCarrelloProdottiById(id: kotlin.Long) : CarrelloProdottiDto {
        val localVarResponse = getCarrelloProdottiByIdWithHttpInfo(id = id)

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as CarrelloProdottiDto
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()} ${localVarError.body}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
     * 
     * 
     * @param id 
     * @return ApiResponse<CarrelloProdottiDto?>
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class)
    fun getCarrelloProdottiByIdWithHttpInfo(id: kotlin.Long) : ApiResponse<CarrelloProdottiDto?> {
        val localVariableConfig = getCarrelloProdottiByIdRequestConfig(id = id)

        return request<Unit, CarrelloProdottiDto>(
            localVariableConfig
        )
    }

    /**
     * To obtain the request config of the operation getCarrelloProdottiById
     *
     * @param id 
     * @return RequestConfig
     */
    fun getCarrelloProdottiByIdRequestConfig(id: kotlin.Long) : RequestConfig<Unit> {
        val localVariableBody = null
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()

        authViewModel.getToken()?.let { token ->
            localVariableHeaders["Authorization"] = "Bearer $token"
        }
        
        return RequestConfig(
            method = RequestMethod.GET,
            path = "/carrello-api/prodotti/get/{id}".replace("{"+"id"+"}", encodeURIComponent(id.toString())),
            query = localVariableQuery,
            headers = localVariableHeaders,
            requiresAuthentication = false,
            body = localVariableBody
        )
    }

    /**
     * 
     * 
     * @param id 
     * @param carrelloDto 
     * @return CarrelloDto
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ClientException If the API returns a client error response
     * @throws ServerException If the API returns a server error response
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class, UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun updateCarrello(id: kotlin.Long, carrelloDto: CarrelloDto) : CarrelloDto {
        val localVarResponse = updateCarrelloWithHttpInfo(id = id, carrelloDto = carrelloDto)

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as CarrelloDto
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()} ${localVarError.body}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
     * 
     * 
     * @param id 
     * @param carrelloDto 
     * @return ApiResponse<CarrelloDto?>
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class)
    fun updateCarrelloWithHttpInfo(id: kotlin.Long, carrelloDto: CarrelloDto) : ApiResponse<CarrelloDto?> {
        val localVariableConfig = updateCarrelloRequestConfig(id = id, carrelloDto = carrelloDto)

        return request<CarrelloDto, CarrelloDto>(
            localVariableConfig
        )
    }

    /**
     * To obtain the request config of the operation updateCarrello
     *
     * @param id 
     * @param carrelloDto 
     * @return RequestConfig
     */
    fun updateCarrelloRequestConfig(id: kotlin.Long, carrelloDto: CarrelloDto) : RequestConfig<CarrelloDto> {
        val localVariableBody = carrelloDto
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        localVariableHeaders["Content-Type"] = "application/json"

        authViewModel.getToken()?.let { token ->
            localVariableHeaders["Authorization"] = "Bearer $token"
        }

        return RequestConfig(
            method = RequestMethod.PUT,
            path = "/carrello-api/update/{id}".replace("{"+"id"+"}", encodeURIComponent(id.toString())),
            query = localVariableQuery,
            headers = localVariableHeaders,
            requiresAuthentication = false,
            body = localVariableBody
        )
    }

    /**
     * 
     * 
     * @param id 
     * @param carrelloProdottiDto 
     * @return CarrelloProdottiDto
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ClientException If the API returns a client error response
     * @throws ServerException If the API returns a server error response
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class, UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun updateCarrelloProdotti(id: kotlin.Long, carrelloProdottiDto: CarrelloProdottiDto) : CarrelloProdottiDto {
        val localVarResponse = updateCarrelloProdottiWithHttpInfo(id = id, carrelloProdottiDto = carrelloProdottiDto)

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as CarrelloProdottiDto
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()} ${localVarError.body}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
     * 
     * 
     * @param id 
     * @param carrelloProdottiDto 
     * @return ApiResponse<CarrelloProdottiDto?>
     * @throws IllegalStateException If the request is not correctly configured
     * @throws IOException Rethrows the OkHttp execute method exception
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class)
    fun updateCarrelloProdottiWithHttpInfo(id: kotlin.Long, carrelloProdottiDto: CarrelloProdottiDto) : ApiResponse<CarrelloProdottiDto?> {
        val localVariableConfig = updateCarrelloProdottiRequestConfig(id = id, carrelloProdottiDto = carrelloProdottiDto)

        return request<CarrelloProdottiDto, CarrelloProdottiDto>(
            localVariableConfig
        )
    }

    /**
     * To obtain the request config of the operation updateCarrelloProdotti
     *
     * @param id 
     * @param carrelloProdottiDto 
     * @return RequestConfig
     */
    fun updateCarrelloProdottiRequestConfig(id: kotlin.Long, carrelloProdottiDto: CarrelloProdottiDto) : RequestConfig<CarrelloProdottiDto> {
        val localVariableBody = carrelloProdottiDto
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        localVariableHeaders["Content-Type"] = "application/json"

        authViewModel.getToken()?.let { token ->
            localVariableHeaders["Authorization"] = "Bearer $token"
        }
        
        return RequestConfig(
            method = RequestMethod.PUT,
            path = "/carrello-api/prodotti/update/{id}".replace("{"+"id"+"}", encodeURIComponent(id.toString())),
            query = localVariableQuery,
            headers = localVariableHeaders,
            requiresAuthentication = false,
            body = localVariableBody
        )
    }


    private fun encodeURIComponent(uriComponent: kotlin.String): kotlin.String =
        HttpUrl.Builder().scheme("http").host("localhost").addPathSegment(uriComponent).build().encodedPathSegments[0]
}
