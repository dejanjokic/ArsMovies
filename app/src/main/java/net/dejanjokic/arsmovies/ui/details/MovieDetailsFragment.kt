package net.dejanjokic.arsmovies.ui.details

import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import coil.api.load
import coil.decode.DataSource
import coil.request.Request
import dagger.hilt.android.AndroidEntryPoint
import net.dejanjokic.arsmovies.ui.MainActivity
import net.dejanjokic.arsmovies.R
import net.dejanjokic.arsmovies.data.domain.DomainDetails
import net.dejanjokic.arsmovies.databinding.FragmentMovieDetailsBinding
import net.dejanjokic.arsmovies.util.Constants.UI.IMDB_BASE_URL
import net.dejanjokic.arsmovies.util.ext.setHeightBasedOnChildren
import timber.log.Timber

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: MovieDetailsFragmentArgs by navArgs()

    private val viewModel: MovieDetailsViewModel by viewModels()

    private lateinit var detailsItem: DomainDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.shared_element_transition)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        loadPoster(args.id, args.posterUrl)

        binding.fabToggleFavorite.setOnClickListener { toggleFavorite() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getMovieDetails(args.id)
        observeUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_movie_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuItemShareMovie -> {
                shareMovie()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeUiState() {
        viewModel.state.observe(viewLifecycleOwner, Observer {
            when (it) {
                is MovieDetailsState.Loading -> showLoading()
                is MovieDetailsState.Success -> bindMovieDetails(it.item)
                is MovieDetailsState.Error -> showStatusMessage(it.message)
            }
        })
    }

    private fun loadPoster(id: String, posterUrl: String?) = with(binding) {
        imageViewMovieDetailsPoster.transitionName = id
        if (posterUrl.equals("N/A")) {
            imageViewMovieDetailsPoster.load(R.drawable.ic_movie) {
                this.listener(object : Request.Listener {
                    override fun onSuccess(request: Request, source: DataSource) {
                        super.onSuccess(request, source)
                        startPostponedEnterTransition()
                    }
                })
            }
        } else {
            imageViewMovieDetailsPoster.load(posterUrl) {
                this.listener(object : Request.Listener {
                    override fun onSuccess(request: Request, source: DataSource) {
                        super.onSuccess(request, source)
                        startPostponedEnterTransition()
                    }
                })
            }
        }
    }

    private fun bindMovieDetails(item: DomainDetails) = with(binding) {
        Timber.d("Binding details for ${item.title}")

        detailsItem = item

        textViewMovieDetailsStatus.isVisible = false
        progressBarMovieDetails.isVisible = false

        textViewMovieDetailsRuntime.text = item.runtime
        textViewMovieDetailsReleaseDate.text = item.releaseDate
        textViewMovieDetailsImdbScore.text = item.imdbRating
        textViewMovieDetailsPlot.text = item.plot

        val actorsList = item.actors?.split(", ")
        val actorsAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, actorsList!!)
        listViewDetailsActors?.let {
            it.adapter = actorsAdapter
            it.setHeightBasedOnChildren()
        }

        val genresList = item.genres?.split(", ")
        val genresAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, genresList!!)
        listViewDetailsGenres?.let {
            it.adapter = genresAdapter
            it.setHeightBasedOnChildren()
        }

        updateFab()

        (requireActivity() as MainActivity).supportActionBar?.title = item.title
    }

    private fun updateFab() = with(binding) {
        fabToggleFavorite.setImageResource(
            if (detailsItem.isFavorite)
                R.drawable.ic_favorite
            else
                R.drawable.ic_favorite_border
        )
    }

    private fun showStatusMessage(message: String) = with(binding) {
        Timber.d(message)
        imageViewMovieDetailsPoster.isVisible = false
        progressBarMovieDetails.isVisible = false

        textViewMovieDetailsStatus.apply {
            isVisible = true
            text = message
        }
    }

    private fun showLoading() = with(binding) {
        Timber.d("Loading...")
        textViewMovieDetailsStatus.isVisible = false
        progressBarMovieDetails.isVisible = true
    }

    private fun shareMovie() {
        val imdbShareUrl = "$IMDB_BASE_URL${args.id}"
        Timber.d("Sharing $imdbShareUrl")
        val intent = Intent().apply {
            action = ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, imdbShareUrl)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.action_share_movie)))
    }

    private fun toggleFavorite() {
        Timber.d("Toggling favorite status for ${detailsItem.title} from ${detailsItem.isFavorite} to ${!detailsItem.isFavorite}")
        viewModel.toggleFavoriteStatus(detailsItem)
        detailsItem.isFavorite = !detailsItem.isFavorite
        updateFab()
    }
}