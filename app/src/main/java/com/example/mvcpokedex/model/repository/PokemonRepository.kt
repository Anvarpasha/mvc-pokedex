package com.example.mvcpokedex.model.repository

import android.util.Log
import com.example.mvcpokedex.model.data.Pokemon
import com.example.mvcpokedex.model.data.PokemonDetail
import com.example.mvcpokedex.network.NetworkErrorMapper
import com.example.mvcpokedex.network.RetrofitClient

class PokemonRepository {

    private val TAG = "PokemonRepository"
    private val apiService = RetrofitClient.apiService

    suspend fun getPokemonList(offset : Int = 0) : Result<List<Pokemon>>{
        return try {
            Log.d(TAG, "Fetching pokemon list — offset: $offset")
            val response = apiService.getPokemonList(
                limit = 20,
                offset = offset
            )
            val pokemonList = response.results.map { it.toPokemon() }
            Log.d(TAG, "Successfully fetched ${pokemonList.size} pokemon")
            Result.success(pokemonList)
        } catch (e: Exception){
            val networkError = NetworkErrorMapper.map(e)
            Log.e(TAG, "Failed to fetch pokemon list — ${networkError.message}")
            Result.failure(networkError)
        }
    }


    suspend fun getPokemonDetail(name : String) : Result<PokemonDetail>{
        return try {
            Log.d(TAG, "Fetching pokemon detail — name: $name")
            val response = apiService.getPokemonDetail(name)
            Log.d(TAG, "Successfully fetched detail for ${response.name}")
            Result.success(response)
        }catch (e: Exception){
            val networkError = NetworkErrorMapper.map(e)
            Log.e(TAG, "Failed to fetch pokemon detail — ${networkError.message}")
            Result.failure(networkError)
        }
    }
}