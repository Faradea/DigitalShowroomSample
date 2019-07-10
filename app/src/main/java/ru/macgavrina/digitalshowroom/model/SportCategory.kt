package ru.macgavrina.digitalshowroom.model

data class SportCategory (
    val id: Int,
    val description: String,
    val children: List<SportCategory>,
    val active: Boolean,
    val name: String,
    val isSport: Boolean,
    val sportID: Int? = null,
    val parentID: Int? = null,
    val inMainNavigation: Boolean,
    val imageHash: Any? = null
)