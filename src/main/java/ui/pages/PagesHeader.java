package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class PagesHeader extends BasePage{
    @Override
    public String url() {
        return "/";
    }
    private SelenideElement changeClinicButton = $("button[aria-label='Change location']");

}
