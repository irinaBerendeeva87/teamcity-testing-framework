 package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class CreateBuildTypePage extends CreateBasePage {
    private static final String BUILD_SHOW_MODE = "createBuildTypeMenu";

    private SelenideElement buildTypeNameInput = $("#buildTypeName");
    private static SelenideElement spanErrorBuildTypeName = $("#error_buildTypeName");

    public static CreateBuildTypePage open(String buildTypeId) {
        return Selenide.open(CREATE_URL.formatted(buildTypeId, BUILD_SHOW_MODE), CreateBuildTypePage.class);
    }

    public CreateBuildTypePage createForm(String url) {
        baseCreateForm(url);
        return this;
    }

    public void setupBuildType(String buildTypeName) {
        buildTypeNameInput.setValue(buildTypeName);
        proceedButton.click();
    }

    public void setupBuildTypeWithOutName() {
        buildTypeNameInput.clear();
        proceedButton.click();
    }

    public static String getErrorMessage() {
        return spanErrorBuildTypeName.getText();
    }
}
