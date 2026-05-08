package ui.components;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

public abstract class BaseComponent {
    protected abstract SelenideElement getSelf();

    public BaseComponent shouldBeLoaded() {
        getSelf().shouldBe(Condition.visible);
        return this;
    }
}
