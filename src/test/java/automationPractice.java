import com.github.javafaker.Faker;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.time.Duration;

public class automationPractice {
    static Faker faker = new Faker();
    static String[] email = new String[10];
    WebDriver driver;
    WebDriverWait wait;
    int index = 0;
    int userIndex = 0;
    String password = "abcd1234#";

    @BeforeSuite
    void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://automationpractice.com/index.php");
        wait = new WebDriverWait(driver, Duration.ofSeconds(120));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("login"))).click();
    }

    @Test(invocationCount = 2, description = "Create two new accounts", priority = 1)
    void signUp() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(120));
        email[index++] = faker.numerify("autopractice####@grr.la");
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String address = faker.address().streetAddress();
        String city = faker.address().city();
        String postCode = String.valueOf(faker.number().numberBetween(10000, 99999));
        String phoneNumber = faker.phoneNumber().cellPhone();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email_create"))).sendKeys(email[index - 1]);
        driver.findElement(By.id("SubmitCreate")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("id_gender1"))).click();
        driver.findElement(By.id("customer_firstname")).sendKeys(firstName);
        driver.findElement(By.id("customer_lastname")).sendKeys(lastName);
        driver.findElement(By.id("passwd")).sendKeys(password);
        driver.findElement(By.id("address1")).sendKeys(address);
        driver.findElement(By.id("city")).sendKeys(city);

        Select state = new Select(driver.findElement(By.id("id_state")));
        state.selectByIndex(5);

        driver.findElement(By.id("postcode")).sendKeys(postCode);

        Select country = new Select(driver.findElement(By.id("id_country")));
        country.selectByVisibleText("United States");

        driver.findElement(By.id("phone_mobile")).sendKeys(phoneNumber);
        driver.findElement(By.id("submitAccount")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("logout"))).click();
    }

    @Test(description = "Login with the any of the new account", priority = 2)
    void login() {
        driver.findElement(By.id("email")).sendKeys(email[userIndex]);
        driver.findElement(By.id("passwd")).sendKeys(password);
        driver.findElement(By.id("SubmitLogin")).click();
        userIndex++;
    }

    @Test(description = "Go to the Casual Dresses section and add a dress into the cart", priority = 3)
    void addCasualDress() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(120));
        Actions actions = new Actions(driver);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='block_top_menu']/ul/child::li[2]")));
        actions.moveToElement(driver.findElement(By.xpath("//div[@id='block_top_menu']/ul/child::li[2]"))).perform();
        driver.findElement(By.xpath("//div[@id='block_top_menu']/ul/child::li[2]/ul/child::li[1]")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("product-container")));
        actions.moveToElement(driver.findElement(By.className("product-container"))).perform();
        driver.findElement(By.cssSelector("[title='Add to cart']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[title='Continue shopping']"))).click();
    }

    @Test(description = "Go to the T-shirt section > Filter the list with blue color > Add a shirt from the filter list", priority = 4)
    void addBlueTShirt() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(120));
        Actions actions = new Actions(driver);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='block_top_menu']/ul/child::li[3]"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("layered_id_attribute_group_14"))).click();

        String expectedResult = "Enabled filters:\nColor: Blue";
        String actualResult = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("enabled_filters"))).getText();
        Assert.assertEquals(actualResult, expectedResult);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("product-container")));
        actions.moveToElement(driver.findElement(By.className("product-container"))).perform();
        driver.findElement(By.id("color_2")).click();
        driver.findElement(By.name("Submit")).click();
    }

    @Test(description = "Now checkout and select the payment process ‘Payment by check’", priority = 5)
    void makePayment() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(120));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[title='Proceed to checkout']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart_navigation [title='Proceed to checkout']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("processAddress"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("uniform-cgv"))).click();
        driver.findElement(By.name("processCarrier")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[title='Pay by check.']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#cart_navigation [type='submit']"))).click();

        String expectedResult = "Your order on My Store is complete.";
        String actualResult = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert.alert-success"))).getText();
        Assert.assertEquals(actualResult, expectedResult);
    }

    @Test(description = "Sign out from the account", priority = 6)
    void signOut() {
        driver.findElement(By.className("logout")).click();
    }

    @Test(description = "Run the above cycle for another user.", priority = 7)
    void repeatCycle() {
        login();
        addCasualDress();
        addBlueTShirt();
        makePayment();
        signOut();
    }

    @AfterSuite
    public void teardown() {
        driver.quit();
    }
}
