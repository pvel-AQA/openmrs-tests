package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class PickLocationPage extends BasePage<PickLocationPage> {
    @Override
    public String url() {
        return "/login/location";
    }

    private final SelenideElement welcomeText = $("p.-esm-login__location-picker__welcomeTitle___iI\\+4Z");
    private final SelenideElement outpatientLocationRadioButton = $(By.xpath("//div[label[@for='44c3efb0-2583-4c80-a79e-1f756a03c0a1'] and input[@type='radio']]/input"));
    private final SelenideElement confirmButton = $(By.xpath("//button/span[text()='Confirm']"));

    public ServiceQueuesPage pickOutpatientLocationAndConfirm() {
        outpatientLocationRadioButton.shouldBe(Condition.visible);
        outpatientLocationRadioButton.click();

        confirmButton.shouldBe(Condition.visible);
        confirmButton.click();

        return getPage(ServiceQueuesPage.class);
    }
}
