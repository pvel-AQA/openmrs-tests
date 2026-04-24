package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePersonResponse {
    private String uuid;
    private String display;
    private String gender;
    private Integer age;
    private String birthdate;
    private Boolean birthdateEstimated;
    private Boolean dead;
    private String deathDate;
    private String causeOfDeath;
    private PreferredName preferredName;
    private PreferredAddress preferredAddress;
    private List<Object> attributes;
    private Boolean voided;
    private String birthtime;
    private Boolean deathdateEstimated;
    private List<Link> links;
    private String resourceVersion;
}
