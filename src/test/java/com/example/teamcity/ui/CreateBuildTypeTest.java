package com.example.teamcity.ui;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.BuildTypes;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.ui.pages.admin.CreateBuildTypePage;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Selenide.webdriver;
import static com.example.teamcity.api.enums.Endpoint.PROJECT_BUILD_TYPES;


@Test(groups = {"Regressions"})
public class CreateBuildTypeTest extends BaseUiTest {
    private static final String EXPECTED_URL_SEGMENT = "/admin/discoverRunners.html?init=1&id=buildType:";
    private static final String REPO_URL = "https://github.com/irinaBerendeeva87/test";

    private Project createdProject;

    @Test(description = "User should  be able to create build configuration with build configuration name", groups = {"Positive"})
    public void userCreatesBuildTypeWithBuildTypeName() {

        createProject();
        var getProjectId = testData.getProject().getId();
        loginAs(testData.getUser());

        CreateBuildTypePage.open(getProjectId)
                .createForm(REPO_URL)
                .setupBuildType(testData.getBuildType().getName());

        var createdBuildType = superUserCheckRequests.<BuildType>getRequest(Endpoint.BUILD_TYPES).read("name:" + testData.getBuildType().getName());
        softy.assertNotNull(createdBuildType);
        String currentUrl = webdriver().driver().url();

        String expectedBuildTypeId = createdBuildType.getId();
        String expectedUrlSegmentWithId = EXPECTED_URL_SEGMENT + expectedBuildTypeId;

        softy.assertTrue(currentUrl.contains(expectedUrlSegmentWithId),
                "Current URL does not contain the expected segment with buildTypeId: " + expectedBuildTypeId);
    }

    @Test(description = "User should not be able to create build configuration without build configuration name", groups = {"Negative"})
    public void userCreatesBuildTypeWithoutBuildTypeName() {
        var initialBuildTypesCount = superUserCheckRequests.<BuildTypes>getRequest(PROJECT_BUILD_TYPES)
                .read(createdProject.getId() + "/buildTypes").getCount();

        createProject();
        var createdProject = superUserCheckRequests.<Project>getRequest(Endpoint.PROJECTS)
                .read("name:" + testData.getProject().getName());

        var getProjectId = testData.getProject().getId();
        loginAs(testData.getUser());

        CreateBuildTypePage.open(getProjectId)
                .createForm(REPO_URL)
                .setupBuildTypeWithOutName();

        String expectedErrorMessage = "Build configuration name must not be empty";
        String actualErrorMessage = CreateBuildTypePage.getErrorMessage();
        softy.assertEquals(actualErrorMessage, expectedErrorMessage, "Error message does not match the expected message.");

        var newBuildTypesCount = superUserCheckRequests.<BuildTypes>getRequest(PROJECT_BUILD_TYPES)
                .read(createdProject.getId() + "/buildTypes").getCount();
        softy.assertEquals(newBuildTypesCount, initialBuildTypesCount);
    }
}
