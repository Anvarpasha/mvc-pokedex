package com.example.mvcpokedex.controller

import com.example.mvcpokedex.model.data.Pokemon
import com.example.mvcpokedex.model.data.PokemonDetail
import com.example.mvcpokedex.model.repository.PokemonRepository
import com.example.mvcpokedex.network.NetworkError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher

class PokemonController(
    private val repository: PokemonRepository,
    private val scope: CoroutineScope
) {
    // with this callback View knows what happened
    interface PokemonListCallback {
        fun onLoading()
        fun onSuccess(pokemonList : List<Pokemon>)
        fun onError(error : NetworkError)
    }

    interface PokemonDetailCallback {
        fun onLoading()
        fun onSuccess(pokemonList : PokemonDetail)
        fun onError(error : NetworkError)
    }

    private var currentOffset = 0
    private var isLoading = false
    private val pokemonList = mutableListOf<Pokemon>()

    fun loadPokemonList(callback : PokemonListCallback) {
        if (isLoading) return

        isLoading = true
        callback.onLoading()

        scope.launch(Dispatchers.IO) {
            val result = repository.getPokemonList(offset = currentOffset)

            withContext(Dispatchers.Main){
                isLoading = false

                result.fold(
                    onSuccess = { newPokemon ->
                        pokemonList.addAll(newPokemon)
                        currentOffset +=newPokemon.size
                        callback.onSuccess(pokemonList.toList())
                    },
                    onFailure = { error ->
                        callback.onError(error as NetworkError)
                    }
                )
            }
        }
    }

    fun loadMorePokemon(callback: PokemonListCallback){
        loadPokemonList(callback)
    }

    fun loadPokemonDetail(name: String, callback : PokemonDetailCallback) {
        callback.onLoading()

        scope.launch(Dispatchers.IO) {
            val result = repository.getPokemonDetail(name)

            withContext(Dispatchers.Main) {
                result.fold(
                    onSuccess = { pokemon ->
                        callback.onSuccess(pokemon)
                    },
                    onFailure = { error ->
                        callback.onError(error as NetworkError)
                    }
                )
            }
        }
    }

    fun resetList() {
        currentOffset = 0
        pokemonList.clear()
    }
}