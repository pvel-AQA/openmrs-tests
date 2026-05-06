package api.constants;

public class Constants {
    public static String[] personFieldsToBeGenerated = new String[]{"gender", "birthdate", "birthdateEstimated",
            "dead", "addresses", "attributes"};
    public static String[] nameFieldsToBeGenerated = new String[]{"givenName", "middleName", "familyName"};
    public static String ClinicNameToGetLocationUuid = "Outpatient";
    public static boolean preferredIdentifierTrue = true;

    private Constants() {
    }
}
