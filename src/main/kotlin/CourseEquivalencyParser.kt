import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.Select
import java.net.URL

@Throws(Exception::class)
fun getCourseEquivalency(courseName: CourseName, webDriver: RemoteWebDriver): IUCourse? {
    webDriver.get("https://cts.admissions.indiana.edu/transferin.cfm")

    val selectState = Select(webDriver.findElementById("STATES"))
    selectState.selectByValue("IN")

    while (Select(webDriver.findElementById("COLLEGE")).options.none { it.getAttribute("value") == "2000130738" }) {
        Thread.sleep(500)
    }

    val selectCollege = Select(webDriver.findElementById("COLLEGE"))
    selectCollege.selectByValue("2000130738")

    while (Select(webDriver.findElementById("SUBJECT")).options.none { it.getAttribute("value") == courseName.department }) {
        Thread.sleep(500)
    }

    val selectDepartment = Select(webDriver.findElementById("SUBJECT"))
    selectDepartment.selectByValue(courseName.department)

    Thread.sleep(2000)

    val selectCourse = Select(webDriver.findElementById("COURSE"))
    val correspondingCourse = selectCourse.options.firstOrNull { it.text.split(" ")[0] == courseName.number } ?: return null
    selectCourse.selectByVisibleText(correspondingCourse.text)

    Thread.sleep(3000)

    val form = webDriver.findElementsByTagName("form")[1]

    val lis = form.findElements(By.tagName("ol")).map { it.findElement(By.tagName("li")) }

    val toCourseDiv = lis[1]
    val courseInformationDivs = toCourseDiv.findElements(By.tagName("div"))
    val iuCourse = courseInformationDivs[0]
        .findElements(By.tagName("span"))[1].text
    if (iuCourse.contains("NTRN")) {
        return null
    } // not transferable

    val iuCourseDescription = courseInformationDivs[1]
        .findElements(By.tagName("span"))[1].text

    val iuCreditsReceived = courseInformationDivs[2]
        .findElements(By.tagName("span"))[1].text.toIntOrNull() ?: 0

    return IUCourse(CourseName(iuCourse.split(" ")[0], iuCourse.split(" ")[1]), iuCourseDescription, iuCreditsReceived)
}

data class IUCourse(val courseName: CourseName, val description: String, val credits: Int) {
    override fun toString() = courseName.toString()
}