package ui;

import common.annotations.AdminSession;
import org.junit.jupiter.api.Test;
import ui.pages.PickLocationPage;

public class RegisterPatientTest extends BaseUiTest {
    @Test
    @AdminSession
    public void knownPatientCanBeRegisteredWithValidDataTest() {
        new PickLocationPage().open().pickOutpatientLocationAndConfirm();
    }
}
