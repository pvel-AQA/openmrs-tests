package api;

import api.models.*;
import api.models.comparison.ModelAssertions;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import api.requests.steps.AdminSteps;
import common.generators.PartialEntityGenerator;
import common.generators.RandomDataGenerator;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonTest extends BaseTest{
    final static String[] fieldsToBeGenerated = new String[]{"givenName", "middleName", "familyName"};
    private final PersonName personName = PartialEntityGenerator.generate(PersonName.class, fieldsToBeGenerated);
    private final static List<String> createdUuids = new ArrayList<>();
    private static Boolean PATH_PARAM_PURGE = true;

    @Test
    public void positiveCreatePersonWithMandatoryFieldsTest(){
        CreatePersonRequest createPersonRequest = CreatePersonRequest.builder()
                .names(List.of(personName))
                .age(RandomDataGenerator.randomAge(0,100))
                .gender(RandomDataGenerator.randomGender().toString())
                .build();

        CreatePersonResponse createdPerson = new ValidatedCrudRequester<CreatePersonResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PERSON,
                ResponseSpecs.requestReturnsCreated())
                .post(createPersonRequest);
        createdUuids.add(createdPerson.getUuid());
        CreatePersonResponse foundPerson = AdminSteps.findPersonByUuid(createdPerson.getUuid()); //checked READ too
        ModelAssertions.assertThatModels(createdPerson, foundPerson).match();
    }
    // Test idea: public void positiveCreatePersonWithAddressTest(){
    // Test idea: public void positiveCreatePersonWithAttributes(){

    public static Stream<Arguments> negativeCreatePersonData() {
        return Stream.of(
                Arguments.of("", "middle", "LastName", 21, "M", "You must define the Given Name"),
                Arguments.of("First", "middle", "LastName", -2, "F", "Cannot be a date in the future"),
                Arguments.of("first", "middle", "LastName", 210, "U", "Nonsensical date, please check."));
    }
    @MethodSource("negativeCreatePersonData")
    @ParameterizedTest
    public void negativeCreatePersonTest(String firstName, String middleName, String lastName, int age, String gender) {
        CreatePersonResponse person = AdminSteps.buildAndPostRandomPerson(personName);
        createdUuids.add(person.getUuid());

        PersonName testName = new PersonName();
        testName.setGivenName(firstName);
        testName.setMiddleName(middleName);
        testName.setFamilyName(lastName);

        CreatePersonRequest createPersonRequest = CreatePersonRequest.builder()
                .names(List.of(testName))
                .age(age)
                .gender(gender)
                .build();

        new ValidatedCrudRequester<CreatePersonResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PERSON,
                ResponseSpecs.requestReturnsBadRequest())
                .post(createPersonRequest);

        //how to check error message?
    }

    @Test
    public void positiveUpdatePersonMandatoryFieldsTest(){
        CreatePersonResponse person = AdminSteps.buildAndPostRandomPerson(personName);
        createdUuids.add(person.getUuid());
        String uuidForUpdate = createdUuids.getLast();
        CreatePersonResponse beforeUpdate = AdminSteps.findPersonByUuid(uuidForUpdate);

        PersonName updatedName = new PersonName();
        updatedName.setGivenName(RandomDataGenerator.randomString(7));
        updatedName.setMiddleName(RandomDataGenerator.randomString(8));
        updatedName.setFamilyName(RandomDataGenerator.randomString(5));

        String newGender = RandomDataGenerator.randomGender().toString();
        int newAge = RandomDataGenerator.randomAge(0,90);

        CreatePersonRequest updateRequest = CreatePersonRequest.builder()
                .names(List.of(updatedName))
                .gender(newGender)
                .age(newAge)
                .build();

        AdminSteps.updatePerson(uuidForUpdate, updateRequest);

        CreatePersonResponse afterUpdate = AdminSteps.findPersonByUuid(uuidForUpdate);

        if (beforeUpdate.getGender() != afterUpdate.getGender()) {
            softly.assertThat(afterUpdate.getGender())
                    .as("Gender should be updated")
                    .isEqualTo(newGender);
        }

        softly.assertThat(afterUpdate.getPreferredName().getDisplay())
                .as("Name should be updated")
                .contains(updatedName.getGivenName());

        softly.assertThat(afterUpdate.getGender())
                .as("Gender should have changed")
                .isNotEqualTo(beforeUpdate.getGender()); //flacky result
    }
    // Test idea: public void positiveUpdatePersonAddressTest(){
    // Test idea: public void positiveUpdatePersonAttributes(){

    @Test
    public void deletePersonVoidedTest(){
        CreatePersonResponse person = AdminSteps.buildAndPostRandomPerson(personName);
        createdUuids.add(person.getUuid());
        String uuidForDelete = createdUuids.getLast();

        new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PERSON_DELETE,
                ResponseSpecs.requestReturnsNoContent())
                .delete(uuidForDelete);

        CreatePersonResponse personDataAfterDelete = AdminSteps.findPersonByUuid(uuidForDelete);
        softly.assertThat(personDataAfterDelete.getVoided()).isEqualTo(true);
    }

    @Test
    public void deletePersonPurgeTest(){
        CreatePersonResponse person = AdminSteps.buildAndPostRandomPerson(personName);
        createdUuids.add(person.getUuid());
        String uuidForDelete = createdUuids.getLast();

        new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PERSON_DELETE,
                ResponseSpecs.requestReturnsNoContent())
                .delete(uuidForDelete, PATH_PARAM_PURGE);

        new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PERSON_READ,
                ResponseSpecs.requestReturnsNotFound())
                .get(uuidForDelete,CreatePersonResponse.class);
    }

    // Test idea for delete: If not authenticated or authenticated user does not have sufficient privileges, 401 Unauthorized status is returned.
    // Test idea for list: Retrieve a person by their UUID. Returns a 404 Not Found status if the person does not exist in the system. If the user is not logged in to perform this action, a 401 Unauthorized status is returned.

    @AfterEach
    public void deleteTestPersons() {
        System.out.println("\u001B[1m" + "\u001B[34m" + createdUuids + "\u001B[0m");
        createdUuids.forEach(uuid -> {
            try {
                AdminSteps.deletePersonByUuid(uuid, PATH_PARAM_PURGE);
            } catch (Exception e) {
                System.err.println("Failed to delete patient: " + uuid + " — " + e.getMessage());
            }
        });
    }
}
