package ru.macgavrina.digitalshowroom.model

data class GetCatalogResponseContent (
    val id: String,
    val items: List<CatalogItem>
)