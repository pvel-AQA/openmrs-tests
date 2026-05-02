package api;

import api.models.*;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.skeleton.requesters.VisitTypeEnum;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import api.requests.steps.AdminSteps;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class VisitTest extends BaseTest {
    @Test
    public void allVisitTypesCanBeRetrievedTest() {
        List<VisitTypeResponse> visitTypes =
                new ValidatedCrudRequester<VisitTypeResponse>(
                        RequestSpecs.adminSpec(),
                        Endpoint.VISIT_TYPE,
                        ResponseSpecs.requestReturnsOK())
                        .getAll(
                                new CrudRequester.QueryBuilder()
                                        .vEqualsFull()
                                        .build(),
                                VisitTypeResponse.class);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(visitTypes)
                    .as("Visit types list should not be empty")
                    .isNotEmpty();

            softly.assertThat(visitTypes)
                    .as("All visit types should have UUID")
                    .allMatch(vt -> vt.getUuid() != null && !vt.getUuid().isEmpty());

            softly.assertThat(visitTypes)
                    .as("All visit types should have name")
                    .allMatch(vt -> vt.getName() != null && !vt.getName().isEmpty());
        });
    }

    @Test
    public void allDefinedVisitTypesShouldExistTest() {
        SoftAssertions.assertSoftly(softly -> {
            for (VisitTypeEnum visitType : VisitTypeEnum.values()) {
                String visitTypeUuid = AdminSteps.getVisitTypeUuid(visitType);

                softly.assertThat(visitTypeUuid)
                        .as(visitType.getDisplayName() + " should have UUID")
                        .isNotEmpty();
            }
        });
    }

    @Test
    public void visitTypeCanBeSearchedByNameTest() {
        VisitTypeEnum searchType = VisitTypeEnum.FACILITY_VISIT;

        List<VisitTypeResponse> results =
                new ValidatedCrudRequester<VisitTypeResponse>(
                        RequestSpecs.adminSpec(),
                        Endpoint.VISIT_TYPE,
                        ResponseSpecs.requestReturnsOK())
                        .getAll(
                                new CrudRequester.QueryBuilder()
                                        .q(searchType.getDisplayName())
                                        .vEqualsFull()
                                        .build(),
                                VisitTypeResponse.class);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(results)
                    .as("Search results should not be empty")
                    .isNotEmpty();

            softly.assertThat(results.get(0).getName())
                    .as("First result should be '" + searchType.getDisplayName() + "'")
                    .isEqualTo(searchType.getDisplayName());
        });
    }

    @Test
    public void visitTypeCanBeRetrievedByUuidTest() {
        String facilityUuid = AdminSteps.getVisitTypeUuid(VisitTypeEnum.FACILITY_VISIT);

        VisitTypeResponse visitType =
                new ValidatedCrudRequester<VisitTypeResponse>(
                        RequestSpecs.adminSpec(),
                        Endpoint.VISIT_TYPE_BY_UUID,
                        ResponseSpecs.requestReturnsOK())
                        .get(facilityUuid, VisitTypeResponse.class);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(visitType.getUuid())
                    .as("UUID should match requested UUID")
                    .isEqualTo(facilityUuid);

            softly.assertThat(visitType.getName())
                    .as("Name should be '" + VisitTypeEnum.FACILITY_VISIT.getDisplayName() + "'")
                    .isEqualTo(VisitTypeEnum.FACILITY_VISIT.getDisplayName());
        });
    }

    @Test
    public void visitTypeContainsRequiredFieldsTest() {
        List<VisitTypeResponse> visitTypes =
                new ValidatedCrudRequester<VisitTypeResponse>(
                        RequestSpecs.adminSpec(),
                        Endpoint.VISIT_TYPE,
                        ResponseSpecs.requestReturnsOK())
                        .getAll(
                                new CrudRequester.QueryBuilder()
                                        .vEqualsFull()
                                        .build(),
                                VisitTypeResponse.class);

        assertThat(visitTypes).isNotEmpty();
        VisitTypeResponse visitType = visitTypes.get(0);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(visitType.getUuid()).isNotEmpty();
            softly.assertThat(visitType.getName()).isNotEmpty();
            softly.assertThat(visitType.getDisplay()).isNotEmpty();
            softly.assertThat(visitType.getLinks()).isNotEmpty();
            softly.assertThat(visitType.getResourceVersion()).isNotEmpty();
        });
    }

    @Test
    public void visitTypeUuidIsValidUuidFormatTest() {
        String uuidRegex = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";

        List<VisitTypeResponse> visitTypes =
                new ValidatedCrudRequester<VisitTypeResponse>(
                        RequestSpecs.adminSpec(),
                        Endpoint.VISIT_TYPE,
                        ResponseSpecs.requestReturnsOK())
                        .getAll(
                                new CrudRequester.QueryBuilder()
                                        .vEqualsFull()
                                        .build(),
                                VisitTypeResponse.class);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(visitTypes)
                    .as("Visit types list should not be empty")
                    .isNotEmpty();

            softly.assertThat(visitTypes)
                    .as("All UUIDs should match valid UUID format (8-4-4-4-12)")
                    .allMatch(vt ->
                            vt.getUuid() != null &&
                                    vt.getUuid().matches(uuidRegex)
                    );
        });
    }

    @Test
    public void createVisitTest() {
        CreatePatientResponse patient = AdminSteps.createPatient();
        CreateVisitResponse visit = AdminSteps.createVisit(patient);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(patient).isNotNull();
            softly.assertThat(visit).isNotNull();
            softly.assertThat(visit.getUuid()).isNotEmpty();
            softly.assertThat(visit.getPatient().getUuid()).isEqualTo(patient.getUuid());
            softly.assertThat(visit.isVoided()).isFalse();
        });
    }

    @Test
    public void searchRecentVisitsReturnsCreatedVisitTest() {
        CreatePatientResponse patient = AdminSteps.createPatient();
        CreateVisitResponse createdVisit = AdminSteps.createVisit(patient);
        List<CreateVisitResponse> foundVisits = AdminSteps.searchRecentVisits(patient.getUuid());

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(foundVisits).isNotEmpty();
            softly.assertThat(foundVisits)
                    .as("Created visit should be found")
                    .anyMatch(visit -> visit.getUuid().equals(createdVisit.getUuid()));
        });
    }

    @Test
    public void updateVisitStartDatetimeTest() {
        CreatePatientResponse patient = AdminSteps.createPatient();
        CreateVisitResponse createdVisit = AdminSteps.createVisit(patient);

        String newStartDatetime = "2026-05-03T14:00:00.000+0200";
        CreateVisitResponse updatedVisit =
                AdminSteps.updateVisit(createdVisit.getUuid(), newStartDatetime);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(updatedVisit).isNotNull();
            softly.assertThat(updatedVisit.getUuid()).isEqualTo(createdVisit.getUuid());
            softly.assertThat(updatedVisit.getPatient().getUuid()).isEqualTo(patient.getUuid());
            softly.assertThat(updatedVisit.isVoided()).isFalse();
            softly.assertThat(updatedVisit.getStartDatetime()).contains("2026-05-03");
        });
    }

    @Test
    public void deleteVisitTest() {
        CreatePatientResponse patient = AdminSteps.createPatient();
        CreateVisitResponse createdVisit = AdminSteps.createVisit(patient);

        assertThat(createdVisit).isNotNull();
        assertThat(createdVisit.getUuid()).isNotBlank();

        AdminSteps.deleteVisit(createdVisit.getUuid());
        Response responseAfterDelete = AdminSteps.getVisitByUuidRaw(createdVisit.getUuid());

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(responseAfterDelete.statusCode()).isEqualTo(404);
        });
    }
}

