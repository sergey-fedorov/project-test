package teamcity.ui;

import api.enums.Endpoint;
import api.models.BuildType;
import api.models.Project;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ui.pages.CreateBuildConfigurationPage;
import ui.pages.EditBuildConfigurationPage;
import ui.pages.EditVcsRootPage;

import static api.constants.Notifications.BUILD_CONFIGURATION_ID_FILED_ERROR;
import static api.constants.Notifications.BUILD_CONFIGURATION_SUCCESSFULLY_CREATED;
import static com.codeborne.selenide.Condition.*;

public class CreateBuildConfigurationTest extends BaseUiTest {

    CreateBuildConfigurationPage createBuildConfigurationPage = new CreateBuildConfigurationPage();
    EditVcsRootPage editVcsRootPage = new EditVcsRootPage();

    @BeforeMethod
    public void loginViaUI() {
        var testUser = testData.getUser();
        loginAs(testUser);
    }

    @Test
    public void userCreatesBuildConfigurationSuccessfully() {
        var buildType = testData.getBuildType();
        var project = testData.getProject();

        checkedRequesterAuthBySuperUser.getRequest(Endpoint.PROJECTS).create(project);

        CreateBuildConfigurationPage.open(project.getId()).createBuildConfigurationManually(buildType);
        editVcsRootPage.buildTypeCreatedMessage.shouldBe(visible)
                .shouldHave(exactText(BUILD_CONFIGURATION_SUCCESSFULLY_CREATED));

        // API verification
        Project createdProject = checkedRequesterAuthBySuperUser.<Project>getRequest(Endpoint.PROJECTS).read(project.getId());

        BuildType buildTypeActual = createdProject.getBuildTypes().getBuildType().stream()
                .filter(build -> build.getId().equals(buildType.getId())).findAny().orElseThrow();

        Assert.assertEquals(buildTypeActual.getId(), buildType.getId());
        Assert.assertEquals(buildTypeActual.getName(), buildType.getName());

        // UI verification
        EditBuildConfigurationPage editBuildConfigurationPage = EditBuildConfigurationPage.open(buildType.getId());
        editBuildConfigurationPage.projectBreadcrumb.shouldHave(exactText(project.getName()));
        editBuildConfigurationPage.idInput.shouldHave(exactValue(buildType.getId()));
        editBuildConfigurationPage.nameInput.shouldHave(exactValue(buildType.getName()));
    }

    @Test
    public void userShouldNotCreateBuildConfigurationWithInvalidId() {
        // Set wrong id
        var buildType = testData.getBuildType().setId("1");
        var project = testData.getProject();

        checkedRequesterAuthBySuperUser.getRequest(Endpoint.PROJECTS).create(project);

        CreateBuildConfigurationPage.open(project.getId()).createBuildConfigurationManually(buildType);

        // UI verification
        createBuildConfigurationPage.idInputError.shouldBe(visible)
                .shouldHave(exactText(BUILD_CONFIGURATION_ID_FILED_ERROR.formatted(buildType.getId())));

        // API verification
        Project createdProject = checkedRequesterAuthBySuperUser.<Project>getRequest(Endpoint.PROJECTS).read(project.getId());

        boolean doesBuildTypeExist = createdProject.getBuildTypes().getBuildType().stream()
                .anyMatch(build -> build.getId().equals(buildType.getId()));

        Assert.assertFalse(doesBuildTypeExist);
    }


}
