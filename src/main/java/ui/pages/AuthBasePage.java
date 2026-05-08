package ui.pages;

import ui.components.Header;

public abstract class AuthBasePage<T extends AuthBasePage> extends BasePage<AuthBasePage> {
    public Header header = new Header();
}
