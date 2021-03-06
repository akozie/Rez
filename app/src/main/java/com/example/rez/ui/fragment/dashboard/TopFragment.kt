package com.example.rez.ui.fragment.dashboard

import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.adapter.OnTableClickListener
import com.example.rez.adapter.TableAdapter
import com.example.rez.adapter.TableDetailsViewPagerAdapter
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentTableListBinding
import com.example.rez.model.dashboard.*
import com.example.rez.ui.GlideApp
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.activity.DashboardActivity
import com.example.rez.ui.fragment.ProfileManagementDialogFragments
import com.example.rez.util.handleApiError
import com.example.rez.util.visible
import com.google.gson.Gson
import com.viewpagerindicator.CirclePageIndicator
import kotlinx.coroutines.launch
import javax.inject.Inject


class TopFragment : Fragment(), OnTableClickListener {

    private var _binding: FragmentTableListBinding? = null
    private val binding get() = _binding!!
    private val rezViewModel: RezViewModel by activityViewModels()
    private lateinit var tableAdapter: TableAdapter
    private lateinit var tableList: List<Table>
    private var args: RecommendedVendor? = null
    private lateinit var sliderDot: CirclePageIndicator
    private var tableDetailsPagerAdapter: TableDetailsViewPagerAdapter? = null
    private var tableDetailsViewPager: ViewPager? = null
    private lateinit var tableDetailsDataList: List<Image>
    private var firstName: String? = null
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
        _binding = FragmentTableListBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args = arguments?.getParcelable("TOPDATA")
        sharedPreferences.edit().putInt("vendorid", args!!.id).apply()

        setList()
        setTopData()
        //setRecyclerview()
        //accountFilterDialog()

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


private fun setTopData() {
        binding.hotelNameTv.text = args?.company_name
        binding.categoryTv.text = args?.category_name
    if (args?.average_rating?.toInt() == 0){
        binding.ratingBar.rating = "3".toFloat()
    }else {
        binding.ratingBar.rating = args?.average_rating!!
    }
        if (args?.total_tables == 1){
            binding.tableQtyTv.text = args?.total_tables.toString() + " table"
        }else{
            binding.tableQtyTv.text = args?.total_tables.toString() + " tables"
        }
        if (args?.liked_by_user == true){
            binding.likeIv.visible(true)
            binding.unLikeIv.visible(false)
        }else{
            binding.likeIv.visible(false)
            binding.unLikeIv.visible(true)
        }
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
                                tableDetailsDataList = it.value.data.images
                                tableList = it.value.data.tables
                              //  val description = it.value.data.tables.description
                           // sharedPreferences.edit().putString("tableid", description).apply()
                            tableAdapter = TableAdapter(tableList, this)
                                binding.tableListRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                                binding.tableListRecycler.adapter = tableAdapter
                                setTableDetailsViewPagerAdapter()

                                val gson = Gson()
                                val db = gson.toJson(tableList)
                                sharedPreferences.edit().putString("tablelist", db).apply()
                            // setRecyclerview()
                                //nearList = emptyList()
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

    override fun onTableItemClick(tableModel: Table) {
        val action = TopFragmentDirections.actionTopFragmentToTableDetails(tableModel)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val FILTER_NAME_REQUEST_KEY = "FILTER NAME REQUEST KEY"
        const val ACCOUNT_FILTER_BUNDLE_KEY = "ACCOUNT FILTER BUNDLE KEY"
        const val CURRENT_FILTER_NAME_BUNDLE_KEY = "CURRENT FILTER NAME BUNDLE KEY"

        const val FILTER_SECOND_NAME_REQUEST_KEY = "FILTER SECOND NAME REQUEST KEY"
        const val ACCOUNT_SECOND_FILTER_BUNDLE_KEY = "ACCOUNT SECOND FILTER BUNDLE KEY"
        const val CURRENT_SECOND_FILTER_NAME_BUNDLE_KEY = "CURRENT SECOND FILTER NAME BUNDLE KEY"
    }
}