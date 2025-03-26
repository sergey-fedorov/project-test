package teamcity.ui;

import api.enums.Endpoint;
import api.models.Project;
import com.codeborne.selenide.Condition;
import org.testng.annotations.Test;
import ui.pages.CreateProjectPage;
import ui.pages.ProjectPage;
import ui.pages.ProjectsPage;

public class CreateProjectTest extends BaseUiTest {

    private static final String REPO_URL = "https://github.com/AlexPshe/spring-core-for-qa";

    @Test
    public void userCreatesProject() {
        // подготовка окружения
        loginAs(testData.getUser());

        // взаимодействие с UI
        CreateProjectPage.open("_Root")
                .createForm(REPO_URL)
                .setupProject(testData.getProject().getName(), testData.getBuildType().getName());

        // проверка состояния API
        // (корректность отправки данных с UI на API)
        var createdProject = checkedRequesterAuthBySuperUser.<Project>getRequest(Endpoint.PROJECTS).read("name:" + testData.getProject().getName());
        softAssert.assertNotNull(createdProject);

        // проверка состояния UI
        // (корректность считывания данных и отображение данных на UI)
        ProjectPage.open(createdProject.getId())
                .title.shouldHave(Condition.exactText(testData.getProject().getName()));

        var foundProjects = ProjectsPage.open()
                .getProjects().stream()
                .anyMatch(project -> project.getName().equals(testData.getProject().getName()));

        softAssert.assertTrue(foundProjects);
    }
}
