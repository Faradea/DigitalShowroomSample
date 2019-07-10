package ru.macgavrina.digitalshowroom.model

data class Color (
    val id: Int,
    val hexacode: String,
    val trigram: String,
    val shortLIB: String,
    val longLIB: String,
    val isAmi: String,
    val genericColorID: Int
)