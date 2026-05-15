package api.requests.steps;

import api.constants.Constants;
import api.models.*;
import api.models.patient.IdentifiersForPatientUpdate;
import api.models.patient.PersonForPatientUpdate;
import api.models.patient.PersonNameForPatientUpdate;
import api.models.patient.UpdatePatientRequest;
import api.models.roles.AdminLogin;
import api.models.ui.RegisterMandatoryFieldsPatientUi;
import api.models.ui.RegisterPatientUi;
import api.models.visit.CreateVisitRequest;
import api.models.visit.CreateVisitResponse;
import api.models.visit.VisitTypeResponse;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.AuthRequester;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.skeleton.requesters.VisitTypeEnum;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import common.generators.PartialEntityGenerator;
import common.generators.RandomDataGenerator;
import ui.pages.PatientRegistrationPage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AdminSteps {
    public static final boolean PREFERRED_IDENTIFIER_TRUE = true;
    public static final String[] NAMES_FIELDS_TO_BE_GENERATED = Constants.nameFieldsToBeGenerated;
    public static final String[] PERSON_FIELDS_TO_BE_GENERATED = Constants.personFieldsToBeGenerated;
    public static final String[] UI_MANDATORY_NAMES_FIELDS_TO_BE_GENERATED = PatientRegistrationPage.uiMandatoryNameFieldsToBeGenerated;
    public static final String[] UI_MANDATORY_PATIENT_FIELDS_TO_BE_GENERATED = PatientRegistrationPage.uiPatientFieldsToBeGenerated;

    private AdminSteps() {

    }

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

    public static ErrorResponse attemptToFindDeletedPatientByUuid(String patientUuid) {
        return new ValidatedCrudRequester<ErrorResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT_SEARCH_AFTER_DELETE,
                ResponseSpecs.requestReturnNotFoundForDeletedObject())
                .get(patientUuid, ErrorResponse.class);
    }

    public static ErrorResponse attemptToFindDeletedPersonByUuid(String personUuid) {
        return new ValidatedCrudRequester<ErrorResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PERSON_READ_DELETED,
                ResponseSpecs.requestReturnNotFoundForDeletedObject())
                .get(personUuid, ErrorResponse.class);
    }

    private static List<VisitTypeResponse> searchVisitTypeByName(String name) {
        return new ValidatedCrudRequester<VisitTypeResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.VISIT_TYPE,
                ResponseSpecs.requestReturnsOK())
                .getAll(
                        new CrudRequester.QueryBuilder()
                                .q(name)
                                .vEqualsFull()
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

        return createPatient(createPatientRequest);
    }

    public static CreatePatientResponse createPatient(CreatePatientRequest createPatientRequest) {
        return new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT,
                ResponseSpecs.requestReturnsCreated())
                .post(createPatientRequest);
    }

    public static CreatePersonRequest createPerson() {
        PersonName personName = PartialEntityGenerator.generate(PersonName.class, NAMES_FIELDS_TO_BE_GENERATED);

        CreatePersonRequest person = PartialEntityGenerator.generate(CreatePersonRequest.class, PERSON_FIELDS_TO_BE_GENERATED);
        person.setNames(List.of(personName));
        person.setBirthdate(RandomDataGenerator.generateValidDate());

        return person;
    }

    public static CreatePatientRequest createPatientRequest() {
        PersonName personName = PartialEntityGenerator.generate(PersonName.class, NAMES_FIELDS_TO_BE_GENERATED);

        CreatePersonRequest person = PartialEntityGenerator.generate(CreatePersonRequest.class, PERSON_FIELDS_TO_BE_GENERATED);
        person.setNames(List.of(personName));
        person.setBirthdate(RandomDataGenerator.generateValidDate());

        IdentifiersForPatientCreation identifiers = AdminSteps.prepareIdentifiersForPatientCreation(
                ClinicName.OUTPATIENT.getClinicName(), PREFERRED_IDENTIFIER_TRUE);

        return CreatePatientRequest.builder()
                .identifiers(List.of(identifiers))
                .person(person)
                .build();
    }

    public static RegisterPatientUi createPatientForUi() {
        PersonName personName = PartialEntityGenerator.generate(PersonName.class, NAMES_FIELDS_TO_BE_GENERATED);

        RegisterPatientUi patient = PartialEntityGenerator.generate(RegisterPatientUi.class, PERSON_FIELDS_TO_BE_GENERATED);
        patient.setNames(List.of(personName));
        patient.setBirthdate(RandomDataGenerator.generateValidDateUiFormat());

        return patient;
    }

    public static RegisterMandatoryFieldsPatientUi createPatientWithMandatoryFieldsForUi() {
        PersonName personName = PartialEntityGenerator.generate(PersonName.class, UI_MANDATORY_NAMES_FIELDS_TO_BE_GENERATED);

        RegisterMandatoryFieldsPatientUi patient = PartialEntityGenerator
                .generate(RegisterMandatoryFieldsPatientUi.class, UI_MANDATORY_PATIENT_FIELDS_TO_BE_GENERATED);
        patient.setNames(List.of(personName));
        patient.setBirthdate(RandomDataGenerator.generateValidDateUiFormat());

        return patient;
    }

    public static CreatePatientRequest createPatientRequest(CreatePersonRequest personRequest) {
        IdentifiersForPatientCreation identifiers = AdminSteps.prepareIdentifiersForPatientCreation(
                ClinicName.OUTPATIENT.getClinicName(), PREFERRED_IDENTIFIER_TRUE);

        return CreatePatientRequest.builder()
                .identifiers(List.of(identifiers))
                .person(personRequest)
                .build();
    }

    public static CreateVisitResponse createVisit(CreatePatientResponse patient) {
        CreateVisitRequest request = CreateVisitRequest.builder()
                .patient(patient.getUuid())
                .visitType(getVisitTypeUuid(VisitTypeEnum.FACILITY_VISIT))
                .startDatetime(RandomDataGenerator.generateVisitStartDatetime())
                .location(getLocationUuidByName(ClinicName.OUTPATIENT.getClinicName()))
                .indication(RandomDataGenerator.generateVisitIndication())
                .build();

        return new ValidatedCrudRequester<CreateVisitResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.VISIT,
                ResponseSpecs.requestReturnsCreated())
                .post(request);
    }

    public static void updateVisit(String visitUuid, String newStartDatetime) {
        CreateVisitRequest updateRequest = CreateVisitRequest.builder()
                .startDatetime(newStartDatetime)
                .build();

        new ValidatedCrudRequester<CreateVisitResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.VISIT_BY_UUID,
                ResponseSpecs.requestReturnsOK())
                .post(updateRequest, visitUuid);
    }

    public static void deletePatientByUuid(String patientUuid) {
        new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT_DELETE,
                ResponseSpecs.requestReturnsNoContent())
                .delete(patientUuid);
    }

    public static void deletePatientByUuid(String patientUuid, Boolean purge) {
        new CrudRequester(
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

    public static CreatePersonResponse createPerson(CreatePersonRequest createPersonRequest) {
        return new ValidatedCrudRequester<CreatePersonResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PERSON,
                ResponseSpecs.requestReturnsCreated())
                .post(createPersonRequest);
    }

    public static void deletePersonByUuid(String uuid, Boolean purge) {
        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.PERSON_DELETE,
                ResponseSpecs.requestReturnsNoContent())
                .delete(uuid, purge);
    }

    public static void updatePerson(String personUuid, CreatePersonRequest updateRequest) {
        new ValidatedCrudRequester<CreatePersonResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PERSON_UPDATE,
                ResponseSpecs.requestReturnsOK())
                .post(updateRequest, personUuid);
    }

    public static UpdatePatientRequest prepareUpdatePatientRequest(CreatePatientRequest createPatientRequest, CreatePatientResponse createPatientResponse) {
        PersonName personName = createPatientRequest.getPerson().getNames().getFirst();
        PersonNameForPatientUpdate personNameForPatientUpdate = PersonNameForPatientUpdate.builder()
                .givenName(personName.getGivenName())
                .middleName(personName.getMiddleName())
                .familyName(personName.getFamilyName())
                .preferred(personName.getPreferred())
                .uuid(createPatientResponse.getPerson().getPreferredName().getUuid())
                .build();

        CreatePersonRequest createPersonRequest = createPatientRequest.getPerson();
        PersonForPatientUpdate personForPatientUpdate = PersonForPatientUpdate.builder()
                .names(List.of(personNameForPatientUpdate))
                .gender(createPersonRequest.getGender())
                .birthdate(createPersonRequest.getBirthdate())
                .birthdateEstimated(createPersonRequest.getBirthdateEstimated())
                .dead(createPersonRequest.getDead())
                .addresses(createPersonRequest.getAddresses())
                .attributes(createPersonRequest.getAttributes())
                .build();

        IdentifiersForPatientCreation identifiersForPatientCreation = createPatientRequest.getIdentifiers().getFirst();
        IdentifiersForPatientUpdate identifiersForPatientUpdate = IdentifiersForPatientUpdate.builder()
                .uuid(createPatientResponse.getIdentifiers().getFirst().getUuid())
                .identifier(identifiersForPatientCreation.getIdentifier())
                .identifierType(identifiersForPatientCreation.getIdentifierType())
                .location(identifiersForPatientCreation.getLocation())
                .preferred(identifiersForPatientCreation.isPreferred())
                .build();

        return UpdatePatientRequest.builder()
                .identifiers(List.of(identifiersForPatientUpdate))
                .person(personForPatientUpdate)
                .build();
    }

    private static PersonName buildPersonName(String firstName, String middleName, String lastName) {
        return PersonName.builder()
                .givenName(firstName)
                .middleName(middleName)
                .familyName(lastName)
                .build();
    }

    public static CreatePatientResponse createPatientWithAge(String firstName, String middleName, String lastName, String gender, int age) {
        PersonName personName = buildPersonName(firstName, middleName, lastName);
        CreatePersonRequest person = CreatePersonRequest.builder()
                .gender(gender)
                .age(age)
                .names(List.of(personName))
                .build();

        CreatePatientRequest patient = createPatientRequest(person);
        return createPatient(patient);
    }

    public static CreatePatientResponse createPatientWithDOB(String firstName, String middleName, String lastName, String gender, String dateOfBirth) {
        PersonName personName = buildPersonName(firstName, middleName, lastName);
        CreatePersonRequest person = CreatePersonRequest.builder()
                .gender(gender)
                .birthdate(dateOfBirth)
                .names(List.of(personName))
                .build();
        CreatePatientRequest patient = createPatientRequest(person);
        return createPatient(patient);
    }

    public static CreatePatientResponse createUnknownPatient() {
        PersonName personName = buildPersonName("UNKNOWN", "", "UNKNOWN");
        CreatePersonRequest person = CreatePersonRequest.builder()
                .gender(RandomDataGenerator.randomGender().toString())
                .age(RandomDataGenerator.randomAge(0, 100))
                .names(List.of(personName))
                .build();
        CreatePatientRequest patient = createPatientRequest(person);
        return createPatient(patient);
    }

    public static List<String> createPatientsForSearch(int count, Boolean knownDOB, String generatedString) {
        List<String> createdUuids = new ArrayList<>();
        String letters = "abcdefghijklmnopqrstuvwxyz";
        String firstName;
        String middleName;
        String lastName;
        String gender;
        int age;
        String dateOfBirth;
        for (int i = 0; i < count; i++) {
            firstName = generatedString.substring(0, 4).toLowerCase() + "FN" + letters.charAt(i); //abcd(e)FNa
            middleName = generatedString.substring(0, 4).toUpperCase() + "MN" + letters.charAt(i); //
            lastName = generatedString.substring(0, 5).toLowerCase() + "LN" + letters.charAt(i); //abcdeLNa
            gender = RandomDataGenerator.randomGender().toString();
            if (knownDOB) {
                dateOfBirth = RandomDataGenerator.randomDateBetween(LocalDate.parse("1900-01-01"), LocalDate.now());
                createdUuids.add(createPatientWithDOB(firstName, middleName, lastName, gender, dateOfBirth).getUuid());
            } else {
                age = RandomDataGenerator.randomAge(20, 70);
                createdUuids.add(createPatientWithAge(firstName, middleName, lastName, gender, age).getUuid());
            }
        }
        return createdUuids;
    }

    public static List<CreatePatientResponse> searchPatientsByString(String searchText) {
        return new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT,
                ResponseSpecs.requestReturnsOK())
                .getAll(new CrudRequester.QueryBuilder().q(searchText).build(), CreatePatientResponse.class);
    }

    public static String retrieveJSessionValue(String username, String password) {
        return new AuthRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.SESSION,
                ResponseSpecs.requestReturnsOK(),
                ResponseSpecs.requestReturnsSetCookieHeader())
                .get(username, password)
                .extract()
                .sessionId();
    }

    public static String retrieveJSessionValue(AdminLogin admin) {
        return retrieveJSessionValue(admin.getUsername(), admin.getPassword());
    }

    public static boolean isVisitExists(String patientUuid) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("includeInactive", "false");
            params.put("v", "full");
            params.put("limit", 100);

            CrudRequester requester = new CrudRequester(
                    RequestSpecs.adminSpec(),
                    Endpoint.VISIT,
                    ResponseSpecs.requestReturnsOK());

            var response = requester.getAll(params, BaseModel.class);
            String responseString = response.extract().asString();

            return responseString.contains(patientUuid);
        } catch (Exception e) {
            System.out.println("API check failed: " + e.getMessage());
            return false;
        }
    }
}
