package com.example.teamcity.ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.elements.ProjectElement;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProjectsPage extends BasePage {
    private static final String PROJECTS_URL = "/favorite/projects";

    private SelenideElement header = $(".MainPanel__router--gF > div");
    private ElementsCollection projectsElements = $$("div[class*='Subproject__container']");

    public ProjectsPage() {
        header.shouldBe(Condition.visible, BASE_WAITING);
    }

    public static ProjectsPage open() {
        return Selenide.open(PROJECTS_URL, ProjectsPage.class);
    }

    //ElementCollection -> List<ProjectElement>
    // UI Elements -> list<Objects>
    //ElementCollection -> List<BasePageElement>

    public List<ProjectElement> getProjects() {
        return generatePageElements(projectsElements, ProjectElement::new);
    }
}
