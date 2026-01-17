package com.example.proiectcm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proiectcm.adapters.FavoriteMovieAdapter
import com.example.proiectcm.databinding.FragmentFavoritesBinding
import com.example.proiectcm.viewModel.MovieViewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var movieViewModel: MovieViewModel
    private lateinit var favoriteMovieAdapter: FavoriteMovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movieViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        favoriteMovieAdapter = FavoriteMovieAdapter { favoriteMovie ->
            val action = FavoritesFragmentDirections.actionFavoritesFragmentToMovieDetailsFragment(favoriteMovie.id)
            findNavController().navigate(action)
        }

        binding.favoritesRecyclerView.apply {
            adapter = favoriteMovieAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        movieViewModel.favoriteMovies.observe(viewLifecycleOwner) { movies ->
            favoriteMovieAdapter.submitList(movies)
        }

        movieViewModel.getFavoriteMovies()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}