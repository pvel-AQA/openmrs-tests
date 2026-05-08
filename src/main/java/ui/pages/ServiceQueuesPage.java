package ui.pages;

import ui.components.Header;

public class ServiceQueuesPage extends BasePage<ServiceQueuesPage> {
    Header header = new Header();

    @Override
    public String url() {
        return "/home/service-queues";
    }
}
