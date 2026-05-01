package api.requests.steps;

import api.models.*;
import api.models.comparison.ModelAssertions;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import common.generators.PartialEntityGenerator;
import common.generators.RandomGenerators;
import common.testData.PatientsDataForSearch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PatientSteps {
    final static String ClinicNameToGetLocationUuid = "Outpatient";
    final static boolean preferredIdentifierTrue = true;

    private static PersonName buildPersonName(String firstName, String middleName, String lastName) {
        PersonName personName = new PersonName();
        personName.setGivenName(firstName);
        personName.setMiddleName(middleName);
        personName.setFamilyName(lastName);
        return personName;
    }

    private static String buildAndPostPatient(CreatePersonRequest person) {
        IdentifiersForPatientCreation identifiers = AdminSteps.prepareIdentifiersForPatientCreation(
                ClinicNameToGetLocationUuid, preferredIdentifierTrue);

        CreatePatientRequest createPatientRequest = CreatePatientRequest.builder()
                .identifiers(List.of(identifiers))
                .person(person)
                .build();

        CreatePatientResponse newPatient = new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT,
                ResponseSpecs.requestReturnsCreated())
                .post(createPatientRequest);

        return newPatient.getUuid();
    }

    public static String createPatientWithAge(String firstName, String middleName, String lastName, String gender, int age){
        PersonName personName = buildPersonName(firstName, middleName, lastName);
        CreatePersonRequest person = CreatePersonRequest.builder()
                .gender(gender)
                .age(age)
                .names(List.of(personName))
                .build();
        return buildAndPostPatient(person);
        }

    public static String createPatientWithDOB(String firstName, String middleName, String lastName, String gender, String dateOfBirth) {
        PersonName personName = buildPersonName(firstName, middleName, lastName);
        CreatePersonRequest person = CreatePersonRequest.builder()
                .gender(gender)
                .birthdate(dateOfBirth)
                .names(List.of(personName))
                .build();
        return buildAndPostPatient(person);
    }

    public static String createUnknownPatient(){
        PersonName personName = buildPersonName("UNKNOWN", "", "UNKNOWN");
        CreatePersonRequest person = CreatePersonRequest.builder()
                .gender(RandomGenerators.randomGender().toString())
                .age(RandomGenerators.randomAge(0,100))
                .names(List.of(personName))
                .build();
        return buildAndPostPatient(person);
    }

    public static List<String> createPatients(int count, Boolean nameKnown, Boolean dateOfBirthKnown) {
        final String[] fieldsToBeGenerated = new String[]{"givenName", "middleName", "familyName"};
        List<String> createdUuids = new ArrayList<>();
        String firstName;
        String middleName;
        String lastName;
        String gender;
        int age;
        String dateOfBirth;
        createdUuids.add("12345");
        PersonName personName = PartialEntityGenerator.generate(PersonName.class, fieldsToBeGenerated);

        CreatePersonRequest person = CreatePersonRequest.builder()
                .gender(RandomGenerators.randomGender().toString())
                .names(List.of(personName)).build();
        return createdUuids;
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
            gender = RandomGenerators.randomGender().toString();
            if (knownDOB) {
                dateOfBirth = RandomGenerators.randomDateBetween(LocalDate.parse("1900-01-01"), LocalDate.now());
                createdUuids.add(createPatientWithDOB(firstName, middleName, lastName, gender, dateOfBirth));
            }
            else {
                age = RandomGenerators.randomAge(20,70);
                createdUuids.add(createPatientWithAge(firstName, middleName, lastName, gender, age));
            }
        }
        return createdUuids;
    }

    public static List<CreatePatientResponse> searchPatientsByString(String searchText) {
        return new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT,
                ResponseSpecs.requestReturnsOK())
                .getWithParams(new CrudRequester.QueryBuilder().q(searchText).build(), CreatePatientResponse.class);
    }

    // delete patients move from AdminSteps or not?
}
