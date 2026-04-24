package api;

import api.models.*;
import api.requests.steps.AdminSteps;
import org.junit.jupiter.api.Test;
import api.models.BaseModel;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class VisitTest extends BaseModel {

    @Test
    public void allVisitTypesCanBeRetrievedTest() {
        List<VisitTypeListResponse> visitTypes = AdminSteps.getAllVisitTypes();

        assertThat(visitTypes).isNotEmpty();
        assertThat(visitTypes).allMatch(vt -> vt.getUuid() != null);
        assertThat(visitTypes).allMatch(vt -> vt.getName() != null);
    }

    @Test
    public void visitTypeCanBeSearchedByNameTest() {
        List<VisitTypeListResponse> facilityVisits = AdminSteps.searchVisitTypeByName("Facility");

        assertThat(facilityVisits).isNotEmpty();
        assertThat(facilityVisits.get(0).getName()).isEqualTo("Facility Visit");
        assertThat(facilityVisits.get(0).getDescription())
                .contains("Patient visits the clinic/hospital");
        assertThat(facilityVisits.get(0).isRetired()).isFalse();
    }

    @Test
    public void visitTypeCanBeRetrievedByUuidTest() {
        String facilityVisitTypeUuid = AdminSteps.getFacilityVisitTypeUuid();

        VisitTypeFullResponse visitType = AdminSteps.getVisitTypeByUuid(facilityVisitTypeUuid);

        assertThat(visitType.getUuid()).isEqualTo(facilityVisitTypeUuid);
        assertThat(visitType.getName()).isEqualTo("Facility Visit");
        assertThat(visitType.getDisplay()).isEqualTo("Facility Visit");
        assertThat(visitType.isRetired()).isFalse();
    }

    @Test
    public void visitTypeContainsRequiredFieldsTest() {
        List<VisitTypeListResponse> visitTypes = AdminSteps.getAllVisitTypes();
        VisitTypeListResponse visitType = visitTypes.get(0);

        assertThat(visitType.getUuid()).isNotEmpty();
        assertThat(visitType.getName()).isNotEmpty();
        assertThat(visitType.getDisplay()).isNotEmpty();
        assertThat(visitType.getLinks()).isNotEmpty();
        assertThat(visitType.getResourceVersion()).isNotEmpty();
    }

    @Test
    public void visitTypeUuidIsValidUuidFormatTest() {
        List<VisitTypeListResponse> visitTypes = AdminSteps.getAllVisitTypes();


        assertThat(visitTypes).allMatch(vt ->
                vt.getUuid().matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
        );
    }



}
