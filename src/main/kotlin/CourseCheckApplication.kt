import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import java.io.File
import java.net.URL
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

val outputFile = File("gen-ed-equivalencies.txt")

val outputInUse = AtomicBoolean(false)

fun main(args: Array<String>) {
    if (!outputFile.exists()) outputFile.createNewFile()
    println("Output file located at: ${outputFile.absolutePath}")

    val executor = Executors.newCachedThreadPool()

    val ivyTechCourses = ConcurrentLinkedQueue<CourseName>()
    ivyTechCourses.addAll(parseCatalog(args[0]))

    repeat((1..5).count()) { executor.execute(ivyTechCourses, args.getOrElse(1) {"/Users/adamratzman/Downloads/chromedriver"}) }
}

fun getGenEdFor(courseName: CourseName, webDriver: RemoteWebDriver): Pair<IUCourse, GenEd>? {
    return try {
        getCourseEquivalency(courseName, webDriver)?.let { iuCourse ->
            checkForGenEd(iuCourse, webDriver)?.let { iuCourse to it }
        } ?: {
            println("No IU course found for IVY|$courseName")
            null
        }.invoke()
    } catch (ise: IllegalStateException) {
        ise.printStackTrace()
        null
    } catch (ignored: Exception) {
        ignored.printStackTrace()
        null
    }
}

fun Executor.execute(courses: ConcurrentLinkedQueue<CourseName>, webdriverPath: String) {
    execute {
        while (courses.isNotEmpty()) {
            val courseName = courses.poll()
            if (!outputFile.readLines().any { it.startsWith("IVY|$courseName->") }) {
                val webDriver = createWebDriver(webdriverPath)
                val genEd = getGenEdFor(courseName, webDriver)
                val string = "IVY|$courseName->IU|${genEd?.first ?: "None"}->GE|${genEd?.second?.types?.joinToString(",") ?: "None"}\n"
                appendToFile(string)
                webDriver.quit()
            }
        }
    }
}

fun appendToFile(string: String) {
    while (outputInUse.get()) {}
    outputInUse.set(true)
    outputFile.appendText(string)
    outputInUse.set(false)
}

fun createWebDriver(webdriverPath: String): RemoteWebDriver {
    System.setProperty("webdriver.chrome.driver", webdriverPath)

    return RemoteWebDriver(URL("http://localhost:4444/wd/hub"), DesiredCapabilities.chrome())

}