package com.example.rez.ui.fragment.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rez.R
import com.example.rez.adapter.OnTableClickListener
import com.example.rez.adapter.TableAdapter
import com.example.rez.databinding.FragmentNearRestBinding
import com.example.rez.databinding.FragmentSuggestBinding
import com.example.rez.model.dashboard.SuggestionRestaurantData
import com.example.rez.model.dashboard.TableData
import com.example.rez.ui.GlideApp
import com.example.rez.ui.RezViewModel


class SuggestFragment : Fragment(), OnTableClickListener {


    private var _binding: FragmentSuggestBinding? = null
    private val binding get() = _binding!!
    private lateinit var rezViewModel: RezViewModel
    private lateinit var tableAdapter: TableAdapter
    private lateinit var tableList: ArrayList<TableData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSuggestBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setList()
        setSuggestionData()
        setRecyclerview()
    }

    private fun setSuggestionData() {
        val args = arguments?.getParcelable<SuggestionRestaurantData>("SUGGESTIONDATA")
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
                5, "#20,000", ""),
            TableData("18th of Jan, 22", "One of the most popular table designs, the two to four sets, is generally sold " +
                    "as two separate components, the base and the tabletop. The tablet..", 1, R.drawable.banner_1,
                5, "#20,000", ""),
            TableData("18th of Jan, 22", "One of the most popular table designs, the two to four sets, is generally sold " +
                    "as two separate components, the base and the tabletop. The tablet..", 1, R.drawable.banner_1,
                5, "#20,000", "")
        )
    }

    override fun onTableItemClick(tableModel: TableData) {
        val action = SuggestFragmentDirections.actionSuggestFragmentToTableDetails(tableModel)
        findNavController().navigate(action)
    }

}