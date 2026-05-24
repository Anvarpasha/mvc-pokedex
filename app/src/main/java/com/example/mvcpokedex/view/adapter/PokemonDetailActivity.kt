package com.example.mvcpokedex.view.adapter

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.mvcpokedex.R
import com.example.mvcpokedex.controller.PokemonController
import com.example.mvcpokedex.databinding.ActivityMainBinding
import com.example.mvcpokedex.databinding.ActivityPokemonDetailBinding
import com.example.mvcpokedex.model.data.PokemonDetail
import com.example.mvcpokedex.model.repository.PokemonRepository
import com.example.mvcpokedex.network.NetworkError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.Dispatcher

class PokemonDetailActivity : AppCompatActivity(), PokemonController.PokemonDetailCallback {

    private lateinit var binding: ActivityPokemonDetailBinding
    private lateinit var controller: PokemonController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPokemonDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from Intent
        val pokemonName = intent.getStringExtra("pokemon_name") ?: ""
        val pokemonId = intent.getIntExtra("pokemon_id", 0)


        // Show image immediately using ID — no need to wait for API
        val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$pokemonId.png"
        binding.ivPokemon.load(imageUrl) {
            crossfade(true)
        }

        // Show back button
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = pokemonName.replaceFirstChar { it.uppercase() }
        }

        setupController()
        controller.loadPokemonDetail(pokemonName, this)
    }

    private fun setupController() {
        controller = PokemonController(
            repository = PokemonRepository(),
            scope = CoroutineScope(Dispatchers.Main)
        )
    }

    // PokemonDetailCallback implementation
    override fun onLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvError.visibility = View.GONE
    }

    override fun onSuccess(pokemon: PokemonDetail) {
        binding.progressBar.visibility = View.GONE
        binding.tvError.visibility = View.GONE

        // Image
        binding.ivPokemon.load(
            pokemon.sprites.other.officialArtwork.frontDefault
        ) {
            crossfade(true)
        }

        // Name and ID
        binding.tvName.text = pokemon.name.replaceFirstChar { it.uppercase() }
        binding.tvId.text = "#${String.format("%03d", pokemon.id)}"

        // Height and Weight
        // PokeAPI returns height in decimetres and weight in hectograms
        val heightInCm = pokemon.height * 10
        val weightInKg = pokemon.weight / 10.0
        binding.tvHeight.text = "Height: ${heightInCm}cm"
        binding.tvWeight.text = "Weight: ${weightInKg}kg"

        // Types
        val types = pokemon.types.joinToString(" | ") { typeSlot ->
            typeSlot.type.name.replaceFirstChar { it.uppercase() }
        }
        binding.tvTypes.text = types

        // Stats
        val stats = pokemon.stats.joinToString("\n") { statSlot ->
            val statName = statSlot.stat.name
                .replace("-", " ")
                .replaceFirstChar { it.uppercase() }
            "$statName: ${statSlot.baseStat}"
        }
        binding.tvStats.text = stats
    }

    override fun onError(error: NetworkError) {
        binding.progressBar.visibility = View.GONE
        when (error) {
            is NetworkError.NotFound -> {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = error.message
            }
            is NetworkError.NoInternetConnection -> {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = error.message
            }
            else -> {
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}