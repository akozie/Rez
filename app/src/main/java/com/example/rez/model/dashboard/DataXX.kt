package com.example.rez.model.dashboard

import com.example.rez.model.dashboard.NearbyVendor
import com.example.rez.model.dashboard.RecommendedVendor
import com.example.rez.model.dashboard.SuggestedVendor

data class DataXX(
    val nearby_vendors: List<NearbyVendor>,
    val recommended_vendors: List<RecommendedVendor>,
    val suggested_vendors: List<SuggestedVendor>
)