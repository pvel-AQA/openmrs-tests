package api;

import api.models.CreatePatientRequest;
import api.models.CreatePatientResponse;
import api.models.comparison.ModelAssertions;
import api.models.patient.UpdatePatientRequest;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import api.requests.steps.AdminSteps;
import common.generators.RandomDataGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdatePatientTest extends BaseTest {
    @Test
    public void patientCanBeUpdatedSuccessfully() {
        final int nameLength = 8;

        CreatePatientRequest patientRequest = AdminSteps.createPatientRequest();
        CreatePatientResponse createdPatient = AdminSteps.createPatient(patientRequest);

        UpdatePatientRequest updatePatientRequest = AdminSteps.prepareUpdatePatientRequest(patientRequest, createdPatient);
        updatePatientRequest.getPerson().getNames().getFirst()
                .setGivenName(RandomDataGenerator.randomString(nameLength));

        CreatePatientResponse updatedPatient = new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT_UPDATE,
                ResponseSpecs.requestReturnsOK()
        ).post(updatePatientRequest, createdPatient.getUuid());

        ModelAssertions.assertThatModels(createdPatient, updatedPatient).match();
        softly.assertThat(updatedPatient.getDisplay())
                .isEqualTo(String.format("%s - %s %s %s",
                        updatePatientRequest.getIdentifiers().getFirst().getIdentifier(),
                        updatePatientRequest.getPerson().getNames().getFirst().getGivenName(),
                        updatePatientRequest.getPerson().getNames().getFirst().getMiddleName(),
                        updatePatientRequest.getPerson().getNames().getFirst().getFamilyName()));
        softly.assertThat(updatedPatient.getPerson().getDisplay())
                .isEqualTo("%s %s %s",
                        updatePatientRequest.getPerson().getNames().getFirst().getGivenName(),
                        updatePatientRequest.getPerson().getNames().getFirst().getMiddleName(),
                        updatePatientRequest.getPerson().getNames().getFirst().getFamilyName());
        softly.assertThat(updatedPatient.getPerson().getPreferredName().getDisplay())
                .isEqualTo("%s %s %s",
                updatePatientRequest.getPerson().getNames().getFirst().getGivenName(),
                updatePatientRequest.getPerson().getNames().getFirst().getMiddleName(),
                updatePatientRequest.getPerson().getNames().getFirst().getFamilyName());

        CreatePatientResponse foundUpdatedPatient = AdminSteps.findPatientByUuid(updatedPatient.getUuid());

        assertThat(foundUpdatedPatient).usingRecursiveComparison().isEqualTo(updatedPatient);
    }
}
