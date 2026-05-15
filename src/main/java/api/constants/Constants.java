package api.constants;

public final class Constants {
    public static String[] personFieldsToBeGenerated = new String[]{"gender", "birthdate", "birthdateEstimated",
            "dead", "addresses", "attributes"};
    public static String[] nameFieldsToBeGenerated = new String[]{"givenName", "middleName", "familyName"};
    public static String ClinicNameToGetLocationUuid = "Outpatient";
    public static final boolean PREFERRED_IDENTIFIER_TRUE = true;

    private Constants() {
    }
}
