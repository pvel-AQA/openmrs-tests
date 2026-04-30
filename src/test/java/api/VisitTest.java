package api;

import api.models.*;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.skeleton.requesters.VisitTypeEnum;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import api.requests.steps.AdminSteps;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class VisitTest extends BaseTest {

    @Test
    public void allVisitTypesCanBeRetrievedTest() {
        List<VisitTypeResponse> visitTypes =
                new ValidatedCrudRequester<VisitTypeResponse>(
                        RequestSpecs.adminSpec(),
                        Endpoint.VISIT_TYPE,
                        ResponseSpecs.requestReturnsOK())
                        .getWithParams(
                                new CrudRequester.QueryBuilder()
                                        .v("full")
                                        .build(),
                                VisitTypeResponse.class);

        assertThat(visitTypes)
                .as("Visit types list should not be empty")
                .isNotEmpty();

        assertThat(visitTypes)
                .as("All visit types should have UUID")
                .allMatch(vt -> vt.getUuid() != null);

        assertThat(visitTypes)
                .as("All visit types should have name")
                .allMatch(vt -> vt.getName() != null);
    }

    @Test
    public void allDefinedVisitTypesShouldExistTest() {// Проверяем что ВСЕ типы из Enum существуют в системе
        for (VisitTypeEnum visitType : VisitTypeEnum.values()) {
            String visitTypeUuid = AdminSteps.getVisitTypeUuid(visitType);

            assertThat(visitTypeUuid)
                    .as(visitType.getDisplayName() + " should have UUID")
                    .isNotEmpty();
        }
    }

    @Test
    public void visitTypeCanBeSearchedByNameTest() {
        VisitTypeEnum searchType = VisitTypeEnum.FACILITY_VISIT;

        List<VisitTypeResponse> results =
                new ValidatedCrudRequester<VisitTypeResponse>(
                        RequestSpecs.adminSpec(),
                        Endpoint.VISIT_TYPE,
                        ResponseSpecs.requestReturnsOK())
                        .getWithParams(
                                new CrudRequester.QueryBuilder()
                                        .q(searchType.getDisplayName())
                                        .v("full")
                                        .build(),
                                VisitTypeResponse.class);

        assertThat(results)
                .as("Search results should not be empty")
                .isNotEmpty();

        assertThat(results.get(0).getName())
                .as("First result should be '" + searchType.getDisplayName() + "'")
                .isEqualTo(searchType.getDisplayName());
    }

    @Test
    public void visitTypeCanBeRetrievedByUuidTest() {
        String facilityUuid = AdminSteps.getVisitTypeUuid(
                VisitTypeEnum.FACILITY_VISIT);
        VisitTypeResponse visitType =
                new ValidatedCrudRequester<VisitTypeResponse>(
                        RequestSpecs.adminSpec(),
                        Endpoint.VISIT_TYPE_BY_UUID,
                        ResponseSpecs.requestReturnsOK())
                        .get(facilityUuid, VisitTypeResponse.class);
        assertThat(visitType.getUuid())
                .as("UUID should match requested UUID")
                .isEqualTo(facilityUuid);

        assertThat(visitType.getName())
                .as("Name should be '" + VisitTypeEnum.FACILITY_VISIT.getDisplayName() + "'")
                .isEqualTo(VisitTypeEnum.FACILITY_VISIT.getDisplayName());
    }

    @Test
    public void visitTypeContainsRequiredFieldsTest() {
        List<VisitTypeResponse> visitTypes =
                new ValidatedCrudRequester<VisitTypeResponse>(
                        RequestSpecs.adminSpec(),
                        Endpoint.VISIT_TYPE,
                        ResponseSpecs.requestReturnsOK())
                        .getWithParams(
                                new CrudRequester.QueryBuilder()
                                        .v("full")
                                        .build(),
                                VisitTypeResponse.class);

        assertThat(visitTypes).isNotEmpty();
        VisitTypeResponse visitType = visitTypes.get(0);

        assertThat(visitType.getUuid()).isNotEmpty();
        assertThat(visitType.getName()).isNotEmpty();
        assertThat(visitType.getDisplay()).isNotEmpty();
        assertThat(visitType.getLinks()).isNotEmpty();
        assertThat(visitType.getResourceVersion()).isNotEmpty();
    }

    @Test
    public void visitTypeUuidIsValidUuidFormatTest() {
        List<VisitTypeResponse> visitTypes =
                new ValidatedCrudRequester<VisitTypeResponse>(
                        RequestSpecs.adminSpec(),
                        Endpoint.VISIT_TYPE,
                        ResponseSpecs.requestReturnsOK())
                        .getWithParams(
                                new CrudRequester.QueryBuilder()
                                        .v("full")
                                        .build(),
                                VisitTypeResponse.class);

        assertThat(visitTypes)
                .as("All UUIDs should match valid UUID format (8-4-4-4-12)")
                .allMatch(vt ->
                        vt.getUuid().matches(
                                "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"
                        )
                );
    }
}
