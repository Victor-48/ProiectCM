package com.example.proiectcm.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proiectcm.BuildConfig
import com.example.proiectcm.R
import com.example.proiectcm.adapters.MovieAdapter
import com.example.proiectcm.databinding.FragmentPopularMoviesBinding
import com.example.proiectcm.viewModel.MovieViewModel

class PopularMoviesFragment : Fragment() {
    private var _binding: FragmentPopularMoviesBinding? = null
    private val binding get() = _binding!!
    private lateinit var movieViewModel: MovieViewModel
    private lateinit var movieAdapter: MovieAdapter
    private val apiKey = BuildConfig.API_KEY

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPopularMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movieViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)
        setupRecyclerView()
        setupSearchView()

        movieViewModel.movies.observe(viewLifecycleOwner) {
            movieAdapter.submitList(it)
        }

        if (movieViewModel.movies.value.isNullOrEmpty()) {
            movieViewModel.getPopularMovies()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.searchView.query.isNotEmpty()) {
                    binding.searchView.setQuery("", false)
                    binding.searchView.clearFocus()
                } else {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter { movie ->
            val action = PopularMoviesFragmentDirections.actionPopularMoviesFragmentToMovieDetailsFragment(movie.id)
            findNavController().navigate(action)
        }
        binding.moviesRecyclerView.apply {
            adapter = movieAdapter
            val linearLayoutManager = LinearLayoutManager(requireContext())
            layoutManager = linearLayoutManager
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val totalItemCount = linearLayoutManager.itemCount
                    val visibleItemCount = linearLayoutManager.childCount
                    val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        val currentQuery = binding.searchView.query.toString()
                        if (currentQuery.isNotEmpty()) {
                            movieViewModel.searchMovies(apiKey, currentQuery)
                        } else {
                            movieViewModel.getPopularMovies()
                        }
                    }
                }
            })
        }
    }

    private fun setupSearchView() {
        binding.searchView.setIconifiedByDefault(false)
        binding.searchView.clearFocus()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    movieViewModel.searchMovies(apiKey, query)
                    binding.searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty() && newText.length > 2) {
                    movieViewModel.searchMovies(apiKey, newText)
                } else if (newText.isNullOrEmpty()) {
                    movieViewModel.getPopularMovies()
                }
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}