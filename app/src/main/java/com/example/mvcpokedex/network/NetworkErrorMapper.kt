package com.example.mvcpokedex.network

import android.util.Log
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object NetworkErrorMapper {

    private const val TAG = "NetworkError"

    fun map(throwable: Throwable): NetworkError {
        return when (throwable) {

            // HTTP errors — server responded but with error code
            is HttpException -> {
                val code = throwable.code()
                val errorBody = throwable.response()?.errorBody()?.string()
                Log.e(TAG, "HTTP Error $code — $errorBody")

                when (code) {
                    400 -> NetworkError.BadRequest()
                    401 -> NetworkError.Unauthorized()
                    403 -> NetworkError.Forbidden()
                    404 -> NetworkError.NotFound()
                    500 -> NetworkError.InternalServerError()
                    503 -> NetworkError.ServiceUnavailable()
                    else -> NetworkError.Unknown("HTTP error $code")
                }
            }

            // No internet — device has no network connection
            is UnknownHostException -> {
                Log.e(TAG, "No internet connection — ${throwable.message}")
                NetworkError.NoInternetConnection()
            }

            // Timeout — server took too long to respond
            is SocketTimeoutException -> {
                Log.e(TAG, "Request timed out — ${throwable.message}")
                NetworkError.TimeoutError()
            }

            // IO Exception — general network error
            is IOException -> {
                Log.e(TAG, "Network IO error — ${throwable.message}")
                NetworkError.NoInternetConnection()
            }

            // Anything else
            else -> {
                Log.e(TAG, "Unknown error — ${throwable.message}")
                NetworkError.Unknown()
            }
        }
    }
}