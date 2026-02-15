package com.example.krishimitra.domain

data class StateModel(
    val state: String,
    val districts: List<String>
)

data class StatesWrapper(
    val states: List<StateModel>
)
