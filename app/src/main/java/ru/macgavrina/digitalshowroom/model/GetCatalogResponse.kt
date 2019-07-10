package ru.macgavrina.digitalshowroom.model

data class GetCatalogResponse (
    val content: List<GetCatalogResponseContent>,
    val pageable: Pageable,
    val totalElements: Int,
    val last: Boolean,
    val totalPages: Int,
    val sort: Sort,
    val numberOfElements: Int,
    val first: Boolean,
    val size: Int,
    val number: Int,
    val empty: Boolean
)