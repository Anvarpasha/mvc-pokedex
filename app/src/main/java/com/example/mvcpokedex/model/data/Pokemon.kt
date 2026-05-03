package com.example.mvcpokedex.model.data

data class Pokemon(
    val id: Int,
    val name: String,
    val url: String
) {
    val imageUrl: String
        get() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
}