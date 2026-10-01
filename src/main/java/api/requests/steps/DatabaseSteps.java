package api.requests.steps;

import api.database.Condition;
import api.database.DBRequest;
import api.database.dao.*;
import lombok.Getter;

import java.util.List;

public final class DatabaseSteps {
    private static final String PERSON_ID_COLUMN = "personId";

    @Getter
    public enum Table {
        PATIENT("patient"),
        PERSON("person"),
        PATIENT_IDENTIFIER("patient_identifier"),
        PERSON_NAME("person_name"),
        PERSON_ADDRESS("person_address");

        Table(String tableName) {
            this.tableName = tableName;
        }

        private final String tableName;
    }

    private DatabaseSteps() { }

    public static Patient getPatientByUuid(String uuid) {
        Person person = DBRequest.builder()
                .table(Table.PERSON.tableName)
                .where(Condition.equalTo("uuid", uuid))
                .extractAs(Person.class);

        Patient patient = DBRequest.builder()
                .table(Table.PATIENT.tableName)
                .where(Condition.equalTo("patient_id", person.getPersonId()))
                .extractAs(Patient.class);

        List<Identifier> identifiers = DBRequest.builder()
                .table(Table.PATIENT_IDENTIFIER.tableName)
                .where(Condition.equalTo(PERSON_ID_COLUMN, person.getPersonId()))
                .extractAsList(Identifier.class);
        patient.setIdentifiers(identifiers);

        List<PersonName> personNames = DBRequest.builder()
                .table(Table.PERSON_NAME.tableName)
                .where(Condition.equalTo(PERSON_ID_COLUMN, person.getPersonId()))
                .extractAsList(PersonName.class);
        person.setNames(personNames);

        List<PersonAddress> personAddresses = DBRequest.builder()
                .table(Table.PERSON_ADDRESS.tableName)
                .where(Condition.equalTo(PERSON_ID_COLUMN, person.getPersonId()))
                .extractAsList(PersonAddress.class);
        person.setAddresses(personAddresses);

        return patient;
    }
}
