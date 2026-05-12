package ui.pages;

import com.codeborne.selenide.Selectors;
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

    @Override
    public Boolean atPage() {
        if (clinicName.getText().equals("Clinic") && tabName.getText().equals("Service queues")){
            return true;
        } else {
            return false;
        }
    }
}
