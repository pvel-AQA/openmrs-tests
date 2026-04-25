package api.requests.steps;

import api.models.*;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;

import java.util.List;

public class AdminSteps {
    public static String getIdentifierSourceUuid() {
        IdentifierSource sourceResponse = new ValidatedCrudRequester<IdentifierSource>(
                RequestSpecs.adminSpec(),
                Endpoint.IDENTIFIER_SOURCE,
                ResponseSpecs.requestReturnsOK())
                .getWithParams(
                        new CrudRequester.QueryBuilder().v("full").build(),
                        IdentifierSource.class).getFirst();

        return sourceResponse.getUuid();
    }

    public static String getIdentifierTypeUuid() {
        IdentifierSource sourceResponse = new ValidatedCrudRequester<IdentifierSource>(
                RequestSpecs.adminSpec(),
                Endpoint.IDENTIFIER_SOURCE,
                ResponseSpecs.requestReturnsOK())
                .getWithParams(
                        new CrudRequester.QueryBuilder().v("full").build(),
                        IdentifierSource.class).getFirst();

        return sourceResponse.getIdentifierType().getUuid();
    }

    public static String generatePatientIdentifier(String identifierSourceUuid) {
        return new ValidatedCrudRequester<IdentifierResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.IDENTIFIER,
                ResponseSpecs.requestReturnsCreated()
        ).post(null, identifierSourceUuid).getIdentifier();
    }

    public static String getLocationUuidByName(String name) {
        return new ValidatedCrudRequester<LocationResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.LOCATION,
                ResponseSpecs.requestReturnsOK()
        ).getWithParams(
                new CrudRequester.QueryBuilder().q(name).build(), LocationResponse.class).getFirst().getUuid();
    }

    public static CreatePatientResponse findPatientByUuid(String patientUuid) {
        return new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT_SEARCH,
                ResponseSpecs.requestReturnsOK())
                .get(patientUuid, CreatePatientResponse.class);
    }

    public static List<VisitTypeListResponse> getAllVisitTypes() {
        return new ValidatedCrudRequester<VisitTypeListResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.VISIT_TYPE,
                ResponseSpecs.requestReturnsOK())
                .getWithParams(
                        new CrudRequester.QueryBuilder()
                                .v("full")
                                .build(),
                        VisitTypeListResponse.class);
    }

    public static List<VisitTypeListResponse> searchVisitTypeByName(String name) {
        return new ValidatedCrudRequester<VisitTypeListResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.VISIT_TYPE,
                ResponseSpecs.requestReturnsOK())
                .getWithParams(
                        new CrudRequester.QueryBuilder()
                                .q(name)
                                .v("full")
                                .build(),
                        VisitTypeListResponse.class);
    }

    public static VisitTypeFullResponse getVisitTypeByUuid(String visitTypeUuid) {
        return new ValidatedCrudRequester<VisitTypeFullResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.VISIT_TYPE_BY_UUID,
                ResponseSpecs.requestReturnsOK())
                .get(visitTypeUuid, VisitTypeFullResponse.class);
    }

    public static String getFacilityVisitTypeUuid() {
        List<VisitTypeListResponse> visitTypes = searchVisitTypeByName("Facility");
        if (visitTypes.isEmpty()) {
            throw new RuntimeException("Facility Visit Type not found");
        }
        return visitTypes.get(0).getUuid(); // 7b0f5697-27e3-40c4-8bae-f4049abfb4ed
    }

    public static String getOutpatientVisitTypeUuid() {
        List<VisitTypeListResponse> visitTypes = searchVisitTypeByName("Outpatient");
        if (visitTypes.isEmpty()) {
            throw new RuntimeException("Outpatient Visit Type not found");
        }
        return visitTypes.get(0).getUuid();
    }
}
