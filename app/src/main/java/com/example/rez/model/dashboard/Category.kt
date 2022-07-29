package com.example.rez.model.dashboard

data class Category(
    val id: Int,
    val name: String
) {
    override fun toString(): String {
        return name
    }
}