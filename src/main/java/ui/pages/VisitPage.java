package ui.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;

public class VisitPage extends BasePage<VisitPage> {

    private String patientUuid;

    public VisitPage() {
    }

    public VisitPage(String patientUuid) {
        this.patientUuid = patientUuid;
    }

    @Override
    public String url() {
        return "/patient/" + patientUuid + "/chart/Patient%20Summary";
    }

    // ACTIONS MENU

    private final ElementsCollection actionsMenuItems =
            $$("div[role='menu'] button[role='menuitem']");

    private final SelenideElement actionButton =
            $("button.cds--overflow-menu__trigger");

    private final SelenideElement showMoreButton =
            $(By.xpath("//button[text()='Show more']"));

    private final SelenideElement addVisitOption =
            $(By.xpath("//div[text()='Add visit']"));

    //VISIT MODAL

    private final SelenideElement startVisitHeader =
            $(By.xpath("//header[@aria-label='Workspace header']//span[contains(text(),'Start a visit')]"));

    private final SelenideElement theVisitIsLegend =
            $(By.xpath("//legend[text()='The visit is']"));

    //TABS

    private final SelenideElement newTab =
            $(By.xpath("//button[.//span[text()='New']]"));

    private final SelenideElement ongoingTab =
            $(By.xpath("//button[.//span[text()='Ongoing']]"));

    private final SelenideElement inThePastTab =
            $(By.xpath("//button[.//span[text()='In the past']]"));

    //LOCATION

    private final SelenideElement visitLocationText =
            $(By.xpath("//div[text()='Visit location']"));

    private final SelenideElement selectLocationLabel =
            $(By.xpath("//label[text()='Select a location']"));

    private final SelenideElement locationInput =
            $(By.xpath("//input[@id='location']"));

    private final SelenideElement locationDropdownButton =
            $("button[aria-haspopup='listbox']");

    private final SelenideElement locationListbox =
            $(By.xpath("//ul[@role='listbox']"));

    private final SelenideElement ubuntuHospitalLocation =
            $(By.xpath("(//div[text()='Ubuntu Hospital'])[1]"));

    //VISIT TYPE

    private final SelenideElement visitTypeText =
            $(By.xpath("//*[text()='Visit Type']"));

    private final SelenideElement searchVisitTypeInput =
            $(By.xpath("//input[@placeholder='Search for a visit type']"));

    private final SelenideElement facilityVisitOption =
            $(By.xpath("//label[.//span[text()='Facility Visit']]"));

    private final SelenideElement homeVisitOption =
            $(By.xpath("//label[.//span[text()='Home Visit']]"));

    private final SelenideElement opdVisitOption =
            $(By.xpath("//label[.//span[text()='OPD Visit']]"));

    private final SelenideElement offlineVisitOption =
            $(By.xpath("//label[.//span[text()='Offline Visit']]"));

    private final SelenideElement groupSessionOption =
            $(By.xpath("//span[text()='Group Session']/parent::label"));

    //ACTION FLOW

    public VisitPage waitPatientSummaryLoaded() {
        $("body")
                .shouldBe(Condition.visible, Duration.ofSeconds(30));

        sleep(1500); // SPA stabilisation

        return this;
    }

    public VisitPage openActionsMenu() {
        actionButton
                .shouldBe(Condition.visible, Duration.ofSeconds(30))
                .click();

        $("div[role='menu']")
                .shouldBe(Condition.visible, Duration.ofSeconds(10));

        return this;
    }

    public VisitPage clickShowMore() {
        showMoreButton
                .shouldBe(Condition.visible, Duration.ofSeconds(10))
                .click();

        return this;
    }

    public VisitPage selectAddVisit() {
        actionsMenuItems
                .shouldBe(CollectionCondition.sizeGreaterThan(0),
                        Duration.ofSeconds(10));

        for (SelenideElement item : actionsMenuItems) {

            if (item.getText().equalsIgnoreCase("Add visit")) {

                item.shouldBe(Condition.visible).click();
                return this;
            }
        }

        throw new AssertionError("Add visit not found in Actions menu");
    }

    public VisitPage waitStartVisitModal() {
        startVisitHeader
                .shouldBe(Condition.visible, Duration.ofSeconds(25));

        return this;
    }

    //VISIT STEPS

    public VisitPage selectVisitTab(String tabName) {
        switch (tabName.toLowerCase()) {

            case "new":
                newTab.shouldBe(Condition.visible).click();
                break;

            case "ongoing":
                ongoingTab.shouldBe(Condition.visible).click();
                break;

            case "in the past":
                inThePastTab.shouldBe(Condition.visible).click();
                break;
        }

        return this;
    }

    public VisitPage selectUbuntuHospitalLocation() {
        locationInput
                .shouldBe(Condition.visible)
                .click();

        ubuntuHospitalLocation
                .shouldBe(Condition.visible, Duration.ofSeconds(10))
                .click();

        return this;
    }

    public VisitPage selectVisitType(String visitTypeName) {
        searchVisitTypeInput
                .shouldBe(Condition.visible)
                .setValue(visitTypeName);

        switch (visitTypeName.toLowerCase()) {

            case "facility visit":
                facilityVisitOption.shouldBe(Condition.visible).click();
                break;

            case "home visit":
                homeVisitOption.shouldBe(Condition.visible).click();
                break;

            case "opd visit":
                opdVisitOption.shouldBe(Condition.visible).click();
                break;

            case "offline visit":
                offlineVisitOption.shouldBe(Condition.visible).click();
                break;

            case "group session":
                groupSessionOption.shouldBe(Condition.visible).click();
                break;
        }

        return this;
    }

    //CHECKS

    public VisitPage checkStartVisitHeaderIsVisible() {
        startVisitHeader.shouldBe(Condition.visible, Duration.ofSeconds(20));
        return this;
    }

    public VisitPage checkTheVisitIsLegendIsVisible() {
        theVisitIsLegend.shouldBe(Condition.visible);
        return this;
    }

    public VisitPage checkVisitLocationTextIsVisible() {
        visitLocationText.shouldBe(Condition.visible);
        return this;
    }

    public VisitPage checkSelectLocationLabelIsVisible() {
        selectLocationLabel.shouldBe(Condition.visible);
        return this;
    }

    public VisitPage checkVisitTypeTextIsVisible() {
        visitTypeText.shouldBe(Condition.visible);
        return this;
    }

    public VisitPage checkNewTabIsSelected() {
        newTab.shouldBe(Condition.visible);
        return this;
    }
}


