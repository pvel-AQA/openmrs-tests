package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class ServiceQueuesPage extends BasePage<ServiceQueuesPage> {
    @Override
    public String url() {
        return "/home/service-queues";
    }

    private SelenideElement serviceQueueHeader = $("[data-testid='patient-queue-header']");

    private SelenideElement serviceQueuesDashboardLink = $("[data-extension-id='service-queues-dashboard-link']");
    private SelenideElement clinicalAppointmentDashboardLink = $("[data-extension-id='clinical-appointment-dashboard-link']");
    private SelenideElement patientListsDashboardLink = $("[data-extension-id='patient-lists-dashboard-link']");
    private SelenideElement laboratoryDashboardLink = $("[data-extension-id='laboratory-dashboard-link']");
    private SelenideElement wardDashboardLink = $("[data-extension-id='ward-dashboard-link']");
    private SelenideElement billingDashboardLink = $("[data-extension-id='billing-dashboard-link']");

    private SelenideElement checkedInPatients = $("//label[text()='Checked in patients']");
    private SelenideElement waitingFor = $("//span[text()='Patients']");
    private SelenideElement averageWaitTimeToday = $("//label[text()='Average wait time today']");
    private SelenideElement patientsCurrentlyInQueue = $("//h2[text()='Patients Currently In Queue']");

}
