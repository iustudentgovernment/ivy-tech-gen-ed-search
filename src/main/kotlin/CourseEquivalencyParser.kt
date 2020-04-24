import org.openqa.selenium.By
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.Select

@Throws(Exception::class)
fun getCourseEquivalency(courseName: CourseName, webDriver: RemoteWebDriver): IUCourse? {
    try {
        println("Getting course equivalency for $courseName")
        webDriver.get("https://cts.admissions.indiana.edu/transferin.cfm")

        Thread.sleep(1000)

        val selectState = Select(webDriver.findElementById("STATES"))
        try {
            selectState.selectByValue("IN")
        } catch (e: Exception) {
            return getCourseEquivalency(courseName, webDriver)
        }

        while (Select(webDriver.findElementById("COLLEGE")).options.none { it.getAttribute("value") == "2000130738" }) {
            Thread.sleep(500)
        }

        val selectCollege = Select(webDriver.findElementById("COLLEGE"))
        selectCollege.selectByValue("2000130738")

        Thread.sleep(5000)

        while (Select(webDriver.findElementById("SUBJECT")).options.isEmpty()) {
            Thread.sleep(500)
        }

        val selectDepartment = Select(webDriver.findElementById("SUBJECT"))
        try {
            selectDepartment.selectByValue(courseName.department)
        } catch (e: Exception) {
            return null
        }

        Thread.sleep(2000)

        val selectCourse = Select(webDriver.findElementById("COURSE"))
        val correspondingCourse =
            selectCourse.options.firstOrNull { it.text.split(" ")[0] == courseName.number } ?: return null
        selectCourse.selectByVisibleText(correspondingCourse.text)

        Thread.sleep(6000)

        val form = webDriver.findElementsByTagName("form").getOrNull(1) ?: return null

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

        return IUCourse(
            CourseName(iuCourse.split(" ")[0], iuCourse.split(" ")[1]),
            iuCourseDescription,
            iuCreditsReceived
        )
    } catch (e: StaleElementReferenceException) {
        return getCourseEquivalency(courseName, webDriver)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

data class IUCourse(val courseName: CourseName, val description: String, val credits: Int) {
    override fun toString() = courseName.toString()
}