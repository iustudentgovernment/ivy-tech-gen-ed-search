import java.io.File

fun parseCatalog(path: String): List<CourseName> {
    val catalogFile = File(path)

    return catalogFile.readLines()
        .filter {
            it.isNotEmpty() && it.length > 4
                    && it.substring(0, 3).toUpperCase() == it.substring(0, 3)
        }.map { line ->
            line.split(" ").let {
                CourseName(it[0], it[1])
            }
        }
}


data class CourseName(val department: String, val number: String) {
    override fun toString() = "$department $number"
}