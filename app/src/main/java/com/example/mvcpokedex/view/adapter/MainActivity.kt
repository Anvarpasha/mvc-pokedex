package com.example.mvcpokedex.view.adapter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvcpokedex.R
import com.example.mvcpokedex.controller.PokemonController
import com.example.mvcpokedex.databinding.ActivityMainBinding
import com.example.mvcpokedex.model.data.Pokemon
import com.example.mvcpokedex.model.repository.PokemonRepository
import com.example.mvcpokedex.network.NetworkError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.jvm.java

class MainActivity : AppCompatActivity(), PokemonController.PokemonListCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var controller: PokemonController
    private lateinit var adapter: PokemonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupController()
        setupRecyclerView()
        loadPokemon()
    }

    private fun setupController() {
        controller = PokemonController(
            repository = PokemonRepository(),
            scope = CoroutineScope(Dispatchers.Main)
        )
    }

    private fun setupRecyclerView() {
        adapter = PokemonAdapter { pokemon ->
            // Navigate to detail screen
            val intent = Intent(this, PokemonDetailActivity::class.java)
            intent.putExtra("pokemon_name", pokemon.name)
            intent.putExtra("pokemon_id", pokemon.id)
            startActivity(intent)
        }

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = this@MainActivity.adapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                    // Load more when user is near the end of the list
                    if (!binding.progressBar.isVisible() &&
                        (visibleItemCount + firstVisibleItem) >= totalItemCount - 4
                    ) {
                        controller.loadMorePokemon(this@MainActivity)
                    }
                }
            })
        }
    }

    private fun loadPokemon() {
        controller.loadPokemonList(this)
    }


    // PokemonListCallback impl
    override fun onLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvError.visibility = View.GONE
    }


    override fun onSuccess(pokemonList: List<Pokemon>) {
        binding.progressBar.visibility = View.GONE
        binding.tvError.visibility = View.GONE
        Log.d("MainActivity", "onSuccess called with ${pokemonList.size} pokemon")

        adapter.submitList(pokemonList)
    }

    override fun onError(error: NetworkError) {
        binding.progressBar.visibility = View.GONE
        when (error) {
            is NetworkError.NoInternetConnection -> {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = error.message
            }
            is NetworkError.NotFound -> {
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }
            is NetworkError.InternalServerError -> {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = error.message
            }
            else -> {
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        controller.resetList()
    }
}

fun View.isVisible(): Boolean = visibility == View.VISIBLE
