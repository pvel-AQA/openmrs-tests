package ui.pages;

import com.codeborne.selenide.Selenide;

import static api.requests.specs.RequestSpecs.fetchSessionCookie;
import static api.requests.specs.RequestSpecs.setCookieInBrowser;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class BasePage<T extends BasePage> {

    public abstract String url();

    public T open() {
        return Selenide.open(url(), (Class<T>) this.getClass());
    }

    public <T extends BasePage> T getPage(Class<T> pageClass) {
        return Selenide.page(pageClass);
    }

    public static void authAsUser(String username, String password) {
        Selenide.open("/");

        io.restassured.http.Cookie sessionCookie = fetchSessionCookie(username, password);
        setCookieInBrowser(sessionCookie);
    }
}
