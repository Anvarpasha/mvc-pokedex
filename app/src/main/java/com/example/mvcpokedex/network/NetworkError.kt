package com.example.mvcpokedex.network

sealed class NetworkError : Exception() {

    // 400 — bad request (wrong parameters sent)
    data class BadRequest(
        override val message: String = "Bad request. Please check your input."
    ) : NetworkError()

    // 401 — unauthorized (invalid or missing token)
    data class Unauthorized(
        override val message: String = "Unauthorized. Please login again."
    ) : NetworkError()

    // 403 — forbidden (no permission)
    data class Forbidden(
        override val message: String = "Access forbidden."
    ) : NetworkError()

    // 404 — not found
    data class NotFound(
        override val message: String = "Pokemon not found."
    ) : NetworkError()

    // 500 — internal server error
    data class InternalServerError(
        override val message: String = "Server error. Please try again later."
    ) : NetworkError()

    // 503 — service unavailable
    data class ServiceUnavailable(
        override val message: String = "Service unavailable. Please try again later."
    ) : NetworkError()

    // No internet connection
    data class NoInternetConnection(
        override val message: String = "No internet connection. Please check your network."
    ) : NetworkError()

    // Request timed out
    data class TimeoutError(
        override val message: String = "Request timed out. Please try again."
    ) : NetworkError()

    // Unknown error
    data class Unknown(
        override val message: String = "An unexpected error occurred."
    ) : NetworkError()
}