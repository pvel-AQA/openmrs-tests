package api;

import api.models.*;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import api.requests.steps.AdminSteps;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CreatePatientTest extends BaseTest {
    @Test
    public void patientCanBeCreatedWithValidDataTest() {
        String identifierTypeUuid = AdminSteps.getIdentifierTypeUuid();
        String identifierSourceUuid = AdminSteps.getIdentifierSourceUuid();
        String patientIdentifier = AdminSteps.generatePatientIdentifier(identifierSourceUuid);
        String locationUuid = AdminSteps.getLocationUuidByName("Outpatient");

        PersonName personName = PersonName.builder()
                .givenName("Vasiliy")
                .middleName("Test2")
                .familyName("Petrov").build();

        CreatePersonRequest person = CreatePersonRequest.builder()
                .gender("M")
                .names(List.of(personName)).build();

        IdentifiersForPatientCreation identifiers = IdentifiersForPatientCreation.builder()
                .identifier(patientIdentifier)
                .identifierType(identifierTypeUuid)
                .location(locationUuid)
                .preferred(true)
                .build();

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
                .isEqualTo(String.format("%s - %s %s %s", patientIdentifier, personName.getGivenName(),
                        personName.getMiddleName(), personName.getFamilyName()));

        String patientUuid = createdPatient.getUuid();
        CreatePatientResponse foundPatientList = AdminSteps.findPatientByUuid(patientUuid);


        assertThat(createdPatient.getUuid()).isEqualTo(foundPatientList.getUuid());
        assertThat(createdPatient.getDisplay()).isEqualTo(foundPatientList.getDisplay());
        assertThat(createdPatient.getPerson().getGender()).isEqualTo(foundPatientList.getPerson().getGender());




    }

}
