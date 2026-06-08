package api.models.enums;

import lombok.Getter;

@Getter
public enum VisitTypeEnum {
    FACILITY_VISIT("Facility Visit"),
    HOME_VISIT("Home Visit"),
    OPD_VISIT("OPD Visit"),
    OFFLINE_VISIT("Offline Visit"),
    GROUP_SESSION("Group Session");

    private final String displayName;

    VisitTypeEnum(String displayName) {
        this.displayName = displayName;
    }
}
