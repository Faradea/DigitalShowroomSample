package ru.macgavrina.digitalshowroom.model

data class Product (
    val id: Int,
    val price: Int,
    val image: Image,
    val colors: List<Color>,
    val url: String,
    val dsm: String,
    val review: Review,
    val description: String,
    val images: List<Image>,
    val brand: Brand,
    val gender: Gender,
    val family: Family,
    val benefits: List<Benefit>,
    val characteristics: List<Characteristic>,
    val sports: List<SportInProductDetails>,
    val articles: List<Article>,
    val osmose: Boolean,
    val name: String,
    val promo_price: Int,
    val is_sale: Boolean,
    val model_name: String,
    val product_nature: ProductNature,
    val sport_groups: List<SportInProductDetails>,
    val sport_practices: List<SportInProductDetails>,
    val sparkow_categories: List<SparkowCategory>
)

data class Article (
    val id: Int,
    val articleID: Int,
    val weight: Double,
    val price: Int,
    val size: String? = null,
    val weightUnit: WeightUnit,
    val osmose: Boolean
)

enum class WeightUnit {
    Kgm
}

data class Family (
    val id: Int,
    val name: String,
    val nameEng: String,
    val subDepartments: Family? = null,
    val department: Family? = null,
    val universe: Family? = null
)

data class Gender (
    val id: Int,
    val label: String,
    val children: List<Any?>,
    val active: Boolean,
    val shortLabel: String
)

data class ProductNature (
    val id: Int,
    val label: String,
    val level: Int,
    val type: String,
    val children: List<Any?>,
    val active: Boolean
)

data class SparkowCategory (
    val id: Int,
    val description: String,
    val lvl: Int,
    val name: String,
    val imageHash: Any? = null
)

data class SportInProductDetails (
    val id: Int,
    val label: String,
    val active: Boolean
)