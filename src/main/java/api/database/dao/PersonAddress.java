package api.database.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonAddress {
    private String uuid;
    private Boolean preferred;
    private String cityVillage;
    private String country;
    private String address1;
    private String address2;
    private String stateProvince;
    private String postalCode;
}
