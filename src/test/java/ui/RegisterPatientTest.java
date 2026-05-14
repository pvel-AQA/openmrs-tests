package ui;

import api.models.AddressResponse;
import api.models.CreatePatientResponse;
import api.models.comparison.ModelAssertions;
import api.models.ui.GenderUi;
import api.models.ui.RegisterPatientUi;
import api.requests.steps.AdminSteps;
import common.annotations.AdminSession;
import common.generators.RandomDataGenerator;
import org.junit.jupiter.api.Test;
import ui.pages.PatientSummaryPage;
import ui.pages.PickLocationPage;

import static api.utils.DisplayFormatterUtils.personDisplayFormatter;

public class RegisterPatientTest extends BaseUiTest {
    @Test
    @AdminSession
    public void knownPatientCanBeRegisteredWithAllValidDataTest() {
        RegisterPatientUi patient = AdminSteps.createPatientForUi();
        patient.setBirthdate(RandomDataGenerator.generateValidDateUiFormat());

        String patientUuid = new PickLocationPage().open()
                .pickOutpatientLocationAndConfirm()
                .header.clickAddPatientButton()
                .registerPatientWithAllFieldsPopulatedCorrectly(patient)
                .verifySuccessNotification()

                .checkPatientNameIsEqualTo(personDisplayFormatter(patient.getNames().getFirst()))
                .checkIdPrefixIsEqualTo(PatientSummaryPage.OPEN_MRS_ID_TEXT)
                .checkBirthDateIsEqualTo(patient.getBirthdate())
                .checkGenderIsEqualTo(patient.getGender())
                .checkGenderIconIsCorrect()

                .clickShowMoreButton()
                .getAddressComponent().checkAllAddressFieldsAreCorrect(patient.getAddresses().getFirst())
                .getContactDetailsComponent().checkTelephoneNumberIsEqualTo(patient.getAttributes().getFirst().getValue())
                .getPatientUuid();

        String openMrsIdText = new PatientSummaryPage().getOpenMrsIdTextInApiFormat();
        CreatePatientResponse foundPatient = AdminSteps.findPatientByUuid(patientUuid);
        AddressResponse patientAddress = AdminSteps.getPersonAddress(foundPatient.getPerson().getUuid());

        patient.setGender(GenderUi.toShortGender(patient.getGender()));

        ModelAssertions.assertThatModels(foundPatient, patient).match();
        softly.assertThat(openMrsIdText).isEqualTo(foundPatient.getIdentifiers().getFirst().getDisplay());
        softly.assertThat(personDisplayFormatter(patient.getNames().getFirst()))
                .isEqualTo(foundPatient.getPerson().getPreferredName().getDisplay());
        ModelAssertions.assertThatModels(patient, patientAddress);
    }
}
