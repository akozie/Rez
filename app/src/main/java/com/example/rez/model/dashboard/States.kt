package com.example.rez.model.dashboard

data class States(
    val id: Int,
    val state: String
) {
    override fun toString(): String {
        return state
    }
}