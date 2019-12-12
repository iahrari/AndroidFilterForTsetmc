package com.example.tsetmc.viewmodel

interface SortingComponent{
    fun handleItemsSorting(i: Int)
//    fun setAscendingSort()
//    fun setDescendingSort()
    fun setSortingMethod(isAscending: Boolean)
}