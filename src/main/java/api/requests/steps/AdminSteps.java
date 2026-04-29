package api.requests.steps;

import api.models.*;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;

import java.util.List;
import java.util.Map;

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

    public static IdentifiersForPatientCreation prepareIdentifiersForPatientCreation(String clinicNameToGetLocation, boolean isIdPreferred) {
        return IdentifiersForPatientCreation.builder()
                .identifier(generatePatientIdentifier(getIdentifierSourceUuid()))
                .identifierType(getIdentifierTypeUuid())
                .location(getLocationUuidByName(clinicNameToGetLocation))
                .preferred(isIdPreferred)
                .build();
    }

    public static boolean deletePatientByUuid(String patientUuid) {
        new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT_DELETE,
                ResponseSpecs.requestReturnsNoContent())
                .delete(patientUuid);
        return true;
    }
}
