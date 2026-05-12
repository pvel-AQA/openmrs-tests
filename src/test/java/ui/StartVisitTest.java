package ui;

import api.models.CreatePatientRequest;
import api.models.CreatePatientResponse;
import api.models.roles.AdminLogin;
import api.requests.steps.AdminSteps;
import common.annotations.AdminSession;
import common.generators.RandomDataGenerator;
import org.junit.jupiter.api.Test;
import ui.pages.BasePage;
import ui.pages.LoginPage;
import ui.pages.PickLocationPage;
import ui.pages.VisitPage;

public class StartVisitTest extends BaseUiTest {

    @Test
    @AdminSession
    public void startVisit() {

     //  AdminLogin admin = AdminLogin.getAdmin();

     //  new LoginPage().open()
     //          .populateUserNameField(admin.getUsername())
     //          .clickContinueButton()
     //          .populatePasswordField(admin.getPassword())
     //          .clickLogInButton()
     //          .getPage(PickLocationPage.class)
     //          .pickOutpatientLocationAndConfirm();

     //  //CREATE PATIENT
     //  CreatePatientRequest patient = AdminSteps.createPatientRequest();
     //  patient.getPerson().setBirthdate(RandomDataGenerator.generateValidDate());

     //  CreatePatientResponse createdPatient =
     //          AdminSteps.createPatient(patient);

     //  String patientUuid = createdPatient.getUuid();

     //  System.out.println("PATIENT UUID = " + patientUuid);

     //  //OPEN PATIENT PAGE
     //  VisitPage visitPage = new VisitPage(patientUuid);

     //  visitPage
     //          .open()
     //          .waitPatientSummaryLoaded();

     //  System.out.println("CURRENT URL = " +
     //          com.codeborne.selenide.WebDriverRunner.url());
     //

     //  //OPEN ACTIONS MENU
     //  visitPage
     //          .openActionsMenu()
     //          .selectAddVisit();

     //  //WAIT VISIT MODAL
     //  visitPage
     //          .waitStartVisitModal();

     //  //CONFIGURE VISIT
     //  visitPage
     //          .selectVisitTab("new")
     //          .selectUbuntuHospitalLocation()
     //          .selectVisitType("Facility Visit");

     //  // ASSERTIONS
     //  visitPage
     //          .checkStartVisitHeaderIsVisible()
     //          .checkTheVisitIsLegendIsVisible()
     //          .checkVisitLocationTextIsVisible()
     //          .checkVisitTypeTextIsVisible();

        // CREATE PATIENT
        CreatePatientRequest patient = AdminSteps.createPatientRequest();
        patient.getPerson().setBirthdate(RandomDataGenerator.generateValidDate());

        CreatePatientResponse createdPatient =
                AdminSteps.createPatient(patient);

        String patientUuid = createdPatient.getUuid();
        System.out.println("PATIENT UUID = " + patientUuid);

        //OPEN PATIENT
        VisitPage visitPage = new VisitPage(patientUuid);

        visitPage.open()
                .waitPatientSummaryLoaded();

        System.out.println("CURRENT URL = " +
                com.codeborne.selenide.WebDriverRunner.url());

        // ACTIONS
        visitPage.openActionsMenu()
                .selectAddVisit()
                .waitStartVisitModal();

        // CONFIGURE VISIT
        visitPage.selectVisitTab("new")
                .selectUbuntuHospitalLocation()
                .selectVisitType("facility visit");

        // ASSERT
        visitPage.checkStartVisitHeaderIsVisible()
                .checkTheVisitIsLegendIsVisible();
    }
}


