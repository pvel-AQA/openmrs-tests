package api;

import api.models.*;
import api.models.comparison.ModelAssertions;
import api.models.visit.CreateVisitRequest;
import api.models.visit.CreateVisitResponse;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.skeleton.requesters.VisitTypeEnum;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import api.requests.steps.AdminSteps;
import common.generators.RandomDataGenerator;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class VisitTest extends BaseTest {

    @Test
    public void createVisitTest() {
        CreatePatientResponse patient = AdminSteps.createPatient();

        CreateVisitRequest createRequest = CreateVisitRequest.builder()
                .patient(patient.getUuid())
                .visitType(AdminSteps.getVisitTypeUuid(VisitTypeEnum.FACILITY_VISIT))
                .startDatetime(RandomDataGenerator.generateVisitStartDatetime())
                .location(AdminSteps.getLocationUuidByName(ClinicName.OUTPATIENT.getClinicName()))
                .indication(RandomDataGenerator.generateVisitIndication())
                .build();

        CreateVisitResponse createdVisit = new ValidatedCrudRequester<CreateVisitResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.VISIT,
                ResponseSpecs.requestReturnsCreated())
                .post(createRequest);

        CreateVisitResponse foundVisit = new ValidatedCrudRequester<CreateVisitResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.VISIT_BY_UUID,
                ResponseSpecs.requestReturnsOK())
                .get(createdVisit.getUuid(), CreateVisitResponse.class);

        ModelAssertions.assertThatModels(createRequest, foundVisit).match();
    }

    @Test
    public void searchRecentVisitsTest() {
        CreatePatientResponse patient = AdminSteps.createPatient();
        CreateVisitResponse createdVisit = AdminSteps.createVisit(patient);

        Map<String, Object> queryParams = new CrudRequester.QueryBuilder()
                .add("patient", patient.getUuid())
                .add("includeInactive", "false")
                .vEqualsFull()
                .limit(5)
                .build();

        List<CreateVisitResponse> foundVisits = new ValidatedCrudRequester<CreateVisitResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.VISIT,
                ResponseSpecs.requestReturnsOK())
                .getAll(queryParams, CreateVisitResponse.class);

        assertThat(foundVisits)
                .as("Created visit should be in search results")
                .extracting(CreateVisitResponse::getUuid)
                .contains(createdVisit.getUuid());
    }

    @Test
    public void updateVisitStartDatetimeTest() {
        CreatePatientResponse patient = AdminSteps.createPatient();
        CreateVisitResponse createdVisit = AdminSteps.createVisit(patient);

        String newStartDatetime = RandomDataGenerator.generateVisitStartDatetime();

        AdminSteps.updateVisit(createdVisit.getUuid(), newStartDatetime);

        CreateVisitResponse updatedVisit = new ValidatedCrudRequester<CreateVisitResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.VISIT_BY_UUID,
                ResponseSpecs.requestReturnsOK())
                .get(createdVisit.getUuid(), CreateVisitResponse.class);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(updatedVisit.getStartDatetime()).contains(newStartDatetime.substring(0, 10));
        });
    }

    @Test
    public void deleteVisitTest() {
        CreatePatientResponse patient = AdminSteps.createPatient();
        CreateVisitResponse createdVisit = AdminSteps.createVisit(patient);

        new ValidatedCrudRequester<CreateVisitResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.VISIT_BY_UUID,
                ResponseSpecs.requestReturnsNoContent())
                .delete(createdVisit.getUuid(), true);

        new ValidatedCrudRequester<CreateVisitResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.VISIT_BY_UUID,
                ResponseSpecs.requestReturnsNotFoundWithMessage("Object with given uuid doesn't exist [null]"))
                .get(createdVisit.getUuid(), CreateVisitResponse.class);
    }
}

