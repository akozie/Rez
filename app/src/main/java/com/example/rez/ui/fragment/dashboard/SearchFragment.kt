package com.example.rez.ui.fragment.dashboard

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.example.rez.util.visible
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
        setList()
        searchData()
        sharedPreferences.edit().putInt("vendorid", args!!.id).apply()

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
        // binding.addressTv.text = args?.address
        // binding.categoryTv.text = args?.categoryName
        //binding.distanceTv.text = args?.distance.toString()
        // binding.tableQtyTv.text = args?.tables
        //GlideApp.with(requireContext()).load(args?.avatar).into(binding.hotelImageIv)
    }


    private fun setList() {
        rezViewModel.getVendorTables(args!!.id, "Bearer ${sharedPreferences.getString("token", "token")}")
        rezViewModel.getVendorTableResponse.observe(
            viewLifecycleOwner, Observer {
                binding.progressBar.visible(it is Resource.Loading)
                when(it) {
                    is Resource.Success -> {
                        if (it.value.status){
                            val message = it.value.message
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            tableList = it.value.data.tables
                            tableDetailsDataList = it.value.data.images
                            tableAdapter = TableAdapter(tableList, this)
                            binding.tableListRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                            binding.tableListRecycler.adapter = tableAdapter
                            setTableDetailsViewPagerAdapter()
                            // setRecyclerview()
                            //nearList = emptyList()
                        } else {
                            it.value.message?.let { it1 ->
                                Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
                        }
                    }
                    is Resource.Failure -> handleApiError(it)
                }
            }
        )
    }


    override fun onTableItemClick(tableModel: Table) {
        val action = SearchFragmentDirections.actionSearchFragmentToTableDetails(tableModel)
        findNavController().navigate(action)
    }
}