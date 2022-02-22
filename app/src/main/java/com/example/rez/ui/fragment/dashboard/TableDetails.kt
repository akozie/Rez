package com.example.rez.ui.fragment.dashboard


import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.adapter.TableDetailsAdapter
import com.example.rez.adapter.TableDetailsFragmentViewPagerAdapter
import com.example.rez.adapter.TableDetailsViewPagerAdapter
import com.example.rez.adapter.TopRecommendedAdapter
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentTableDetailsBinding
import com.example.rez.model.dashboard.*
import com.example.rez.ui.GlideApp
import com.example.rez.ui.RezViewModel
import com.example.rez.util.handleApiError
import com.example.rez.util.showToast
import com.example.rez.util.visible
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.viewpagerindicator.CirclePageIndicator
import kotlinx.coroutines.launch
import javax.inject.Inject


class TableDetails : Fragment() {

    private var _binding: FragmentTableDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var argsId: Int = 0
    private val rezViewModel: RezViewModel by activityViewModels()

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
        _binding = FragmentTableDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gson = Gson()
        val json = sharedPreferences.getString("tablelist", "tablelist")
        val obj = gson.fromJson(json, Array<Table>::class.java)

      //  tableList = obj.toList()

        argsId = arguments?.getParcelable<Table>("TABLE")!!.id
        sharedPreferences.edit().putInt("tid", argsId).apply()
        setUpTable()
        setUpAboutTable()
    }

    private fun setUpTable(){
       val vendoID = sharedPreferences.getInt("vendorid", 0)
        rezViewModel.getVendorProfileTable(vendoID, argsId, "Bearer ${sharedPreferences.getString("token", "token")}")
        rezViewModel.getProfileTableResponse.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)
            when(it) {
                is Resource.Success -> {
                    if (it.value.status){
                        val tableList = it.value.data
                        binding.tabPriceTv.text = tableList.price
                        binding.tabCapacityTv.text = tableList.max_people.toString()
                        GlideApp.with(requireContext()).load(tableList.image).into(binding.hotelImageIv)
                        binding.tabNameTv.text = tableList.name
                        sharedPreferences.edit().putString("tablename", tableList.name).apply()
                        sharedPreferences.edit().putString("tablequantity", tableList.max_people.toString()).apply()
                       // showToast(it.value.message)
                    } else {
                        it.value.message?.let { it1 ->
                            Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
                    }
                }
                is Resource.Failure -> handleApiError(it)
            }
        })
    }

    private fun setUpAboutTable() {
         viewPager2 = binding.pager
         tabLayout = binding.tabs

        val adapter = TableDetailsFragmentViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager2.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.about)
                1 -> tab.text = getString(R.string.review)
            }
        }.attach()
    }
}