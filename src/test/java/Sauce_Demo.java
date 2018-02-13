import com.saucelabs.saucerest.SauceREST;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class Sauce_Demo {

    private WebDriver driver;

    private static String Username = "";
    private static String AccessKey = "";

    private static String Hub = String.format("http://%s:%s@ondemand.saucelabs.com:80/wd/hub", Username, AccessKey);


    @BeforeTest
    public void setup() throws MalformedURLException {
        // Use which ever capabilities you want here
        DesiredCapabilities caps = DesiredCapabilities.firefox();
        caps.setCapability("platform", "Windows 10");
        caps.setCapability("version", "58.0");

        caps.setCapability("name", "Creating Studio Code Account");
        caps.setCapability("tags", "Creating");
        caps.setCapability("build", "build-1234");

        driver = new RemoteWebDriver(new URL(Hub), caps);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test
    public void SuccessfulCreateCodeStudioAccount(){
        driver.get("https://studio.code.org/courses");
        driver.findElement(By.cssSelector("a[href=\"/users/sign_up\"]")).click();
        new Select(driver.findElement(By.id("user_user_type"))).selectByValue("student");

        driver.findElement(By.id("user_email")).sendKeys("andrew@lazycoder.io");
        driver.findElement(By.id("user_password")).sendKeys("WeakPassword");
        driver.findElement(By.id("user_password_confirmation")).sendKeys("WeakPassword");

        driver.findElement(By.id("user_name")).sendKeys("Andrew");
        new Select(driver.findElement(By.id("user_user_age"))).selectByValue("21+");

        new Select(driver.findElement(By.id("user_gender"))).selectByValue("m");

        driver.findElement(By.id("signup-button")).click();


        // Using the Sauce REST client to update the test results
        SauceREST r = new SauceREST(Username, AccessKey);
        String job_id = (((RemoteWebDriver) driver).getSessionId()).toString();

        try{
            Assert.assertTrue(driver.findElement(By.id("homepage-container")).getText().contains("My Dashboard"));
            r.jobPassed(job_id);
        }
        catch (Exception e){
            r.jobFailed(job_id);
        }
    }

    @AfterTest
    public void teardown(){
        driver.quit();
    }
}
