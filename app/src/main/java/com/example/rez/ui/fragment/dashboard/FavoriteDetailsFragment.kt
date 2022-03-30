package com.example.rez.ui.fragment.dashboard

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
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
import com.example.rez.databinding.FragmentFavoriteDetailsBinding
import com.example.rez.databinding.FragmentNearRestBinding
import com.example.rez.model.authentication.response.Favourite
import com.example.rez.model.dashboard.Image
import com.example.rez.model.dashboard.NearbyVendor
import com.example.rez.model.dashboard.Table
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.fragment.ProfileManagementDialogFragments
import com.example.rez.util.handleApiError
import com.example.rez.util.visible
import com.viewpagerindicator.CirclePageIndicator
import javax.inject.Inject


class FavoriteDetailsFragment : Fragment(), OnTableClickListener {
    private var _binding: FragmentFavoriteDetailsBinding? = null
    private val binding get() = _binding!!

    private val rezViewModel: RezViewModel by activityViewModels()
    private lateinit var tableAdapter: TableAdapter
    private lateinit var tableList: List<Table>
    private var args: Favourite? = null
    private lateinit var sliderDot: CirclePageIndicator
    private var tableDetailsPagerAdapter: TableDetailsViewPagerAdapter? = null
    private var tableDetailsViewPager: ViewPager? = null
    private lateinit var tableDetailsDataList: List<Image>
    private var current = 0
    private var firstName: String? = null

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
        _binding = FragmentFavoriteDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args = arguments?.getParcelable("FAVORITEDATA")
        setList()
        setNearData()
        sharedPreferences.edit().putInt("vendorid", args!!.id).apply()

        // setRecyclerview()
        accountFilterDialog()
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


    private fun setNearData() {
        binding.hotelNameTv.text = args?.company_name
        binding.ratingBar.rating = args?.ratings!!
    }

    override fun onTableItemClick(tableModel: Table) {
        val action = FavoriteDetailsFragmentDirections.actionFavoriteDetailsFragmentToTableDetails(tableModel)
        findNavController().navigate(action)
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

    private fun filter(guest: String, price: String) {
        var newList = listOf<Table>()
        newList = tableList.filter { it.price <= price || it.max_people.toString() == guest }

        tableAdapter = TableAdapter(newList, this)
        binding.tableListRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.tableListRecycler.adapter = tableAdapter
    }

    private fun accountFilterDialog() {
        // when first name value is clicked
        childFragmentManager.setFragmentResultListener(
            TopFragment.FILTER_NAME_REQUEST_KEY,
            requireActivity()
        ){ key, bundle ->
            // collect input values from dialog fragment and update the firstname text of user
            firstName = bundle.getString(TopFragment.ACCOUNT_FILTER_BUNDLE_KEY)
            binding.saveText.text = firstName
        }
        childFragmentManager.setFragmentResultListener(
            TopFragment.FILTER_SECOND_NAME_REQUEST_KEY,
            requireActivity()
        ) { key, bundle ->
            // collect input values from dialog fragment and update the firstname text of user
            val secondName = bundle.getString(TopFragment.ACCOUNT_SECOND_FILTER_BUNDLE_KEY)
            binding.priceText.text = secondName
            filter(firstName!!, secondName!!)
            //  filter(secondName!!, firstName!!)
        }

        // when first name value is clicked
        binding.filterImageIv.setOnClickListener {
            val currentFilterName = binding.saveText.toString()
            val currentFilterSecondName = binding.priceText.toString()
            val bundle = bundleOf(TopFragment.CURRENT_FILTER_NAME_BUNDLE_KEY to currentFilterName)
            val bundleSecond = bundleOf(TopFragment.CURRENT_SECOND_FILTER_NAME_BUNDLE_KEY to currentFilterSecondName)
            ProfileManagementDialogFragments.createProfileDialogFragment(
                R.layout.account_filter_dialog_fragment,
                bundle, bundleSecond
            ).show(
                childFragmentManager, TopFragment::class.java.simpleName
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}