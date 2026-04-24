package api.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreatePersonRequest {
    private List<PersonName> names;
    private String gender;                  // "M", "F", "U"
    private Integer age;                    // estimated age in years
    private String birthDate;               // date of birth (string format, e.g., "YYYY-MM-DD")
    private Boolean birthDateEstimated;     // true if estimated
    private String birthTime;               // time of birth
    private Boolean dead;                   // true if patient is dead
    private String deathDate;               // date of death
    private String causeOfDeath;            // Concept UUID
    private Boolean deathdateEstimated;     // true if death date is estimated
    private List<Object> addresses;     // addresses array
    private List<Object> attributes;  // attributes array
}
