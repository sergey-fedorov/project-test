package api;

import api.enums.Endpoint;
import api.models.*;
import api.requests.checked.CheckedRequests;
import api.requests.unchecked.UncheckedRequests;
import api.spec.ResponseSpecifications;
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
        project.setName("existing_name");
        checkedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).create(project);
        uncheckedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).create(project)
                .then().assertThat().spec(ResponseSpecifications.checkProjectWithNameAlreadyExist(project.getName()));
    }

    @Test
    public void projectShouldNotBeCreatedIfNameIsInvalid() {
        var project = testData.getProject();
        project.setName("");
        uncheckedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).create(project)
                .then().assertThat().spec(ResponseSpecifications.checkProjectCannotBeEmpty());
        uncheckedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).read(project.getId())
                .then().assertThat().spec(ResponseSpecifications.checkProjectNotFoundById(project.getId()));
        // TODO: Other "name" negative test cases
    }

    @Test
    public void projectShouldNotBeCreatedIfIdIsInvalid() {
        var project = testData.getProject();
        project.setId("2test");
        uncheckedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).create(project)
                .then().assertThat().spec(ResponseSpecifications.checkProjectIdIsInvalid(project.getId()));
        uncheckedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).read(project.getId())
                .then().assertThat().spec(ResponseSpecifications.checkProjectNotFoundById(project.getId()));
        // TODO: Other "id" negative test cases
    }

    @Test
    public void projectShouldNotBeCreatedIfParentProjectIdDoesNotExist() {
        String notExistingProjectId = "not_existing";
        var project = testData.getProject();
        project.setParentProject(ParentProject.builder().locator("id:" + notExistingProjectId).build());
        uncheckedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).create(project)
                .then().assertThat().spec(ResponseSpecifications.checkProjectNotFoundById(notExistingProjectId));
        uncheckedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).read(project.getId())
                .then().assertThat().spec(ResponseSpecifications.checkProjectNotFoundById(project.getId()));
    }

    @Test
    public void projectShouldNotBeCreatedIfSourceProjectIdDoesNotExist() {
        String notExistingProjectId = "not_existing";
        var project = testData.getProject();
        project.setSourceProject(SourceProject.builder().locator("id:" + notExistingProjectId).build());
        uncheckedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).create(project)
                .then().assertThat().spec(ResponseSpecifications.checkProjectNotFoundById(notExistingProjectId));
        uncheckedRequesterAuthByUser.getRequest(Endpoint.PROJECTS).read(project.getId())
                .then().assertThat().spec(ResponseSpecifications.checkProjectNotFoundById(project.getId()));
    }


}
