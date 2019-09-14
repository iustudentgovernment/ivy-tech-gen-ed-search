import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import java.net.URL
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executor
import java.util.concurrent.Executors

fun main(args: Array<String>) {
    val executor = Executors.newCachedThreadPool()

    val ivyTechCourses = ConcurrentLinkedQueue<CourseName>()
    ivyTechCourses.addAll(parseCatalog(args[0]))

    (1..3).forEach { executor.execute(ivyTechCourses) }
}

fun getGenEdFor(courseName: CourseName, webDriver: RemoteWebDriver): Pair<IUCourse, GenEd>? {
    return try {
        getCourseEquivalency(courseName, webDriver)?.let { iuCourse ->
            checkForGenEd(iuCourse, webDriver)?.let { iuCourse to it }
        }
    } catch (ise: IllegalStateException) {
        ise.printStackTrace()
        null
    } catch (ignored: Exception) {
        ignored.printStackTrace()
        null
    }
}

fun Executor.execute(courses: ConcurrentLinkedQueue<CourseName>) {
    execute {
        while (courses.isNotEmpty()) {
            val webDriver = createWebDriver()
            val courseName = courses.poll()
            val genEd = getGenEdFor(courseName, webDriver)
            if (genEd == null) println("Gen ed for $courseName: NONE")
            else println("Gen ed for $courseName: ${genEd.second}")
            webDriver.quit()
        }
    }
}

fun createWebDriver(): RemoteWebDriver {
    System.setProperty("webdriver.chrome.driver", "/Users/adamratzman/Downloads/chromedriver");

    return RemoteWebDriver(URL("http://localhost:4444/wd/hub"), DesiredCapabilities.chrome())

}