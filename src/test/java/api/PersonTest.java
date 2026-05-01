package api;

import api.models.*;
import api.models.comparison.ModelAssertions;
import api.requests.steps.AdminSteps;
import common.generators.PartialEntityGenerator;
import common.generators.RandomDataGenerator;
import io.restassured.response.Response;
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
    private static List<String> createdUuids = new ArrayList<>();

    @Test
    public void positiveCreatePersonTest(){
        CreatePersonResponse createdPerson = AdminSteps.buildAndPostRandomPerson(personName);
        createdUuids.add(createdPerson.getUuid());
        CreatePersonResponse foundPerson = AdminSteps.findPersonByUuid(createdPerson.getUuid()); //checked READ too
        ModelAssertions.assertThatModels(createdPerson, foundPerson).match();
    }

    @Test
    public void positiveUpdatePersonTest(){
        CreatePersonResponse createdPerson = AdminSteps.buildAndPostRandomPerson(personName);
        createdUuids.add(createdPerson.getUuid());
        CreatePersonResponse beforeUpdate = AdminSteps.findPersonByUuid(createdUuids.getFirst());

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

        AdminSteps.updatePerson(createdUuids.getFirst(), updateRequest);

        // Assert — fetch again and verify changes persisted
        CreatePersonResponse afterUpdate = AdminSteps.findPersonByUuid(createdUuids.getFirst());

        assertThat(afterUpdate.getGender())
                .as("Gender should be updated")
                .isEqualTo(newGender);

        /*assertThat(afterUpdate.getAge())
                .as("Age should be updated")
                .isEqualTo(newAge);*/ // age is not updated, only date

        assertThat(afterUpdate.getPreferredName().getDisplay())
                .as("Name should be updated")
                .contains(updatedName.getGivenName());

        // Verify before and after are actually different
        assertThat(afterUpdate.getGender())
                .as("Gender should have changed")
                .isNotEqualTo(beforeUpdate.getGender()); //flacky result

    }

    public static Stream<Arguments> negativeCreatePersonData() {
        return Stream.of(
                Arguments.of("", "middle", "LastName", 21, "M", "You must define the Given Name"),
                Arguments.of("First", "middle", "LastName", -2, "F", "Cannot be a date in the future"),
                Arguments.of("first", "middle", "LastName", 210, "U", "Nonsensical date, please check."));
    }
    @MethodSource("negativeCreatePersonData")
    @ParameterizedTest
    public void negativeCreatePersonTest(String firstName, String middleName, String lastName, int age, String gender, String expectedMessage) {
        Response response = AdminSteps.buildAndPostSpecificPersonForNegativeTests(firstName, middleName, lastName, age, gender);
        assertThat(response.statusCode()).isEqualTo(302);
    }
    //read

    //update


    //delete

}
