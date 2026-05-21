package ui.components;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import ui.pages.BasePage;

public abstract class BaseComponent {
    protected abstract SelenideElement getSelf();

    public BaseComponent shouldBeLoaded() {
        getSelf().shouldBe(Condition.visible);
        return this;
    }

    public <T extends BasePage> T getPage(Class<T> pageClass) {
        return Selenide.page(pageClass);
    }
}
