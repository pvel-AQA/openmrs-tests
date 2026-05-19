package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class PickLocationPage extends BasePage<PickLocationPage> {
    public static final String WELCOME_ADMIN_TEXT = "Welcome Admin";

    @Override
    public String url() {
        return "/login/location";
    }

    public Boolean pageIsReady() {
        return outpatientLocationRadioButton.is(Condition.visible);
    }

    private final SelenideElement welcomeText = $("p.-esm-login__location-picker__welcomeTitle___iI\\+4Z");
    private final SelenideElement outpatientLocationRadioButton = $(Selectors.byText("Outpatient Clinic"));
    private final SelenideElement confirmButton = $(By.xpath("//button/span[text()='Confirm']"));
    private final SelenideElement rememberMyLocationCheckbox = $(".cds--checkbox-label-text");

    public ServiceQueuesPage pickOutpatientLocationAndConfirm() {
        outpatientLocationRadioButton.shouldBe(Condition.visible);
        outpatientLocationRadioButton.click();

        confirmButton.shouldBe(Condition.visible);
        confirmButton.click();

        return getPage(ServiceQueuesPage.class);
    }

    public ServiceQueuesPage pickOutpatientLocationClickRememberMyLocationAndConfirm() {
        outpatientLocationRadioButton.shouldBe(Condition.visible);
        outpatientLocationRadioButton.click();

        rememberMyLocationCheckbox.shouldBe(Condition.visible);
        rememberMyLocationCheckbox.click();

        confirmButton.shouldBe(Condition.visible);
        confirmButton.click();

        return getPage(ServiceQueuesPage.class);
    }
}
