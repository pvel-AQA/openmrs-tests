package ui;

import api.models.CreatePatientRequest;
import api.models.CreatePatientResponse;
import api.requests.steps.AdminSteps;
import api.utils.DisplayFormatterUtils;
import common.annotations.AdminSession;
import common.generators.RandomDataGenerator;
import org.junit.jupiter.api.Test;
import ui.pages.PickLocationPage;

public class RegisterPatientTest extends BaseUiTest {
    @Test
    @AdminSession
    public void knownPatientCanBeRegisteredWithValidDataTest() {
        CreatePatientRequest patient = AdminSteps.createPatientRequest();
        patient.getPerson().setBirthdate(RandomDataGenerator.generateValidDateUiFormat());

        new PickLocationPage().open()
                .pickOutpatientLocationAndConfirm()
                .header.clickAddPatientButton()
                .registerPatientWithAllFieldsPopulatedCorrectly(patient)
                .checkPatientNameIsEqualTo(DisplayFormatterUtils
                        .personDisplayFormatter(patient.getPerson().getNames().getFirst()));

    }
}
