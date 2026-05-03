package com.example.mvcpokedex.model.data

data class PokemonResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonResult>
)

data class PokemonResult(
    val name: String,
    val url: String
) {
    // Extract Pokemon ID from the URL
    // URL format: https://pokeapi.co/api/v2/pokemon/1/
    val id: Int
        get() = url.trimEnd('/').split("/").last().toInt()

    fun toPokemon() = Pokemon(
        id = id,
        name = name,
        url = url
    )
}