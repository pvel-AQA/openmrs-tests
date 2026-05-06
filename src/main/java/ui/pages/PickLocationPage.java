package ui.pages;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class PickLocationPage extends BasePage<PickLocationPage> {
    @Override
    public String url() {
        return "/login/location";
    }

    private SelenideElement welcomeText = $("p.-esm-login__location-picker__welcomeTitle___iI\\+4Z");
}
