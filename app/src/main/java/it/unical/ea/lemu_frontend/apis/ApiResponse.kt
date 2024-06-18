package it.unical.ea.lemu_frontend.apis

class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)