import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import java.lang.IllegalStateException
import java.net.URL

fun checkForGenEd(iuCourse: IUCourse, webDriver: RemoteWebDriver): GenEd? {
    val url = "https://gened.indiana.edu/scripts/course-search/genedsearch.php?SEARCH=$iuCourse&ED=4195&sort=alphaBySUB&page=1&displayCount=20"
    webDriver.get(url)

    val results = webDriver.findElementsByClassName("result")

    if (results.isEmpty()) {
        return null
    } // no gen ed course

    val result = results[0]
    val leftResult = result.findElements(By.tagName("span"))[0]
    val genEdType = leftResult.findElement(By.className("indicator")).text

    return GenEd(genEdType)
}

data class GenEd(val type: String)