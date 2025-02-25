package api;

import api.generators.TestDataStorage;
import api.models.TestData;
import api.requests.checked.CheckedRequests;
import api.spec.Specifications;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.asserts.SoftAssert;

public class BaseTest {
    protected SoftAssert softAssert;
    protected CheckedRequests checkedRequesterAuthBySuperUser = new CheckedRequests(Specifications.superUserSpec());
    protected TestData testData;


    @BeforeMethod(alwaysRun = true)
    public void beforeTestMethod() {
        softAssert = new SoftAssert();
        testData = TestData.generate();
    }

    @AfterMethod(alwaysRun = true)
    public void afterTestMethod() {
        softAssert.assertAll();
    }

    @AfterTest(alwaysRun = true)
    public void afterTest() {
        TestDataStorage.getStorage().deleteCreatedEntities();
    }

}
