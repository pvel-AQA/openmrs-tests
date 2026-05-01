package api;

import api.models.CreatePatientResponse;
import api.requests.steps.AdminSteps;
import api.requests.steps.PatientSteps;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class DeletePatientTest {
    private final String createdUuid = PatientSteps.createUnknownPatient();

    @Test
    void deletePatientTest() {
            try {
                AdminSteps.deletePatientByUuid(createdUuid, true);
            } catch (Exception e) {
                System.err.println("Failed to delete patient: " + createdUuid + " — " + e.getMessage());
            }
            assertThat(AdminSteps.findPatientByUuid(createdUuid).getDisplay()).isEmpty();


    }
}
