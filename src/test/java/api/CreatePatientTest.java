package api;

import api.models.*;
import api.models.comparison.ModelAssertions;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import api.requests.steps.AdminSteps;
import common.generators.PartialEntityGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CreatePatientTest extends BaseTest {
    @Test
    public void patientCanBeCreatedWithValidDataTest() {
        final String ClinicNameToGetLocationUuid = "Outpatient";
        final boolean preferredIdentifierTrue = true;
        final String[] fieldsToBeGenerated = new String[]{"givenName", "middleName", "familyName"};

        PersonName personName = PartialEntityGenerator.generate(PersonName.class, fieldsToBeGenerated);

        CreatePersonRequest person = CreatePersonRequest.builder()
                .gender(Gender.M.toString())
                .names(List.of(personName)).build();

        IdentifiersForPatientCreation identifiers = AdminSteps.prepareIdentifiersForPatientCreation(
                ClinicNameToGetLocationUuid, preferredIdentifierTrue);

        CreatePatientRequest createPatientRequest = CreatePatientRequest.builder()
                .identifiers(List.of(identifiers))
                .person(person)
                .build();

        CreatePatientResponse createdPatient = new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT,
                ResponseSpecs.requestReturnsCreated())
                .post(createPatientRequest);

        assertThat(createdPatient.getUuid()).isNotEmpty();
        assertThat(createdPatient.getPerson().getGender()).isEqualTo(person.getGender());
        assertThat(createdPatient.getDisplay())
                .isEqualTo(String.format("%s - %s %s %s", identifiers.getIdentifier(), personName.getGivenName(),
                        personName.getMiddleName(), personName.getFamilyName()));

        CreatePatientResponse foundPatientList = AdminSteps.findPatientByUuid(createdPatient.getUuid());

        ModelAssertions.assertThatModels(createdPatient, foundPatientList).match();
    }

}
