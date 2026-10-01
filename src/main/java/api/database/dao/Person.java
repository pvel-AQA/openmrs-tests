package api.database.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Person {
    private String personId;
    private String uuid;
    private String birthdate;
    private Boolean birthdateEstimated;
    private Boolean dead;
    private String deathDate;
    private String causeOfDeath;
    private Boolean voided;
    private String birthtime;
    private Boolean deathdateEstimated;
    private List<PersonAddress> addresses;
    private List<PersonAttribute> attributes;
    private List<PersonName> names;
}
