package api;

import api.models.CreatePatientResponse;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import api.requests.steps.AdminSteps;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DeletePatientTest extends BaseTest{
    private static Boolean PATH_PARAM_PURGE = true;

    @Test
    void deletePatientTest() {
        String createdUuid = AdminSteps.createUnknownPatient();
        new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT_DELETE,
                ResponseSpecs.requestReturnsNoContent()) //204
                .delete(createdUuid);
        softly.assertThat(AdminSteps.findPatientByUuid(createdUuid).getDisplay()).isEmpty();
    }

    @Test
    void deletePatientFromDBTest(){
        String createdUuid = AdminSteps.createUnknownPatient();
        new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT_DELETE,
                ResponseSpecs.requestReturnsNoContent()) //404
                .delete(createdUuid, PATH_PARAM_PURGE);


    }
}
