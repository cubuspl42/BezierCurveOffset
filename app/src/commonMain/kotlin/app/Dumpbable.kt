package app

interface Dumpbable {
    fun dump(): String
}

fun List<Dumpbable>.dump() = "listOf(" + this.map { "${it.dump()},\n" }.joinToString(separator = "") + ")"
