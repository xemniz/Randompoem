package ru.xmn.randompoem.model

data class Poet(val id: String, val name: String, val century: Int?, var poems: List<Poem>?)
data class Poem(val id: String, val poet: Poet?, val title: String, val text: String, val year: String)

class PoetFB {
    val id: String? = null
    val name: String? = null
    val century: Int? = null
    var poems: List<Poem>? = null
}

class PoemFB {
    val id: String? = null
    val year: String? = null
    val poet: Poet? = null
    val title: String? = null
    val text: String? = null
}
