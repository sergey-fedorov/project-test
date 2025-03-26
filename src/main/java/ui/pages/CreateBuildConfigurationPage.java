package ui.pages;

import api.models.BuildType;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class CreateBuildConfigurationPage extends BasePage {
    private static final String URL = "/admin/createObjectMenu.html?projectId=%s&showMode=createBuildTypeMenu";

    private final SelenideElement manuallyOption = $("[href='#createManually']");
    private final SelenideElement nameInput = $("#buildTypeName");
    private final SelenideElement idInput = $("#buildTypeExternalId");
    public final SelenideElement idInputError = $("#error_buildTypeExternalId");
    private final SelenideElement createButton = $("[name=createBuildType]");

    public static CreateBuildConfigurationPage open(String projectId) {
        return Selenide.open(URL.formatted(projectId), CreateBuildConfigurationPage.class);
    }

    public void createBuildConfigurationManually(BuildType buildType) {
        manuallyOption.click();
        nameInput.val(buildType.getName());
        idInput.click();
        idInput.val(buildType.getId());
        createButton.click();
    }

}
