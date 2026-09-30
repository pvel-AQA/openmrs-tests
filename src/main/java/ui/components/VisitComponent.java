package ui.components;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ui.pages.PatientSummaryPage;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class VisitComponent extends BaseComponent {
    public static final String UBUNTU_LOCATION_NAME = "Ubuntu Hospital";
    private static final int EXPECTED_COLLECTION_SIZE = 0;

    private final SelenideElement newButton = $(By.xpath("//div/button/span[@class='cds--content-switcher__label' and text()='New']"));
    private final SelenideElement ongoingButton = $(By.xpath("//div/button/span[@class='cds--content-switcher__label' and text()='Ongoin']"));
    private final SelenideElement inThePastButton = $(By.xpath("//div/button/span[@class='cds--content-switcher__label' and text()='In the past']"));
    private final SelenideElement selectVisitLocationDropdown = $(By.xpath("//div[text()='Visit location']/..//input[@class='cds--text-input']"));
    private final ElementsCollection selectVisitLocationDropdownList = $$(By.xpath("//input[@id='location']/../../ul"));
    private final SelenideElement selectVisitLocationSelectButton = $(By.xpath("//input[@id='location']/../button[2]"));
    private final ElementsCollection visitTypeFieldSet = $$(By.xpath("//section/h1[text()='Visit Type']/..//fieldset[@class]//span[@class='cds--radio-button__label-text']"));
    private final SelenideElement visitTypeSearchTextbox = $(By.xpath("//input[@placeholder='Search for a visit type']"));
    private final SelenideElement startVisitButton = $(By.xpath("//button/span[text()='Start visit']"));


    public VisitComponent(SelenideElement self) {
        super(self);
    }

    @Override
    protected SelenideElement getSelf() {
        return self;
    }

    public VisitComponent clickOnNewButton() {
        newButton.shouldBe(Condition.visible);
        newButton.click();

        return this;
    }

    public VisitComponent selectVisitLocation(String location) {
        selectVisitLocationDropdown.shouldBe(Condition.visible);
        selectVisitLocationSelectButton.click();
        selectVisitLocationDropdownList.findBy(Condition.exactText(location)).click();

        return this;
    }

    public VisitComponent selectVisitType(String type) {
        visitTypeSearchTextbox.shouldBe(Condition.visible);
        visitTypeFieldSet.shouldHave(CollectionCondition.sizeGreaterThan(EXPECTED_COLLECTION_SIZE));
        visitTypeFieldSet.findBy(Condition.exactText(type)).click();

        return this;
    }

    public VisitComponent clickStartVisitButton() {
        startVisitButton.shouldBe(Condition.visible);
        startVisitButton.click();

        return this;
    }

    public PatientSummaryPage startVisit(String visitLocation, String visitType) {
        clickOnNewButton();
        selectVisitLocation(visitLocation);
        selectVisitType(visitType);
        clickStartVisitButton();

        return getPage(PatientSummaryPage.class);
    }
}
