package com.example.proiectcm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.proiectcm.databinding.FragmentMovieDetailsBinding
import com.example.proiectcm.model.FavoriteMovie
import com.example.proiectcm.model.Movie
import com.example.proiectcm.viewModel.MovieViewModel

class MovieDetailsFragment : Fragment() {

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var movieViewModel: MovieViewModel
    private val args: MovieDetailsFragmentArgs by navArgs()

    private var isFavorite = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movieViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        val movieId = args.movieId
        movieViewModel.getMovieDetails(movieId)
        movieViewModel.getFavoriteMovies()

        movieViewModel.movieDetails.observe(viewLifecycleOwner) { movie ->
            updateUI(movie)
        }

        movieViewModel.favoriteMovies.observe(viewLifecycleOwner) { favoriteMovies ->
            isFavorite = favoriteMovies.any { it.id == movieId }
            updateFavoriteButton()
        }
    }

    private fun updateUI(movie: Movie) {
        binding.movieTitle.text = movie.title
        binding.movieReleaseDate.text = movie.releaseDate
        binding.movieOverview.text = movie.overview
        Glide.with(binding.root.context)
            .load("https://image.tmdb.org/t/p/w500" + movie.posterPath)
            .into(binding.moviePoster)

        binding.favoriteButton.setOnClickListener {
            if (isFavorite) {
                movieViewModel.removeFavoriteMovieById(movie.id)
            } else {
                movieViewModel.addFavoriteMovie(movie)
            }
        }
    }

    private fun updateFavoriteButton() {
        if (isFavorite) {
            binding.favoriteButton.text = "Remove from Favorites"
        } else {
            binding.favoriteButton.text = "Add to Favorites"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}