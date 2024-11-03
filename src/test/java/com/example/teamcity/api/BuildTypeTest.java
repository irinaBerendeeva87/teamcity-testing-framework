package com.example.teamcity.api;

import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.Roles;
import com.example.teamcity.api.models.User;
import com.example.teamcity.api.requests.checked.CheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;

@Test(groups = {"Regressions"})
public class BuildTypeTest extends BaseApiTest {
    @Test(description = "User should be able to create build type", groups = {"Positive", "CRUD"})
    public void userCreatesBuildTypeTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read("id:"+ testData.getBuildType().getId());

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }

    @Test(description = "User should not be able to create two build type with the same id", groups = {"Negative", "CRUD"})
    public void userCreatesTwoBuildTypesWithTheSameIdTest() {
        var buildTypeWithSameId = generate(Arrays.asList(testData.getProject()), BuildType.class, testData.getBuildType().getId() );

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
        new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                .create(buildTypeWithSameId)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("The build configuration / template ID \"%s\" is already used by another configuration or template".formatted(testData.getBuildType().getId())));
    }

    @Test(description = "Project admin should be able to create build type for their project", groups = {"Positive", "Roles "})
    public void projectsAdminCreatesBuildTypeTest() {
        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());
        testData.getUser().setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + testData.getProject().getId()));

        superUserCheckRequests.<User>getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read("id:"+testData.getBuildType().getId());

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }

    @Test(description = "Project admin should not be able to create build type for another user project", groups = {"Negative", "Roles"})
    public void projectsAdminCreatesBuildTypeForAnotherUserProjectTest() {
        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());
        var anotherProject = superUserCheckRequests.<Project>getRequest (PROJECTS) .create(generate(Project.class));

        testData.getUser().setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + testData.getProject().getId()));

        superUserCheckRequests.<User>getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var anotherUser = generate (User.class);
        anotherUser.setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + anotherProject.getId()));

        superUserCheckRequests.<User>getRequest(USERS).create(anotherUser);
        String projectId = testData.getProject().getId();
        var expectedBody = """
                Responding with error, status code: 403 (Forbidden).
                Details: jetbrains.buildServer.serverSide.auth.AccessDeniedException: You do not have enough permissions to edit project with id: %s
                Access denied. Check the user has enough permissions to perform the operation.""".formatted(projectId);
        new UncheckedBase(Specifications.authSpec(anotherUser), BUILD_TYPES)
                .create(testData.getBuildType())
                .then ().assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.containsString(expectedBody));
    }
}
