package ru.macgavrina.digitalshowroom.model

data class Benefit (
    val id: Int,
    val title: String,
    val text: String,
    val definition: String,
    val active: Boolean
)