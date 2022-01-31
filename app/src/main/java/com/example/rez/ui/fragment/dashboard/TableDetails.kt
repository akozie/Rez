package com.example.rez.ui.fragment.dashboard


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.rez.R
import com.example.rez.adapter.TableDetailsFragmentViewPagerAdapter
import com.example.rez.adapter.TableDetailsViewPagerAdapter
import com.example.rez.databinding.FragmentTableDetailsBinding
import com.example.rez.model.dashboard.TableDetailsData
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.viewpagerindicator.CirclePageIndicator
import java.util.ArrayList


class TableDetails : Fragment() {

    private var _binding: FragmentTableDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sliderDot: CirclePageIndicator
    private var tableDetailsPagerAdapter: TableDetailsViewPagerAdapter? = null
    private var tableDetailsViewPager: ViewPager? = null
    private lateinit var tableDetailsDataList: MutableList<TableDetailsData>
    private var current = 0
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
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
        setUpAboutTable()
        tableDetailsViewPager = binding.viewPager
        sliderDot = binding.indicator

        //Tab indicator listener
        sliderDot.setOnPageChangeListener(object : ViewPager.OnPageChangeListener{
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
        setTableDetailsViewPagerAdapter()
    }

    private fun setTableDetailsViewPagerAdapter() {
        tableDetailsDataList = ArrayList()
        tableDetailsDataList.add(TableDetailsData(R.drawable.sp1, "Couples Friendly Tables", 2, "#500"))
        tableDetailsDataList.add(TableDetailsData(R.drawable.sp1, "Couples Friendly Tables", 2, "#500"))
        tableDetailsDataList.add(TableDetailsData(R.drawable.sp1, "Couples Friendly Tables", 2, "#500"))


        tableDetailsPagerAdapter = TableDetailsViewPagerAdapter(requireContext(), tableDetailsDataList)
        tableDetailsViewPager?.adapter = tableDetailsPagerAdapter
        tableDetailsViewPager?.setCurrentItem(current++, true)
        sliderDot.setViewPager(tableDetailsViewPager)
        val density = resources.displayMetrics.density
        sliderDot.radius = 5 * density
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