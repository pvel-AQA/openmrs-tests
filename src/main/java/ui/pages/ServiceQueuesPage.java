package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import ui.components.Header;

import static com.codeborne.selenide.Selenide.$;

public class ServiceQueuesPage extends AuthBasePage<ServiceQueuesPage> {
    private final SelenideElement clinicName = $(Selectors.byXpath("//*[@data-testid='patient-queue-header']//p[text()='Clinic']"));
    private final SelenideElement tabName = $(Selectors.byXpath("//*[@data-testid='patient-queue-header']//p[text()='Service queues']"));

    @Override
    public String url() {
        return "/home/service-queues";
    }

    public Boolean pageIsReady() {
        return clinicName.getText().equals("Clinic") && tabName.getText().equals("Service queues");
    }

    public SearchResultsPage pressEnterButton() {
        header.searchTextInputField.shouldBe(Condition.visible).click();
        header.searchResultsCount.click();
        header.searchTextInputField.click();
        header.searchTextInputField.pressEnter();

        return getPage(SearchResultsPage.class);
    }

    public SearchResultsPage clickSearchButton() {
        header.searchResultsCount.click();
        header.searchButton.shouldBe(Condition.visible).click();

        return getPage(SearchResultsPage.class);
    }
}
