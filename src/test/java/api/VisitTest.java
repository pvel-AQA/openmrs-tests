package api;

import api.models.*;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import api.requests.steps.AdminSteps;
import org.junit.jupiter.api.Test;
import api.models.BaseModel;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class VisitTest extends BaseModel {

    @Test
    public void allVisitTypesCanBeRetrievedTest() {
        List<VisitTypeListResponse> visitTypes =
                new ValidatedCrudRequester<VisitTypeListResponse>(
                        RequestSpecs.adminSpec(),
                        Endpoint.VISIT_TYPE,
                        ResponseSpecs.requestReturnsOK())
                        .getWithParams(
                                new CrudRequester.QueryBuilder()
                                        .v("full")
                                        .build(),
                                VisitTypeListResponse.class);

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
    public void visitTypeCanBeSearchedByNameTest() {
        String searchQuery = "Facility";

        List<VisitTypeListResponse> facilityVisits =
                new ValidatedCrudRequester<VisitTypeListResponse>(
                        RequestSpecs.adminSpec(),
                        Endpoint.VISIT_TYPE,
                        ResponseSpecs.requestReturnsOK())
                        .getWithParams(
                                new CrudRequester.QueryBuilder()
                                        .q(searchQuery)
                                        .v("full")
                                        .build(),
                                VisitTypeListResponse.class);

        assertThat(facilityVisits)
                .as("Search results should not be empty")
                .isNotEmpty();

        assertThat(facilityVisits.get(0).getName())
                .as("First result should be 'Facility Visit'")
                .isEqualTo("Facility Visit");

        assertThat(facilityVisits.get(0).getDescription())
                .as("Description should contain clinic/hospital reference")
                .contains("Patient visits the clinic/hospital");

        assertThat(facilityVisits.get(0).isRetired())
                .as("Visit type should not be retired")
                .isFalse();
    }

    @Test
    public void visitTypeCanBeRetrievedByUuidTest() {
        String facilityVisitTypeUuid = AdminSteps.getFacilityVisitTypeUuid();

        VisitTypeFullResponse visitType =
                new ValidatedCrudRequester<VisitTypeFullResponse>(
                        RequestSpecs.adminSpec(),
                        Endpoint.VISIT_TYPE_BY_UUID,
                        ResponseSpecs.requestReturnsOK())
                        .get(facilityVisitTypeUuid, VisitTypeFullResponse.class);

        assertThat(visitType.getUuid())
                .as("UUID should match requested UUID")
                .isEqualTo(facilityVisitTypeUuid);

        assertThat(visitType.getName())
                .as("Name should be 'Facility Visit'")
                .isEqualTo("Facility Visit");

        assertThat(visitType.getDisplay())
                .as("Display should equal name")
                .isEqualTo("Facility Visit");

        assertThat(visitType.isRetired())
                .as("Visit type should not be retired")
                .isFalse();
    }

    @Test
    public void visitTypeContainsRequiredFieldsTest() {
        List<VisitTypeListResponse> visitTypes =
                new ValidatedCrudRequester<VisitTypeListResponse>(
                        RequestSpecs.adminSpec(),
                        Endpoint.VISIT_TYPE,
                        ResponseSpecs.requestReturnsOK())
                        .getWithParams(
                                new CrudRequester.QueryBuilder()
                                        .v("full")
                                        .build(),
                                VisitTypeListResponse.class);

        assertThat(visitTypes)
                .as("Visit types list should not be empty")
                .isNotEmpty();

        VisitTypeListResponse visitType = visitTypes.get(0);

        assertThat(visitType.getUuid())
                .as("UUID should not be empty")
                .isNotEmpty();

        assertThat(visitType.getName())
                .as("Name should not be empty")
                .isNotEmpty();

        assertThat(visitType.getDisplay())
                .as("Display should not be empty")
                .isNotEmpty();

        assertThat(visitType.getLinks())
                .as("Links should not be empty")
                .isNotEmpty();

        assertThat(visitType.getResourceVersion())
                .as("ResourceVersion should not be empty")
                .isNotEmpty();
    }

    @Test
    public void visitTypeUuidIsValidUuidFormatTest() {
        List<VisitTypeListResponse> visitTypes =
                new ValidatedCrudRequester<VisitTypeListResponse>(
                        RequestSpecs.adminSpec(),
                        Endpoint.VISIT_TYPE,
                        ResponseSpecs.requestReturnsOK())
                        .getWithParams(
                                new CrudRequester.QueryBuilder()
                                        .v("full")
                                        .build(),
                                VisitTypeListResponse.class);

        assertThat(visitTypes)
                .as("All UUIDs should match valid UUID format (8-4-4-4-12)")
                .allMatch(vt ->
                        vt.getUuid().matches(
                                "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"
                        )
                );
    }


}
