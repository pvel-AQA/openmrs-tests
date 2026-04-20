package api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserProperties {
    String defaultLocale;
    String lastLoginTimestamp;
    String loginAttempts;
    String lockoutTimestamp;
    String starredPatientLists;
    String patientsVisited;
}
