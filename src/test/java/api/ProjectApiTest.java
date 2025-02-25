package api;

import api.enums.Endpoint;
import api.models.*;
import api.requests.checked.CheckedRequests;
import api.requests.unchecked.UncheckedRequests;
import api.spec.Specifications;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;

import static api.data.TestDataGenerator.generate;

public class ProjectApiTest extends BaseApiTest {

    CheckedRequests checkedRequesterAuthByUser;
    UncheckedRequests uncheckedRequesterAuthByUser;

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        var user = testData.getUser();
        checkedRequesterAuthBySuperUser.getRequest(Endpoint.USERS).create(user);

        checkedRequesterAuthByUser = new CheckedRequests(Specifications.authSpec(user));
        uncheckedRequesterAuthByUser = new UncheckedRequests(Specifications.authSpec(user));
    }

    @Test
    public void projectShouldBeCreatedSuccessfully() {
        var project = testData.getProject();
        checkedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).create(project);

        var createdProjectResponse = checkedRequesterAuthByUser.<Project>getRequest(Endpoint.PROJECTS).read(project.getId());

        softAssert.assertEquals(createdProjectResponse.getName(), project.getName(), "Project is not correct");
    }

    @Test
    public void projectWithParentProjectShouldBeCreatedSuccessfully() {
        var parentProject = generate(Project.class);
        checkedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).create(parentProject);

        var project = generate(Project.class);
        project.setParentProject(ParentProject.builder().locator("id:" + parentProject.getId()).build());
        checkedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).create(project);

        var createdProjectResponse = checkedRequesterAuthByUser.<Project>getRequest(Endpoint.PROJECTS).read(project.getId());

        softAssert.assertEquals(createdProjectResponse.getName(), project.getName(), "Project is not correct");
        softAssert.assertEquals(createdProjectResponse.getParentProject().getId(), parentProject.getId(), "Parent project name is not correct");
    }

    @Test
    public void projectWithSourceProjectShouldBeCreatedSuccessfully() {
        var sourceProject = generate(Project.class);
        checkedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).create(sourceProject);

        var sourceProjectBuildType = generate(Arrays.asList(sourceProject), BuildType.class);
        checkedRequesterAuthByUser.getRequest(Endpoint.BUILD_TYPES).create(sourceProjectBuildType);

        var project = generate(Project.class);
        project.setSourceProject(SourceProject.builder().locator("id:" + sourceProject.getId()).build());
        checkedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).create(project);

        var createdProjectResponse = checkedRequesterAuthByUser.<Project>getRequest(Endpoint.PROJECTS).read(project.getId());

        softAssert.assertEquals(createdProjectResponse.getName(), project.getName(), "Project is not correct");
        softAssert.assertEquals(createdProjectResponse.getBuildTypes().getCount(), 1, "Count of build types is not correct");
    }

    /* Negative tests */

    @Test
    public void projectShouldNotBeCreatedIfNameAlreadyExist() {
        var project = testData.getProject();
        project.setName("test");
        uncheckedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).create(project);
        Response createdProjectResponse = uncheckedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).create(project);

        softAssert.assertEquals(createdProjectResponse.statusCode(), 400);
        softAssert.assertTrue(createdProjectResponse.getBody().asString().contains("Project with this name already exists: %s".formatted(project.getName())));
    }

    @Test
    public void projectShouldNotBeCreatedIfNameIsInvalid() {
        var project = testData.getProject();
        project.setName("");
        Response createdProjectResponse = uncheckedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).create(project);
        Response getProjectResponse = uncheckedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).read(project.getId());

        softAssert.assertNotEquals(getProjectResponse.statusCode(), 200, "Project should not exist");
        softAssert.assertEquals(createdProjectResponse.statusCode(), 400);
        softAssert.assertTrue(createdProjectResponse.getBody().asString().contains("Project name cannot be empty."));
        // TODO: Other "name" negative test cases
    }

    @Test
    public void projectShouldNotBeCreatedIfIdIsInvalid() {
        var project = testData.getProject();
        project.setId("2test");
        Response createdProjectResponse = uncheckedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).create(project);
        Response getProjectResponse = uncheckedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).read(project.getId());

        softAssert.assertNotEquals(getProjectResponse.statusCode(), 200, "Project should not exist");
        softAssert.assertEquals(createdProjectResponse.statusCode(), 500);
        softAssert.assertTrue(createdProjectResponse.getBody().asString().contains("Project ID \"%s\" is invalid: starts with non-letter character '%s'.".formatted(project.getId(), project.getId().charAt(0))));
        // TODO: Other "id" negative test cases
    }

    @Test
    public void projectShouldNotBeCreatedIfParentProjectDoesNotExist() {
        String notExisting = "not_existing";
        var project = testData.getProject();
        project.setParentProject(ParentProject.builder().locator(notExisting).build());
        Response createdProjectResponse = uncheckedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).create(project);
        Response getProjectResponse = uncheckedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).read(project.getId());

        softAssert.assertNotEquals(getProjectResponse.statusCode(), 200, "Project should not exist");
        softAssert.assertEquals(createdProjectResponse.statusCode(), 404);
        softAssert.assertTrue(createdProjectResponse.getBody().asString().contains("No project found by name or internal/external id '%s'.".formatted(notExisting)));
    }

    @Test
    public void projectShouldNotBeCreatedIfSourceProjectDoesNotExist() {
        String notExisting = "not_existing";
        var project = testData.getProject();
        project.setSourceProject(SourceProject.builder().locator(notExisting).build());
        Response createdProjectResponse = uncheckedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).create(project);
        Response getProjectResponse = uncheckedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).read(project.getId());

        softAssert.assertNotEquals(getProjectResponse.statusCode(), 200, "Project should not exist");
        softAssert.assertEquals(createdProjectResponse.statusCode(), 404);
        softAssert.assertTrue(createdProjectResponse.getBody().asString().contains("No project found by name or internal/external id '%s'.".formatted(notExisting)));
    }


}
