package ru.macgavrina.digitalshowroom.model

data class CatalogItem (
    val id: Int,
    val description: String,
    val price: Int,
    val review: Review,
    val benefits: List<Benefit>,
    val characteristics: List<Characteristic>,
    val brand: Brand,
    val image: Image,
    val colors: List<Color>,
    val url: String,
    val osmose: Boolean,
    val name: String,
    val promoPrice: Int,
    val images: List<String>,
    val isSale: Boolean
)