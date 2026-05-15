package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import ui.components.Header;

import static com.codeborne.selenide.Selenide.$;

public class ServiceQueuesPage extends AuthBasePage<ServiceQueuesPage> {
    public Header header = new Header();
    private final SelenideElement clinicName = $(Selectors.byXpath("//*[@data-testid='patient-queue-header']//p[text()='Clinic']"));
    private final SelenideElement tabName = $(Selectors.byXpath("//*[@data-testid='patient-queue-header']//p[text()='Service queues']"));

    @Override
    public String url() {
        return "/home/service-queues";
    }

    public Boolean atPage() {
        return clinicName.getText().equals("Clinic") && tabName.getText().equals("Service queues");
    }

    public SearchResultsPage pressEnterButton() {
        header.searchTextInputField.shouldBe(Condition.visible).click();
        header.searchTextInputField.pressEnter();
        Selenide.sleep(5000);

        return getPage(SearchResultsPage.class);
    }

    public SearchResultsPage clickSearchButton() {
        header.searchButton.shouldBe(Condition.visible).click();
        Selenide.sleep(5000);
        return new SearchResultsPage();
    }
}
