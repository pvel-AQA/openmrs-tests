package api.requests.steps;

import api.models.*;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.skeleton.requesters.VisitTypeEnum;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import common.generators.PartialEntityGenerator;
import common.generators.RandomDataGenerator;

import java.util.List;

public class AdminSteps {
    private static final boolean PREFERRED_IDENTIFIER_TRUE = true;
    private static final String[] NAMES_FIELDS_TO_BE_GENERATED = new String[]{"givenName", "middleName", "familyName"};
    private static final String[] PERSON_FIELDS_TO_BE_GENERATED = new String[]{"gender", "birthdate", "birthdateEstimated",
            "dead", "addresses", "attributes"};

    public static String getIdentifierSourceUuid() {
        IdentifierSource sourceResponse = new ValidatedCrudRequester<IdentifierSource>(
                RequestSpecs.adminSpec(),
                Endpoint.IDENTIFIER_SOURCE,
                ResponseSpecs.requestReturnsOK())
                .getAll(new CrudRequester.QueryBuilder().vEqualsFull().build(),
                        IdentifierSource.class).getFirst();

        return sourceResponse.getUuid();
    }

    public static String getIdentifierTypeUuid() {
        IdentifierSource sourceResponse = new ValidatedCrudRequester<IdentifierSource>(
                RequestSpecs.adminSpec(),
                Endpoint.IDENTIFIER_SOURCE,
                ResponseSpecs.requestReturnsOK())
                .getAll(new CrudRequester.QueryBuilder().vEqualsFull().build(),
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
        ).getAll(new CrudRequester.QueryBuilder().q(name).build(), LocationResponse.class).getFirst().getUuid();
    }

    public static CreatePatientResponse findPatientByUuid(String patientUuid) {
        return new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT_SEARCH,
                ResponseSpecs.requestReturnsOK())
                .get(patientUuid, CreatePatientResponse.class);
    }

    private static List<VisitTypeResponse> searchVisitTypeByName(String name) {
        return new ValidatedCrudRequester<VisitTypeResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.VISIT_TYPE,
                ResponseSpecs.requestReturnsOK())
                .getWithParams(
                        new CrudRequester.QueryBuilder()
                                .q(name)
                                .v("full")
                                .build(),
                        VisitTypeResponse.class);
    }

    public static String getVisitTypeUuid(VisitTypeEnum visitType) {
        List<VisitTypeResponse> visitTypes = searchVisitTypeByName(
                visitType.getDisplayName());

        if (visitTypes.isEmpty()) {
            throw new RuntimeException(
                    visitType.getDisplayName() + " Visit Type not found");
        }

        return visitTypes.get(0).getUuid();
    }

    public static IdentifiersForPatientCreation prepareIdentifiersForPatientCreation(String clinicNameToGetLocation, boolean isIdPreferred) {
        return IdentifiersForPatientCreation.builder()
                .identifier(generatePatientIdentifier(getIdentifierSourceUuid()))
                .identifierType(getIdentifierTypeUuid())
                .location(getLocationUuidByName(clinicNameToGetLocation))
                .preferred(isIdPreferred)
                .build();

    }

    public static AddressResponse getPersonAddress(String personUuid) {
        return new ValidatedCrudRequester<AddressResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PERSON_ADDRESS,
                ResponseSpecs.requestReturnsOK())
                .getAll(personUuid, AddressResponse.class).getFirst();
    }

    public static CreatePatientResponse createPatient() {
        PersonName personName = PartialEntityGenerator.generate(PersonName.class, NAMES_FIELDS_TO_BE_GENERATED);

        CreatePersonRequest person = PartialEntityGenerator.generate(CreatePersonRequest.class, PERSON_FIELDS_TO_BE_GENERATED);
        person.setNames(List.of(personName));
        person.setBirthdate(RandomDataGenerator.generateValidDate());

        IdentifiersForPatientCreation identifiers = AdminSteps.prepareIdentifiersForPatientCreation(
                ClinicName.OUTPATIENT.getClinicName(), PREFERRED_IDENTIFIER_TRUE);

        CreatePatientRequest createPatientRequest = CreatePatientRequest.builder()
                .identifiers(List.of(identifiers))
                .person(person)
                .build();

        return new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT,
                ResponseSpecs.requestReturnsCreated())
                .post(createPatientRequest);
    }
}
