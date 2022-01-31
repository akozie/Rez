package com.example.rez.ui.fragment.dashboard

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.adapter.*
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentHomeBinding
import com.example.rez.databinding.NearRestaurantAdapterBinding
import com.example.rez.model.dashboard.*
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.activity.DashboardActivity
import com.example.rez.util.handleApiError
import com.example.rez.util.showToast
import kotlinx.coroutines.launch
import javax.inject.Inject


class Home : Fragment(), OnItemClickListener, OnSuggestionClickListener, OnTopItemClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var topRecommendedAdapter: TopRecommendedAdapter
    private lateinit var nearRestaurantAdapter: NearRestaurantAdapter
    private lateinit var suggestionRestaurantAdapter: SuggestionRestaurantAdapter
    private lateinit var bannerRecyclerView: RecyclerView
    private lateinit var topRecyclerView: RecyclerView
    private lateinit var nearRecyclerView: RecyclerView
    private lateinit var suggestionRecyclerView: RecyclerView
    private lateinit var bannerList:ArrayList<BannerData>
    private lateinit var topList:ArrayList<TopRecommendedData>
    private lateinit var nearList:ArrayList<SuggestionAndNearData>
    private lateinit var likedList:ArrayList<SuggestionAndNearData>
//    private lateinit var nearList:ArrayList<NearRestaurantData>
    private lateinit var suggestionList:ArrayList<SuggestionRestaurantData>
    private val rezViewModel: RezViewModel by activityViewModels()

    private var _likeUnlikeBinding : NearRestaurantAdapterBinding? = null
    private val likeUnlikeBinding get() = _likeUnlikeBinding!!

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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
//        _likeUnlikeBinding = NearRestaurantAdapterBinding.inflate(inflater, container, false)
       // rezViewModel = (activity as DashboardActivity).rezViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchData()
        onInitView()
       // registerObservers()

        binding.seeAllTopRecommTv.setOnClickListener {
            val action = HomeDirections.actionHome2ToTopRecommended()
            findNavController().navigate(action)
        }

        binding.seeAllNearRestTv.setOnClickListener {
            val action = HomeDirections.actionHome2ToNearRestaurant()
            findNavController().navigate(action)
        }

        binding.seeAllSuggestionTv.setOnClickListener {
            val action = HomeDirections.actionHome2ToSuggestionForYou()
            findNavController().navigate(action)
        }

    }

    private fun banner() {
        bannerRecyclerView = binding.bannerRecycler
        bannerAdapter = BannerAdapter(bannerList)
        bannerRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        bannerRecyclerView.adapter = bannerAdapter
    }

    private fun topRestaurants() {
        topRecyclerView = binding.topRecomendRecycler
        topRecommendedAdapter = TopRecommendedAdapter(topList, this)
        topRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        topRecyclerView.adapter = topRecommendedAdapter
    }

    private fun nearRestaurants() {
        nearRecyclerView = binding.nearRestRecycler
        nearRestaurantAdapter = NearRestaurantAdapter(nearList, this)
        nearRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        nearRecyclerView.adapter = nearRestaurantAdapter
    }

    private fun suggestionRestaurants() {
        suggestionRecyclerView = binding.suggestionRecycler
        suggestionRestaurantAdapter = SuggestionRestaurantAdapter(suggestionList, this)
        suggestionRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        suggestionRecyclerView.adapter = suggestionRestaurantAdapter
    }

    private fun topList() {
        topList = arrayListOf(
            TopRecommendedData("1", "Hustle and Bustle", R.drawable.restaurant_img,
                "4", "3", "Cafe", "10", "", ""),
            TopRecommendedData("1", "Hustle and Bustle", R.drawable.restaurant_img,
                "4", "3", "Cafe", "10", "", ""),
            TopRecommendedData("1", "Hustle and Bustle", R.drawable.restaurant_img,
                "4", "3", "Cafe", "10", "", "")
        )
    }

    private fun bannerList() {
        bannerList = arrayListOf(
            BannerData("Book Table From Nearby Restaurant", "enjoy table booking by your mobile from anywhere", R.drawable.banner_4),
            BannerData("Book Table From Nearby Restaurant", "enjoy table booking by your mobile from anywhere", R.drawable.banner_2),
            BannerData("Book Table From Nearby Restaurant", "enjoy table booking by your mobile from anywhere", R.drawable.banner_3)
        )
    }

    private fun nearList() {
        nearList = arrayListOf(
            SuggestionAndNearData("Hustle and bustle", R.drawable.hustle,
                "4.0", "Cafe", "5 Tables", "7771.32km", "80, Amino Kano Crescent, Wuse 2, Abuja", "1", ""),
            SuggestionAndNearData("Hustle and bustle", R.drawable.hustle,
                "4.0", "Cafe", "5 Tables", "7771.32km", "80, Amino Kano Crescent, Wuse 2, Abuja", "2", ""),
            SuggestionAndNearData("Hustle and bustle", R.drawable.hustle,
                "4.0", "Cafe", "5 Tables", "7771.32km", "80, Amino Kano Crescent, Wuse 2, Abuja", "3", ""),

        )
    }

    private fun suggestionList() {
        suggestionList = arrayListOf(
            SuggestionRestaurantData("Tarragon", R.drawable.restaurant,
                "3.5", "Restaurant", "21 Tables", "777.13km", "1c Ozumba Mbadiwe Ave, Victoria Island 106104, Lagos", "4", ""),
           SuggestionRestaurantData("Tarragon", R.drawable.restaurant,
                "3.5", "Restaurant", "21 Tables", "777.13km", "1c Ozumba Mbadiwe Ave, Victoria Island 106104, Lagos", "5", ""),
           SuggestionRestaurantData("Tarragon", R.drawable.restaurant,
                "3.5", "Restaurant", "21 Tables", "777.13km", "1c Ozumba Mbadiwe Ave, Victoria Island 106104, Lagos", "6", ""),
        )
    }

    override fun onItemClick(nearModel: SuggestionAndNearData) {
        val action = HomeDirections.actionHome2ToNearRestFragment(nearModel)
        findNavController().navigate(action)
    }

    override fun likeUnlike(id: String, like: ImageView, unLike: ImageView) {
        registerObservers(like, unLike)
        rezViewModel.addOrRemoveFavorites(id, "Bearer ${sharedPreferences.getString("token", "token")}")
    }

    override fun onSuggestionItemClick(suggestionModel: SuggestionRestaurantData) {
        val action = HomeDirections.actionHome2ToSuggestFragment( suggestionModel)
        findNavController().navigate(action)
    }

    override fun onTopItemClick(topModel: TopRecommendedData) {
        val action = HomeDirections.actionHome2ToTableList(topModel)
        findNavController().navigate(action)
    }

    private fun registerObservers(like: ImageView, unLike: ImageView) {
        rezViewModel.addOrRemoveFavoritesResponse.observe(viewLifecycleOwner, {
            when(it) {
                is Resource.Success -> {
                    if (unLike.isVisible) {
                        showToast("Added Successfully")
                        like.visibility = View.VISIBLE
                        unLike.visibility = View.INVISIBLE
                        removeObserver()
                    } else if(!unLike.isVisible){
//                        it.value.message?.let { it1 -> showToast(it1)
                        showToast("Removed Successfully")
                        like.visibility = View.INVISIBLE
                        unLike.visibility = View.VISIBLE
                        removeObserver()
                    }
                }
                is Resource.Failure -> handleApiError(it)
            }
        })
    }

    private fun fetchData() {
        bannerList()
        topList()
        nearList()
        suggestionList()
    }

    private fun onInitView() {
        banner()
        topRestaurants()
        nearRestaurants()
        suggestionRestaurants()
    }

    private fun removeObserver() {
        rezViewModel.addOrRemoveFavoritesResponse.removeObservers(viewLifecycleOwner)

    }
}