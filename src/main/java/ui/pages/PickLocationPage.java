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

    public PickLocationPage pageIsReady() {
        outpatientLocationRadioButton.shouldBe(Condition.visible);

        return this;
    }

    private final SelenideElement welcomeText = $("p.-esm-login__location-picker__welcomeTitle___iI\\+4Z");
    private final SelenideElement outpatientLocationRadioButton = $(Selectors.byText("Outpatient Clinic"));
    private final SelenideElement confirmButton = $(By.xpath("//button/span[text()='Confirm']"));
    private final SelenideElement rememberMyLocationCheckbox = $(".cds--checkbox-label-text");

    public PickLocationPage clinicLocationSelect() {
        outpatientLocationRadioButton.shouldBe(Condition.visible);
        outpatientLocationRadioButton.click();

        return this;
    }

    public ServiceQueuesPage clinicLocationConfirm() {
        confirmButton.shouldBe(Condition.visible);
        confirmButton.click();

        return getPage(ServiceQueuesPage.class);
    }

    public PickLocationPage clinicLocationClickRemember() {
        rememberMyLocationCheckbox.shouldBe(Condition.visible);
        rememberMyLocationCheckbox.click();

        return this;
    }

    public PickLocationPage confirmButtonDisabled() {
        confirmButton.shouldBe(Condition.visible);
        confirmButton.shouldBe(Condition.disabled);
        confirmButton.shouldNotBe(Condition.clickable);

        return this;
    }
}
