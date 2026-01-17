package com.example.proiectcm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proiectcm.databinding.MovieListItemBinding
import com.example.proiectcm.model.FavoriteMovie

class FavoriteMovieAdapter(private val onClick: (FavoriteMovie) -> Unit) :
    ListAdapter<FavoriteMovie, FavoriteMovieAdapter.FavoriteMovieViewHolder>(FavoriteMovieDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteMovieViewHolder {
        val binding = MovieListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteMovieViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: FavoriteMovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie)
    }

    class FavoriteMovieViewHolder(private val binding: MovieListItemBinding, private val onClick: (FavoriteMovie) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: FavoriteMovie) {
            binding.movieTitle.text = movie.title
            binding.movieReleaseDate.text = movie.releaseDate
            Glide.with(binding.root.context)
                .load("https://image.tmdb.org/t/p/w500" + movie.posterPath)
                .into(binding.moviePoster)
            binding.root.setOnClickListener {
                onClick(movie)
            }
        }
    }

    object FavoriteMovieDiffCallback : DiffUtil.ItemCallback<FavoriteMovie>() {
        override fun areItemsTheSame(oldItem: FavoriteMovie, newItem: FavoriteMovie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FavoriteMovie, newItem: FavoriteMovie): Boolean {
            return oldItem == newItem
        }
    }
}