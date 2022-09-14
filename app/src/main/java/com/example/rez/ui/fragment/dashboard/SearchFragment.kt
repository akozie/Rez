package com.example.rez.ui.fragment.dashboard

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.adapter.OnTableClickListener
import com.example.rez.adapter.TableAdapter
import com.example.rez.adapter.TableDetailsViewPagerAdapter
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentSearch2Binding
import com.example.rez.databinding.FragmentSuggestBinding
import com.example.rez.model.authentication.response.ResultX
import com.example.rez.model.dashboard.Image
import com.example.rez.model.dashboard.SuggestedVendor
import com.example.rez.model.dashboard.Table
import com.example.rez.ui.RezViewModel
import com.example.rez.util.handleApiError
import com.example.rez.util.showToast
import com.example.rez.util.visible
import com.google.gson.Gson
import com.viewpagerindicator.CirclePageIndicator
import javax.inject.Inject


class SearchFragment : Fragment(), OnTableClickListener {
    private var _binding: FragmentSearch2Binding? = null
    private val binding get() = _binding!!
    private val rezViewModel: RezViewModel by activityViewModels()
    private lateinit var tableAdapter: TableAdapter
    private lateinit var tableList: List<Table>
    private var args: ResultX? = null
    private lateinit var sliderDot: CirclePageIndicator
    private var tableDetailsPagerAdapter: TableDetailsViewPagerAdapter? = null
    private var tableDetailsViewPager: ViewPager? = null
    private lateinit var tableDetailsDataList: List<Image>
    private var current = 0

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
        _binding = FragmentSearch2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args = arguments?.getParcelable("RESULT")
        getOpeningHours()
        setList()
        getTable()
        searchData()

        sharedPreferences.edit().putInt("vendorid", args!!.id).apply()

        binding.likeIv.setOnClickListener {
            registerObservers()
            rezViewModel.addOrRemoveFavorites(args?.id.toString(), "Bearer ${sharedPreferences.getString("token", "token")}")
        }

        binding.unLikeIv.setOnClickListener {
            registerObservers()
            rezViewModel.addOrRemoveFavorites(args?.id.toString(), "Bearer ${sharedPreferences.getString("token", "token")}")
        }

        // setRecyclerview()
        tableDetailsViewPager = binding.viewPager
        sliderDot = binding.indicator

        //Tab indicator listener
        sliderDot.setOnPageChangeListener(
            object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    current = position
                }

                override fun onPageSelected(position: Int) {
                }

                override fun onPageScrollStateChanged(state: Int) {
                }

            })
    }
    private fun setTableDetailsViewPagerAdapter() {
        tableDetailsPagerAdapter = TableDetailsViewPagerAdapter(requireContext(), tableDetailsDataList)
        tableDetailsViewPager?.adapter = tableDetailsPagerAdapter
        tableDetailsViewPager?.setCurrentItem(current++, true)
        sliderDot.setViewPager(tableDetailsViewPager)
        val density = resources.displayMetrics.density
        sliderDot.radius = 5 * density
    }


    private fun searchData() {
        binding.hotelNameTv.text = args?.company_name
        binding.hotelNameTv.text = args?.company_name
        binding.ratingBar.rating = args?.ratings!!.toFloat()


    }


    private fun getTable() {
        rezViewModel.getTable("Bearer ${sharedPreferences.getString("token", "token")}", args!!.id)
        rezViewModel.getTablesResponse.observe(
            viewLifecycleOwner, Observer {
                binding.progressBar.visible(it is Resource.Loading)
                when(it) {
                    is Resource.Success -> {
                        if (it.value.status){
                            tableList = it.value.data.tables
                            if (tableList.isEmpty()){
                                binding.tableListRecycler.visibility = View.GONE
                                binding.empty.visibility = View.VISIBLE
                            }
                            setTableList()
                            val gson = Gson()
                            val db = gson.toJson(tableList)
                            sharedPreferences.edit().putString("tablelist", db).apply()

                        } else {
                            it.value.message.let { it1 ->
                                Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
                        }
                    }
                    is Resource.Failure -> handleApiError(it) { getTable() }
                }
            }
        )
    }

    private fun setList() {
        rezViewModel.getVendorTables(args!!.id, "Bearer ${sharedPreferences.getString("token", "token")}")
        rezViewModel.getVendorTableResponse.observe(
            viewLifecycleOwner, Observer {
                binding.progressBar.visible(it is Resource.Loading)
                when(it) {
                    is Resource.Success -> {
                        if (it.value.status){
                            tableDetailsDataList = it.value.data.images
                            if (tableDetailsDataList.isEmpty()){
                                tableDetailsDataList = listOf(Image("", 1, "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80"))
                            }
                            //Log.d("TABLEDATALIST", tableDetailsDataList.toString())

                            setTableDetailsViewPagerAdapter()

                        } else {
                            it.value.message?.let { it1 ->
                                Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
                        }
                    }
                    is Resource.Failure -> handleApiError(it) { setList() }
                }
            }
        )
    }

    private fun setTableList() {
        tableAdapter = TableAdapter(tableList, this)
        binding.tableListRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.tableListRecycler.adapter = tableAdapter

    }

    private fun getOpeningHours() {
        rezViewModel.getOpeningHours("Bearer ${sharedPreferences.getString("token", "token")}", args!!.id)
        rezViewModel.getOpeningHoursResponse.observe(
            viewLifecycleOwner, Observer {
                binding.progressBar.visible(it is Resource.Loading)
                when(it) {
                    is Resource.Success -> {
                        //
                        val data = it.value.data
                        if (data == null){
                            binding.openingHours.visibility = View.GONE
                        } else{
                            binding.openingHours.visibility = View.VISIBLE
                            binding.openingHours.setOnClickListener {
                                val action = SearchFragmentDirections.actionSearchFragmentToOpeningHoursFragment(data)
                                findNavController().navigate(action)
                            }
                        }
                    }
                    is Resource.Failure -> handleApiError(it) { getOpeningHours() }
                }
            }
        )
    }

    private fun registerObservers() {
        rezViewModel.addOrRemoveFavoritesResponse.observe(viewLifecycleOwner) {
            binding.progressBar.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    if (binding.unLikeIv.isVisible) {
                        showToast("Added Successfully to favorites")
                        //rezViewModel.favoriteResponse = 1
                        binding.likeIv.visibility = View.VISIBLE
                        binding.unLikeIv.visibility = View.INVISIBLE
                        removeObserver()
                    } else if (!binding.unLikeIv.isVisible) {
                        showToast("Removed Successfully from favorites")
                        //rezViewModel.favoriteResponse = 0
                        binding.likeIv.visibility = View.INVISIBLE
                        binding.unLikeIv.visibility = View.VISIBLE
                        removeObserver()
                    }
                }
                is Resource.Failure -> handleApiError(it)
            }
        }
    }

    private fun removeObserver() {
        rezViewModel.addOrRemoveFavoritesResponse.removeObservers(viewLifecycleOwner)
    }

    override fun onTableItemClick(tableModel: Table) {
        val action = SearchFragmentDirections.actionSearchFragmentToTableDetails(tableModel)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        rezViewModel.clean()
        _binding = null
    }
}