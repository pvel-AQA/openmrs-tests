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
import common.utils.DateUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CreatePatientTest extends BaseTest {
    private static final boolean PREFERRED_IDENTIFIER_TRUE = true;
    private static final String[] NAMES_FIELDS_TO_BE_GENERATED = new String[]{"givenName", "middleName", "familyName"};

    @Test
    public void knownPatientCanBeCreatedWithOnlyMandatoryDataTest() {
        PersonName personName = PartialEntityGenerator.generate(PersonName.class, NAMES_FIELDS_TO_BE_GENERATED);

        CreatePersonRequest person = CreatePersonRequest.builder()
                .gender(Gender.M.toString())
                .names(List.of(personName)).build();

        IdentifiersForPatientCreation identifiers = AdminSteps.prepareIdentifiersForPatientCreation(
                ClinicName.OUTPATIENT.getClinicName(), PREFERRED_IDENTIFIER_TRUE);

        CreatePatientRequest createPatientRequest = CreatePatientRequest.builder()
                .identifiers(List.of(identifiers))
                .person(person)
                .build();

        CreatePatientResponse createdPatient = new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT,
                ResponseSpecs.requestReturnsCreated())
                .post(createPatientRequest);

        softly.assertThat(createdPatient.getUuid()).isNotEmpty();
        softly.assertThat(createdPatient.getUuid()).isEqualTo(createdPatient.getPerson().getUuid());
        softly.assertThat(createdPatient.getPerson().getGender()).isEqualTo(person.getGender());
        softly.assertThat(createdPatient.getDisplay())
                .isEqualTo(String.format("%s - %s %s %s", identifiers.getIdentifier(), personName.getGivenName(),
                        personName.getMiddleName(), personName.getFamilyName()));

        CreatePatientResponse foundPatient = AdminSteps.findPatientByUuid(createdPatient.getUuid());

        assertThat(createdPatient).usingRecursiveComparison().isEqualTo(foundPatient);
    }

    @Test
    public void knownPatientCanBeCreatedWithValidDataTest() {
        final String identifierResponsePrefix = "OpenMRS ID = ";
        final String[] personFieldsToBeGenerated = new String[]{"gender", "birthdate", "birthdateEstimated",
                "dead", "addresses", "attributes"};

        PersonName personName = PartialEntityGenerator.generate(PersonName.class, NAMES_FIELDS_TO_BE_GENERATED);

        CreatePersonRequest person = PartialEntityGenerator.generate(CreatePersonRequest.class, personFieldsToBeGenerated);
        person.setNames(List.of(personName));
        person.setBirthdate(RandomDataGenerator.generateValidDate());
        int expectedDate = DateUtils.calculateAge(person.getBirthdate());

        IdentifiersForPatientCreation identifiers = AdminSteps.prepareIdentifiersForPatientCreation(
                ClinicName.OUTPATIENT.getClinicName(), PREFERRED_IDENTIFIER_TRUE);

        CreatePatientRequest createPatientRequest = CreatePatientRequest.builder()
                .identifiers(List.of(identifiers))
                .person(person)
                .build();

        CreatePatientResponse createdPatient = new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT,
                ResponseSpecs.requestReturnsCreated())
                .post(createPatientRequest);

        ModelAssertions.assertThatModels(createPatientRequest, createdPatient).match();
        softly.assertThat(createdPatient.getUuid()).isNotEmpty();
        softly.assertThat(createdPatient.getUuid()).isEqualTo(createdPatient.getPerson().getUuid());
        softly.assertThat(createdPatient.getDisplay())
                .isEqualTo(String.format("%s - %s %s %s", identifiers.getIdentifier(), personName.getGivenName(),
                        personName.getMiddleName(), personName.getFamilyName()));
        softly.assertThat(createdPatient.getIdentifiers().getFirst().getDisplay())
                .isEqualTo("%s%s", identifierResponsePrefix,
                        createPatientRequest.getIdentifiers().getFirst().getIdentifier());
        softly.assertThat(createdPatient.getPerson().getDisplay())
                .isEqualTo("%s %s %s", createPatientRequest.getPerson().getNames().getFirst().getGivenName(),
                        createPatientRequest.getPerson().getNames().getFirst().getMiddleName(),
                        createPatientRequest.getPerson().getNames().getFirst().getFamilyName());
        softly.assertThat(expectedDate).isEqualTo(createdPatient.getPerson().getAge());

        AddressResponse personAddress = AdminSteps.getPersonAddress(createdPatient.getPerson().getUuid());
        ModelAssertions.assertThatModels(person, personAddress).match();

        CreatePatientResponse foundPatient = AdminSteps.findPatientByUuid(createdPatient.getUuid());

        assertThat(createdPatient).usingRecursiveComparison().isEqualTo(foundPatient);
    }
}
