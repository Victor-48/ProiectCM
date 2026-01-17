package com.example.proiectcm.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.proiectcm.api.ApiClient
import com.example.proiectcm.database.AppDatabase
import com.example.proiectcm.model.FavoriteMovie
import com.example.proiectcm.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> = _movies

    private val _movieDetails = MutableLiveData<Movie>()
    val movieDetails: LiveData<Movie> = _movieDetails

    private val favoriteMovieDao by lazy { AppDatabase.getDatabase(application).favoriteMovieDao() }
    val favoriteMovies: LiveData<List<FavoriteMovie>> = favoriteMovieDao.getAllFavoriteMovies()

    private var currentPage = 1
    private var currentQuery = ""
    private var isLoading = false

    private var isGenreSearch = false
    private var currentGenreId = ""

    private val genreMap = mapOf(
        "action" to "28", "adventure" to "12", "animation" to "16", "comedy" to "35",
        "crime" to "80", "documentary" to "99", "drama" to "18", "family" to "10751",
        "fantasy" to "14", "history" to "36", "horror" to "27", "music" to "10402",
        "mystery" to "9648", "romance" to "10749", "science fiction" to "878",
        "scifi" to "878", "tv movie" to "10770", "thriller" to "53", "war" to "10752",
        "western" to "37", "animal" to "16", "funny" to "35"
    )

    init {
        Log.d("MovieViewModel", "init: ViewModel created")
    }

    fun getPopularMovies() {
        if (isLoading) return

        if (currentQuery.isNotEmpty() || isGenreSearch) {
            currentPage = 1
            currentQuery = ""
            isGenreSearch = false
            _movies.value = emptyList()
        }

        isLoading = true
        Log.d("MovieViewModel", "getPopularMovies: Fetching page $currentPage")
        viewModelScope.launch {
            try {
                // Pass currentPage
                val response = ApiClient.apiService.getPopularMovies(currentPage)
                if (response.isSuccessful) {
                    val currentList = if (currentPage == 1) emptyList() else _movies.value ?: emptyList()
                    val newMovies = response.body()?.movies ?: emptyList()
                    _movies.postValue(currentList + newMovies)
                    currentPage++
                    Log.d("MovieViewModel", "getPopularMovies: Success - movies loaded")
                } else {
                    Log.e("MovieViewModel", "getPopularMovies: Error - ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "getPopularMovies: Exception - ${e.message}")
            }
            isLoading = false
        }
    }

    fun searchMovies(apiKey: String, query: String) {
        val lowerQuery = query.lowercase().trim()

        if (query != currentQuery) {
            currentPage = 1
            _movies.value = emptyList()
            currentQuery = query

            if (genreMap.containsKey(lowerQuery)) {
                isGenreSearch = true
                currentGenreId = genreMap[lowerQuery]!!
                Log.d("MovieViewModel", "searchMovies: Detected Genre Search -> $lowerQuery ($currentGenreId)")
            } else {
                isGenreSearch = false
            }
        }

        if (isLoading) return
        isLoading = true

        Log.d("MovieViewModel", "searchMovies: Searching for '$query' (Genre: $isGenreSearch), page $currentPage")
        viewModelScope.launch {
            try {
                val response = if (isGenreSearch) {
                    ApiClient.apiService.getMoviesByGenre(currentGenreId, currentPage)
                } else {
                    ApiClient.apiService.searchMovies(query, currentPage)
                }

                if (response.isSuccessful) {
                    val currentList = if (currentPage == 1) emptyList() else _movies.value ?: emptyList()
                    val newMovies = response.body()?.movies ?: emptyList()
                    _movies.postValue(currentList + newMovies)
                    currentPage++
                    Log.d("MovieViewModel", "searchMovies: Success - movies found")
                } else {
                    Log.e("MovieViewModel", "searchMovies: Error - ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "searchMovies: Exception - ${e.message}")
            }
            isLoading = false
        }
    }

    fun getMovieDetails( movieId: Int) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getMovieDetails(movieId)
                if (response.isSuccessful) {
                    _movieDetails.postValue(response.body())
                } else {
                    Log.e("MovieViewModel", "getMovieDetails: Error - ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "getMovieDetails: Exception - ${e.message}")
            }
        }
    }

    fun getFavoriteMovies() {
    }

    fun addFavoriteMovie(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            val favoriteMovie = FavoriteMovie(
                id = movie.id,
                title = movie.title,
                overview = movie.overview,
                posterPath = movie.posterPath,
                releaseDate = movie.releaseDate,
                voteAverage = movie.voteAverage
            )
            favoriteMovieDao.insertFavoriteMovie(favoriteMovie)
        }
    }

    fun removeFavoriteMovieById(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            favoriteMovieDao.deleteFavoriteMovie(movieId)
        }
    }
}