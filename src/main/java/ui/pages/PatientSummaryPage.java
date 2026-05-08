package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

public class PatientSummaryPage extends BasePage<PatientSummaryPage> {
    private final SelenideElement patientName = $("span._3QvC113UMQvMOBhW\\+z79\\+Q\\=\\=");
    private final SelenideElement openMrsIdNumber = $("span._7O7gKi8oSk8dU4N7z9kfrQ\\=\\=");
    private final SelenideElement genderText = $("div.qIV9qXAs11cCtcKxvIip4w\\=\\=>span");
    private final SelenideElement ageText = $("div.m8jQX0Xu7TIqdLMfGF5vMw\\=\\= > span:nth-child(1)");
    private final SelenideElement birthDateText = $("div.m8jQX0Xu7TIqdLMfGF5vMw\\=\\= > span:nth-child(3)");

    @Override
    public String url() {
        return "/patient/%s/chart/Patient%%20Summary";
    }

    public PatientSummaryPage checkPatientNameIsEqualTo(String name) {
        patientName.shouldBe(Condition.visible);
        String patientNameText = patientName.getText();

        assertThat(patientNameText).isEqualTo(name);

        return this;
    }

    public PatientSummaryPage checkOpenMrsIdIsEqualTo(String id) {
        openMrsIdNumber.shouldBe(Condition.visible);
        String textId = openMrsIdNumber.getText();

        assertThat(textId).isEqualTo(id);

        return this;
    }


}
