package api;

import api.models.CreatePatientRequest;
import api.models.CreatePatientResponse;
import api.models.comparison.ModelAssertions;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import api.requests.steps.AdminSteps;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdatePatientTest extends BaseTest {
    @Test
    public void patientCanBeUpdatedSuccessfully() {
        CreatePatientRequest patientRequest = AdminSteps.createPatientRequest();
        CreatePatientResponse createdPatient = AdminSteps.createPatient(patientRequest);
        patientRequest.getPerson().getNames().getFirst().setGivenName("Givenname");

        CreatePatientResponse updatedPatient = new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT_UPDATE,
                ResponseSpecs.requestReturnsOK()
        ).post(patientRequest, createdPatient.getUuid());

        ModelAssertions.assertThatModels(createdPatient, updatedPatient).match();
        softly.assertThat(updatedPatient.getDisplay())
                .isEqualTo(String.format("%s - %s %s %s",
                        patientRequest.getIdentifiers().getFirst().getIdentifier(),
                        patientRequest.getPerson().getNames().getFirst().getGivenName(),
                        patientRequest.getPerson().getNames().getFirst()
                                .getMiddleName(), patientRequest.getPerson().getNames().getFirst().getFamilyName()));
        softly.assertThat(updatedPatient.getPerson().getDisplay())
                .isEqualTo("%s %s %s",
                        patientRequest.getPerson().getNames().getFirst().getGivenName(),
                        patientRequest.getPerson().getNames().getFirst().getMiddleName(),
                        patientRequest.getPerson().getNames().getFirst().getFamilyName());
        softly.assertThat(updatedPatient.getPerson().getPreferredName().getDisplay())
                .isEqualTo("%s %s %s",
                patientRequest.getPerson().getNames().getFirst().getGivenName(),
                patientRequest.getPerson().getNames().getFirst().getMiddleName(),
                patientRequest.getPerson().getNames().getFirst().getFamilyName());

        CreatePatientResponse foundUpdatedPatient = AdminSteps.findPatientByUuid(updatedPatient.getUuid());

        assertThat(foundUpdatedPatient).usingRecursiveComparison().isEqualTo(updatedPatient);
    }
}
