package com.example.mvcpokedex.network

import com.example.mvcpokedex.model.data.PokemonDetail
import com.example.mvcpokedex.model.data.PokemonResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset : Int = 0
    ) : PokemonResponse

    @GET("pokemon/{name}")
    suspend fun getPokemonDetail(
        @Path("name") name: String
    ) : PokemonDetail
}