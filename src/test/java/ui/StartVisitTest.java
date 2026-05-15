package ui;

import api.models.CreatePatientResponse;
import api.models.ui.VisitTab;
import api.requests.skeleton.requesters.VisitTypeEnum;
import api.requests.steps.AdminSteps;
import com.codeborne.selenide.WebDriverRunner;
import common.annotations.AdminSession;
import org.junit.jupiter.api.Test;

import ui.pages.*;

import static org.assertj.core.api.Assertions.assertThat;

public class StartVisitTest extends BaseUiTest {

    @Test
    @AdminSession
    public void startVisit() {
        CreatePatientResponse createdPatient = AdminSteps.createPatient();
        String patientUuid = createdPatient.getUuid();

        new PickLocationPage().open().pickOutpatientLocationAndConfirm();

        VisitPage visitPage = new VisitPage().open(patientUuid);
        visitPage.waitPatientSummaryLoaded();

        visitPage.openActionsMenu()
                .selectAddVisit()
                .waitStartVisitModal()
                .selectVisitTab(VisitTab.NEW)
                .selectUbuntuHospitalLocation()
                .selectVisitType(VisitTypeEnum.FACILITY_VISIT)
                .confirmStartVisitAndWaitForClose();

        visitPage.checkActiveVisitIsStarted();

        assertThat(WebDriverRunner.url())
                .as("URL should contain patient UUID")
                .contains(patientUuid);

        boolean visitExists = AdminSteps.isVisitExists(patientUuid);
        assertThat(visitExists)
                .as("Visit should exist in API response")
                .isTrue();
    }
}
