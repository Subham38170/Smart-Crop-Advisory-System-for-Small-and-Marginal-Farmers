package com.example.krishimitra.presentation.mandi_screen

import com.example.krishimitra.presentation.nav_graph.Routes

sealed interface UiAction {

    data class onStateSelect(val state: String) : UiAction


    data class onDistrictSelect(val district: String) : UiAction


    data object onDistrictDeselect : UiAction

    data object onStateDeselect : UiAction


    data class onSearch(val searchText: String) : UiAction

    data object loadAllCrops : UiAction

    data class navigateTo(val route: Routes) : UiAction
}