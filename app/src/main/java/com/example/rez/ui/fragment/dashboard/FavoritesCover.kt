package com.example.rez.ui.fragment.dashboard

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rez.RezApp
import com.example.rez.adapter.FavoritesCoverAdapter
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentFavoritesCoverBinding
import com.example.rez.model.dashboard.Favorite
import com.example.rez.ui.RezViewModel
import com.example.rez.util.handleApiError
import com.example.rez.util.showToast
import com.example.rez.util.visible
import javax.inject.Inject


class FavoritesCover : Fragment(), FavoritesCoverAdapter.OnClickFavoritesCoverItemClickListener {

    private var _binding : FragmentFavoritesCoverBinding? = null
    private val binding get() = _binding!!
    private val rezViewModel: RezViewModel by activityViewModels()
    private lateinit var tableList: List<Favorite>
    private lateinit var tableAdapter: FavoritesCoverAdapter

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as RezApp).localComponent?.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFavoritesCoverBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpFavorites()
//        val fm: FragmentManager? = fragmentManager
//        for (i in 0 until fm!!.backStackEntryCount) {
//            fm.popBackStack()
//        }
    }

    private fun setUpFavorites(){
        rezViewModel.getFavorites( "Bearer ${sharedPreferences.getString("token", "token")}")
        rezViewModel.getFavoritesCover.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)
            when(it) {
                is Resource.Success -> {
                    if (it.value.status){
                        tableList = it.value.data.favourites
                        tableAdapter = FavoritesCoverAdapter(tableList, this)
                        binding.favoriteCoverRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
                        binding.favoriteCoverRecycler.adapter = tableAdapter
                        if (tableList.isEmpty()){
                            binding.favoriteCoverRecycler.visibility = View.GONE
                            binding.emptyText.visibility = View.VISIBLE
                        }
                    } else {
                        it.value.message.let { it1 ->
                            Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
                    }
                }
                is Resource.Error<*> -> {
                    showToast(it.data.toString())
                    rezViewModel.getFavoritesCover.removeObservers(viewLifecycleOwner)                }
            }
        })
    }

    override fun onEachFavoriteItemClickListener(favorite: Favorite) {
        val action = FavoritesCoverDirections.actionFavoritesCoverToFavorites(favorite)
        findNavController().navigate(action)
    }


}