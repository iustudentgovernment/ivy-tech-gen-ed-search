import java.io.File

const val catalogFileLocation = "/Users/adamratzman/iu/2019/fall/academics/ivytech/course-catalog.txt"

fun parseCatalog(): List<CourseName> {
    val catalogFile = File(catalogFileLocation)

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