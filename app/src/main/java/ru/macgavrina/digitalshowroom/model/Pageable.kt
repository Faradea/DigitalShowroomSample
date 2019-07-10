package ru.macgavrina.digitalshowroom.model

data class Pageable (
    val sort: Sort,
    val pageSize: Int,
    val pageNumber: Int,
    val offset: Int,
    val paged: Boolean,
    val unpaged: Boolean
)