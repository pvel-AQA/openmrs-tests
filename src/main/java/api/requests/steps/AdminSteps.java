package api.requests.steps;

import api.models.*;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import common.generators.PartialEntityGenerator;
import common.generators.RandomDataGenerator;
import io.restassured.response.Response;

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

    public static void deletePatientByUuid(String patientUuid) {
        new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT_DELETE,
                ResponseSpecs.requestReturnsNoContent())
                .delete(patientUuid);
    }

    public static void deletePatientByUuid(String patientUuid, Boolean purge) {
        new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT_DELETE,
                ResponseSpecs.requestReturnsNotFound())
                .delete(patientUuid, purge);
    }

    public static CreatePersonResponse findPersonByUuid(String personUuid) {
        return new ValidatedCrudRequester<CreatePersonResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PERSON_READ,
                ResponseSpecs.requestReturnsOK())
                .get(personUuid, CreatePersonResponse.class);
    }

    public static CreatePersonResponse buildAndPostRandomPerson(PersonName personName) {
        CreatePersonRequest createPersonRequest = CreatePersonRequest.builder()
                .names(List.of(personName))
                .age(RandomDataGenerator.randomAge(0,100))
                .gender(RandomDataGenerator.randomGender().toString())
                .build();

        return new ValidatedCrudRequester<CreatePersonResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PERSON,
                ResponseSpecs.requestReturnsCreated())
                .post(createPersonRequest);
    }

    public static void deletePersonByUuid(String uuid, Boolean purge) {
        new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PERSON_DELETE,
                ResponseSpecs.requestReturnsNoContent())
                .delete(uuid, purge);
    }

    public static void updatePerson(String personUuid, CreatePersonRequest updateRequest) {
            new ValidatedCrudRequester<CreatePersonResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PERSON_UPDATE,   // ← needs the UUID in the path
                ResponseSpecs.requestReturnsOK())
                .post(updateRequest, personUuid);     // ← POST as confirmed by Postman
    }
}
