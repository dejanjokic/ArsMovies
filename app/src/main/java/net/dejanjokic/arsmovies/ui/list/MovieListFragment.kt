package net.dejanjokic.arsmovies.ui.list

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_movie_list.*
import net.dejanjokic.arsmovies.R
import net.dejanjokic.arsmovies.data.domain.DomainItem
import net.dejanjokic.arsmovies.databinding.FragmentMovieListBinding
import timber.log.Timber

@AndroidEntryPoint
class MovieListFragment : Fragment() {

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private val viewModel: MovieListViewModel by viewModels()

    private lateinit var movieAdapter: MovieAdapter

    private lateinit var searchView: SearchView
    private var savedQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeUiState()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        postponeEnterTransition()

        movieAdapter = MovieAdapter(
            { imageView, item -> showMovieDetails(imageView, item) },
            { item, position -> toggleFavoriteStatus(item, position) }
        )
        recyclerViewMovies.apply {
            adapter = movieAdapter
            layoutManager = LinearLayoutManager(requireContext())
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_movie_list, menu)
        val searchViewItem = menu.findItem(R.id.menuItemSearchView)
        searchView = searchViewItem.actionView as SearchView

        if (savedQuery.isNotEmpty()) {
            searchView.apply {
                searchViewItem.expandActionView()
                setQuery(savedQuery, false)
                clearFocus()
            }
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                if (query.length < 3) {
                    showStatusMessage("Query must be at least 3 characters")
                } else if (!query.matches(Regex("[a-zA-Z0-9 ]*"))) {
                    showStatusMessage("Invalid query")
                } else {
                    viewModel.getMovieSearchResults(query)
                    savedQuery = query
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        searchViewItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(menuItem: MenuItem?): Boolean {
                viewModel.getFavoriteMovies()
                savedQuery = ""
                return true
            }

            override fun onMenuItemActionExpand(menuItem: MenuItem?): Boolean {
                return true
            }
        })
    }

    private fun observeUiState() {
        viewModel.state.observe(viewLifecycleOwner, Observer {
            when (it) {
                is MovieListState.Success.Db -> showMovieList(it.items)
                is MovieListState.Success.Network -> showMovieList(it.items)
                is MovieListState.Error -> showStatusMessage(it.message)
                is MovieListState.Loading -> showLoading()
                is MovieListState.Empty -> showStatusMessage(getString(R.string.message_favorites_empty))
            }
        })
    }

    private fun showMovieList(items: List<DomainItem>) = with(binding) {
        Timber.d("Showing ${items.size} items.")
        progressBarMovieList.isVisible = false
        textViewMovieListStatus.isVisible = false
        recyclerViewMovies.isVisible = true
        movieAdapter.submitList(items)
    }

    private fun showLoading() = with(binding) {
        Timber.d("Loading...")
        recyclerViewMovies.isVisible = false
        textViewMovieListStatus.isVisible = false
        progressBarMovieList.isVisible = true
    }

    private fun showStatusMessage(message: String) = with(binding) {
        Timber.d(message)
        progressBarMovieList.isVisible = false
        recyclerViewMovies.isVisible = false
        textViewMovieListStatus.isVisible = true
        textViewMovieListStatus.text = message
    }

    private fun showMovieDetails(imageView: ImageView, item: DomainItem.Movie) {
        Timber.d("Show movie details for ${item.title}")
        val extras = FragmentNavigatorExtras(imageView to item.imdbId)
        val action = MovieListFragmentDirections.actionMovieListFragmentToMovieDetailsFragment(item.imdbId, item.poster)
        findNavController().navigate(action, extras)
    }

    private fun toggleFavoriteStatus(item: DomainItem.Movie, position: Int) {
        Timber.d("Toggling favorite status for ${item.title} from ${item.isFavorite} to ${!item.isFavorite}")

        // FIXME: Disgusting
        if (!item.isFavorite) {
            searchView.apply {
                setQuery("", false)
                isIconified = true
                onActionViewCollapsed()
            }
        }

        viewModel.toggleFavoriteStatus(item)
        item.isFavorite = !item.isFavorite
        movieAdapter.notifyItemChanged(position)
    }
}