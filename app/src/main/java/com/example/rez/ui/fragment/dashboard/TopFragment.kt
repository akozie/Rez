package com.example.rez.ui.fragment.dashboard

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rez.R
import com.example.rez.adapter.OnTableClickListener
import com.example.rez.adapter.TableAdapter
import com.example.rez.databinding.FragmentTableListBinding
import com.example.rez.model.dashboard.TableData
import com.example.rez.model.dashboard.TopRecommendedData
import com.example.rez.ui.GlideApp
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.activity.DashboardActivity
import com.example.rez.ui.fragment.ProfileManagementDialogFragments


class TopFragment : Fragment(), OnTableClickListener {

    private var _binding: FragmentTableListBinding? = null
    private val binding get() = _binding!!
    private lateinit var rezViewModel: RezViewModel
    private lateinit var tableAdapter: TableAdapter
    private lateinit var tableList: ArrayList<TableData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         _binding = FragmentTableListBinding.inflate(inflater, container, false)
        rezViewModel = (activity as DashboardActivity).rezViewModel

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setList()
        setTopData()
        setRecyclerview()
        accountFilterDialog()

    }

    private fun setTopData() {
        val args = arguments?.getParcelable<TopRecommendedData>("TOPDATA")
        binding.addressTv.text = args?.address
        binding.categoryTv.text = args?.categoryName
        binding.distanceTv.text = args?.distance
        binding.tableQtyTv.text = args?.tables
        GlideApp.with(requireContext()).load(args?.restaurantImage).into(binding.hotelImageIv)
    }

    private fun setRecyclerview() {
        tableAdapter = TableAdapter(tableList, this)
        binding.tableListRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.tableListRecycler.adapter = tableAdapter
    }

    private fun setList() {
        tableList = arrayListOf(
            TableData("18th of Jan, 22", "One of the most popular table designs, the two to four sets, is generally sold " +
                    "as two separate components, the base and the tabletop. The tablet..", 1, R.drawable.banner_1,
            10, "#10,000", ""),
            TableData("18th of Jan, 22", "One of the most popular table designs, the two to four sets, is generally sold " +
                    "as two separate components, the base and the tabletop. The tablet..", 1, R.drawable.banner_1,
            10, "#10,000", ""),
            TableData("18th of Jan, 22", "One of the most popular table designs, the two to four sets, is generally sold " +
                    "as two separate components, the base and the tabletop. The tablet..", 1, R.drawable.banner_1,
            10, "#10,000", "")
        )
    }

    override fun onTableItemClick(tableModel: TableData) {
        val action = TopFragmentDirections.actionTopFragmentToTableDetails(tableModel)
        findNavController().navigate(action)
    }

    private fun accountFilterDialog() {
        // when first name value is clicked
        childFragmentManager.setFragmentResultListener(
            TopFragment.ACCOUNT_FILTER_BUNDLE_KEY,
            requireActivity()
        ) { key, bundle ->
            // collect input values from dialog fragment and update the firstname text of user
            val firstName = bundle.getString(TopFragment.FILTER_NAME_REQUEST_KEY)
            binding.saveText.text = firstName
        }

        // when first name value is clicked
        binding.filterImageIv.setOnClickListener {
            val currentFilterName = binding.saveText.toString()
            val bundle = bundleOf(TopFragment.CURRENT_FILTER_NAME_BUNDLE_KEY to currentFilterName)
            ProfileManagementDialogFragments.createProfileDialogFragment(
                R.layout.account_filter_dialog_fragment,
                bundle
            ).show(
                childFragmentManager, TopFragment::class.java.simpleName
            )
        }
    }
    companion object {
        const val FILTER_NAME_REQUEST_KEY = "FILTER NAME REQUEST KEY"
        const val ACCOUNT_FILTER_BUNDLE_KEY = "ACCOUNT FILTER BUNDLE KEY"
        const val CURRENT_FILTER_NAME_BUNDLE_KEY = "CURRENT FILTER NAME BUNDLE KEY"
    }
    }