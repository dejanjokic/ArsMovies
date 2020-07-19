package net.dejanjokic.arsmovies.ui.list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.api.load
import net.dejanjokic.arsmovies.R
import net.dejanjokic.arsmovies.data.domain.DomainItem
import net.dejanjokic.arsmovies.databinding.ListItemMovieBinding
import net.dejanjokic.arsmovies.databinding.ListItemSectionHeaderBinding
import java.lang.Exception
import java.lang.UnsupportedOperationException

class MovieAdapter(
    private val detailsClickListener: (ImageView, DomainItem.Movie) -> Unit,
    private val favoriteClickListener: (DomainItem.Movie, Int) -> Unit
) : ListAdapter<DomainItem, ViewHolder>(DIFF_UTIL) {

    companion object {

        const val ITEM_TYPE_HEADER = 0
        const val ITEM_TYPE_MOVIE = 1

        val DIFF_UTIL = object : DiffUtil.ItemCallback<DomainItem>() {
            override fun areItemsTheSame(oldItem: DomainItem, newItem: DomainItem): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: DomainItem, newItem: DomainItem): Boolean =
                oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DomainItem.Header -> ITEM_TYPE_HEADER
            is DomainItem.Movie -> ITEM_TYPE_MOVIE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ITEM_TYPE_HEADER -> HeaderViewHolder(ListItemSectionHeaderBinding
                .inflate(LayoutInflater.from(parent.context), parent, false))
            ITEM_TYPE_MOVIE -> MovieViewHolder(ListItemMovieBinding
                .inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw UnsupportedOperationException("Unsupported viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(getItem(position) as DomainItem.Header)
            is MovieViewHolder -> holder.bind(getItem(position) as DomainItem.Movie)
        }
    }

    inner class MovieViewHolder(private val binding: ListItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: DomainItem.Movie) = with(binding) {
            textViewMovieItemTitle.text = movie.title

            buttonMovieItemToggleFavorite.apply {
                setImageResource(
                    if (movie.isFavorite)
                        R.drawable.ic_favorite
                    else
                        R.drawable.ic_favorite_border
                )
                setOnClickListener { favoriteClickListener(movie, layoutPosition) }
            }

            if (movie.poster.equals("N/A")) {
                imageViewMovieItemPoster.load(R.drawable.ic_movie)
            } else {
                imageViewMovieItemPoster.load(movie.poster)
            }
            imageViewMovieItemPoster.transitionName = movie.id

            root.setOnClickListener { detailsClickListener(imageViewMovieItemPoster, movie) }
        }
    }

    inner class HeaderViewHolder(private val binding: ListItemSectionHeaderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(header: DomainItem.Header) = with(binding) {
            textViewSectionHeaderItemYear.text = header.year.toString()
        }
    }
}