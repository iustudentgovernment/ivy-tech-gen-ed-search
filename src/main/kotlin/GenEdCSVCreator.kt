import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.File
import java.io.FileWriter

fun main() {
    val equivalencyFile = File("gen-ed-equivalencies.txt").readLines()

    val genEdAreas = mutableSetOf<String>()

    val equivalencies = equivalencyFile.map { line ->
        val split = line.split("->")
        val ivyTechCourse = split[0].substringAfter("IVY|")
        val iuCourse = split[1].substringAfter("IU|")
        val genEds = split[2].substringAfter("GE|").split(",")
        if (split[2] != "None") genEds.forEach { genEdAreas.add(it) }

        Triple(ivyTechCourse, iuCourse, genEds)
    }.filterNot { it.third.size == 1 && it.third[0] == "None" }

    genEdAreas.remove("None")

    val equivalenciesByArea = genEdAreas.associateWith { area ->
        equivalencies.filter { it.third.contains(area) }
    }

    val out = FileWriter("ivy-tech-gen-ed-transfer.csv")

    CSVPrinter(
        out,
        CSVFormat.EXCEL
            .withHeader("Ivy Tech Course", "IU Course", "Gen Ed Area")
    ).use { printer ->
        repeat(2) { printer.emptyLine() }
        printer.printRecord("Equivalencies by Gen Ed Area")

        equivalenciesByArea.forEach { area, courses ->
            repeat(3) { printer.emptyLine() }
            printer.printRecord(area)
            courses.forEach { course -> printer.printRecord(course.first, course.second, area) }
            printer.printRecord("Total:", courses.size)
        }

        repeat(3) { printer.emptyLine() }
        printer.printRecord("---------------")
        repeat(3) { printer.emptyLine() }

        printer.printRecord("Equivalencies by Ivy Tech Course")
        equivalencies.forEach { (ivyTechCourse, iuCourse, genEdAreas) ->
            printer.printRecord(ivyTechCourse, iuCourse, genEdAreas.joinToString(", "))
        }
        printer.printRecord("Total:", equivalencies.size)
    }
}

fun CSVPrinter.emptyLine() = printRecord("")