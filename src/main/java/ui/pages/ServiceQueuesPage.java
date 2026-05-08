package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class ServiceQueuesPage extends BasePage<ServiceQueuesPage> {
    @Override
    public String url() {
        return "/home/service-queues";
    }

    private SelenideElement headerClinicText = $("div[data-testid='patient-queue-header'] p:nth-child(1)");



}
