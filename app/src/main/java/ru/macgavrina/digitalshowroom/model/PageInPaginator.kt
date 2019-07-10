package ru.macgavrina.digitalshowroom.model

const val PAGINATOR_TYPES_REALPAGE = 0
const val PAGINATOR_TYPES_NEXT = 1
const val PAGINATOR_TYPES_PREVIOUS = 2
const val PAGINATOR_TYPES_DIVIDER = 3

data class PageInPaginator (
    val text: String,
    val type: Int,
    var isCurrentPage: Boolean
)
