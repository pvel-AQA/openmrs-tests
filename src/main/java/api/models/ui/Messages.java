package api.models.ui;

import lombok.Getter;

@Getter
public enum Messages {
    SEARCH_RESULTS_ERROR_MESSAGE ("Sorry, there was a an error. You can try to reload this page, or contact the site administrator and quote the error code above."),
    ERROR_TITLE_TEXT ("Error"),
    SEARCH_INPUT_FIELD_DEFAULT_TEXT ("Search for a patient by name or identifier number"),
    LOGIN_ERROR_MESSAGE ("Invalid username or password");

    private final String text;

    Messages(String error) {
        this.text = error;
    }
}
