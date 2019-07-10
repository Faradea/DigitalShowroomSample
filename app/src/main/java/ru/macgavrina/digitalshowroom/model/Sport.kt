package ru.macgavrina.digitalshowroom.model

data class Sport (
    val id: Int,
    val sparkow_id: Int? = null,
    val sport: String,
    val slug: String,
    val isActive: Boolean,
    val name: SportName
) {
    override fun toString(): String {
        return "Sport: id = $id, sparkowID = $sparkow_id, sport = $sport, slug = $slug, isActive = $isActive, nameEN = ${name.en}, nameRU = ${name.ru}"
    }
}