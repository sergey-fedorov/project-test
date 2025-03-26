package ui.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class EditBuildConfigurationPage extends BasePage {
    private static final String URL = "/admin/editBuild.html?id=buildType:%s";

    public final SelenideElement nameInput = $("#name");
    public final SelenideElement idInput = $("#externalId");
    public final SelenideElement projectBreadcrumb = $("#restNavigation li:nth-of-type(2) a");

    public static EditBuildConfigurationPage open(String buildId) {
        return Selenide.open(URL.formatted(buildId), EditBuildConfigurationPage.class);
    }

}
