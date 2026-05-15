package ui.pages;

import api.models.ui.VisitTab;
import api.requests.skeleton.requesters.VisitTypeEnum;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class VisitPage extends BasePage<VisitPage> {
    @Override
    public String url() {
        return "/patient/%s/chart/Patient%%20Summary";
    }

    private final ElementsCollection actionsMenuItems = $$("div[role='menu'] button[role='menuitem']");
    private final SelenideElement actionButton = $("button.cds--overflow-menu__trigger");
    private final SelenideElement showMoreButton = $(By.xpath("//button[text()='Show more']"));
    private final SelenideElement addVisitOption = $(By.xpath("//div[text()='Add visit']"));
    private final SelenideElement startVisitHeader = $(By.xpath("//header[@aria-label='Workspace header']//span[contains(text(),'Start a visit')]"));
    private final SelenideElement theVisitIsLegend = $(By.xpath("//legend[text()='The visit is']"));
    private final SelenideElement newTab = $(By.xpath("//button[.//span[text()='New']]"));
    private final SelenideElement ongoingTab = $(By.xpath("//button[.//span[text()='Ongoing']]"));
    private final SelenideElement inThePastTab = $(By.xpath("//button[.//span[text()='In the past']]"));
    private final SelenideElement startVisitSubmitButton = $(By.xpath("//button[@type='submit']//span[text()='Start visit']"));
    private final SelenideElement activeVisitTag = $(By.xpath("//div[contains(@class,'cds--tag') and .//span[contains(text(),'Active Visit')]]"));
    private final SelenideElement visitLocationText = $(By.xpath("//div[text()='Visit location']"));
    private final SelenideElement selectLocationLabel = $(By.xpath("//label[text()='Select a location']"));
    private final SelenideElement locationInput = $(By.xpath("//input[@id='location']"));
    private final SelenideElement locationDropdownButton = $("button[aria-haspopup='listbox']");
    private final SelenideElement locationListbox = $(By.xpath("//ul[@role='listbox']"));
    private final SelenideElement ubuntuHospitalLocation = $(By.xpath("(//div[text()='Ubuntu Hospital'])[1]"));
    private final SelenideElement visitTypeText = $(By.xpath("//*[text()='Visit Type']"));
    private final SelenideElement searchVisitTypeInput = $(By.xpath("//input[@placeholder='Search for a visit type']"));
    private final SelenideElement facilityVisitOption = $(By.xpath("//label[.//span[text()='Facility Visit']]"));
    private final SelenideElement homeVisitOption = $(By.xpath("//label[.//span[text()='Home Visit']]"));
    private final SelenideElement opdVisitOption = $(By.xpath("//label[.//span[text()='OPD Visit']]"));
    private final SelenideElement offlineVisitOption = $(By.xpath("//label[.//span[text()='Offline Visit']]"));
    private final SelenideElement groupSessionOption = $(By.xpath("//span[text()='Group Session']/parent::label"));

    public VisitPage waitPatientSummaryLoaded() {
        $("body")
                .shouldBe(Condition.visible);

        return this;
    }

    public VisitPage openActionsMenu() {
        actionButton
                .shouldBe(Condition.visible)
                .click();

        $("div[role='menu']")
                .shouldBe(Condition.visible);

        return this;
    }

    public VisitPage clickShowMore() {
        showMoreButton
                .shouldBe(Condition.visible)
                .click();

        return this;
    }

    public VisitPage selectAddVisit() {
        actionsMenuItems
                .shouldBe(CollectionCondition.sizeGreaterThan(0));

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
                .shouldBe(Condition.visible);

        return this;
    }

    public VisitPage selectVisitTab(VisitTab tab) {
        switch (tab) {
            case NEW:
                newTab.shouldBe(Condition.visible).click();
                break;

            case ONGOING:
                ongoingTab.shouldBe(Condition.visible).click();
                break;

            case IN_THE_PAST:
                inThePastTab.shouldBe(Condition.visible).click();
                break;

            default:
                throw new IllegalArgumentException("Unknown tab: " + tab.getTabName());
        }

        return this;
    }

    public VisitPage selectUbuntuHospitalLocation() {
        locationInput
                .shouldBe(Condition.visible)
                .click();

        ubuntuHospitalLocation
                .shouldBe(Condition.visible)
                .click();

        return this;
    }

    public VisitPage selectVisitType(VisitTypeEnum visitType) {
        String visitTypeDisplayName = visitType.getDisplayName();

        searchVisitTypeInput
                .shouldBe(Condition.visible)
                .sendKeys(visitTypeDisplayName);

        switch (visitType) {
            case FACILITY_VISIT:
                facilityVisitOption.shouldBe(Condition.visible).click();
                break;

            case HOME_VISIT:
                homeVisitOption.shouldBe(Condition.visible).click();
                break;

            case OPD_VISIT:
                opdVisitOption.shouldBe(Condition.visible).click();
                break;

            case OFFLINE_VISIT:
                offlineVisitOption.shouldBe(Condition.visible).click();
                break;

            case GROUP_SESSION:
                groupSessionOption.shouldBe(Condition.visible).click();
                break;

            default:
                throw new IllegalArgumentException("Unknown visit type: " + visitType);
        }

        return this;
    }

    public VisitPage confirmStartVisit() {
        startVisitSubmitButton
                .shouldBe(Condition.visible)
                .shouldBe(Condition.enabled)
                .click();

        return this;
    }

    public VisitPage confirmStartVisitAndWaitForClose() {
        confirmStartVisit();
        startVisitHeader
                .shouldBe(Condition.disappear);

        return this;
    }

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

    public VisitPage checkActiveVisitIsStarted() {
        activeVisitTag.shouldBe(Condition.visible);

        assertThat(activeVisitTag.getText())
                .as("Active Visit tag text should be correct")
                .contains("Active Visit");

        return this;
    }
}


