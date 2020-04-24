import org.openqa.selenium.By
import org.openqa.selenium.remote.RemoteWebDriver

fun checkForGenEd(iuCourse: IUCourse, webDriver: RemoteWebDriver): GenEd? {
    val url =
        "https://gened.indiana.edu/scripts/course-search/genedsearch.php?SEARCH=$iuCourse&ED=4195&sort=alphaBySUB&page=1&displayCount=20"
    webDriver.get(url)

    val results = webDriver.findElementsByClassName("result")

    if (results.isEmpty()) {
        return null
    } // no gen ed course

    return GenEd(results.map { result ->
        val leftResult = result.findElements(By.tagName("span"))[0]
        leftResult.findElement(By.className("indicator")).text
    })
}

data class GenEd(val types: List<String>)