package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import ui.components.Header;

import static com.codeborne.selenide.Selenide.$;

public class PatientSearchResultsPage extends AuthBasePage<PatientSearchResultsPage> {
    public Header header = new Header();
    private final SelenideElement errorText = $(Selectors.byXpath(""));
    private final SelenideElement errorHint = $(Selectors.byXpath(""));
    private final SelenideElement searchResultsCount = $(Selectors.byText("search results"));

    //user-block

    //Refine panel

    @Override
    public String url() {
        return "http://localhost/openmrs/spa/search?query=%s";
    }

    @Override
    public Boolean atPage() {
        return searchResultsCount.is(Condition.visible);
    }
}
