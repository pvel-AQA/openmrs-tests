package api.database.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Patient {
    private String patientId;
    private Person person;
    private boolean voided;
    private List<Identifier> identifiers;

//    @Override
//    public Patient mapRow(ResultSet rs) throws SQLException {
//        Patient patient = Patient.builder()
//                .voided(rs.getBoolean("voided"))
//                .build();
//
//        Person person = Person.builder()
//                .uuid(rs.getString("uuid"))
//                .birthdate(rs.getString("birthdate"))
//                .birthdateEstimated(rs.getBoolean("birthdate_estimated"))
//                .dead(rs.getBoolean("dead"))
//                .deathDate(rs.getString("death_date"))
//                .causeOfDeath(rs.getString("cause_of_death"))
//                .voided(rs.getBoolean("voided"))
//                .birthtime(rs.getString("birthtime"))
//                .deathdateEstimated(rs.getBoolean("deathdate_estimated"))
//                .build();
//
//        return null;
//    }
}
