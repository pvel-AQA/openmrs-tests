package common.testData;

import api.models.*;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import api.requests.steps.AdminSteps;

import java.util.List;


public class PatientsDataForSearch {
    final static String ClinicNameToGetLocationUuid = "Outpatient";
    final static boolean preferredIdentifierTrue = true;

    public static String createPatient(
        String firstName,
        String middleName,
        String lastName,
        String gender,
        int age,
        String dateOfBirth){

        PersonName personName = new PersonName();
        personName.setGivenName(firstName);
        personName.setMiddleName(middleName);
        personName.setFamilyName(lastName);
        CreatePersonRequest person = CreatePersonRequest.builder()
                .gender(gender)
                .age(age)
                //.birthDate(dateOfBirth)
                .names(List.of(personName))
                .build();

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
}
