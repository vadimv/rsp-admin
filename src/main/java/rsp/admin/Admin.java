package rsp.admin;

import rsp.App;
import rsp.admin.auth.Auth;
import rsp.admin.crud.components.main.*;
import rsp.admin.pubsub.PubSub;

import java.util.Arrays;
import java.util.List;

public final class Admin {
    private final String title;
    private final List<ResourceView<?>> resources;
    private final Auth auth = new Auth();
    private final PubSub pubSub = new PubSub();

    public Admin(String title, ResourceView<?>... resources) {
        this.title = title;
        this.resources = Arrays.asList(resources);
    }

    public App<ViewState> app() {
        final AdminResources res = new AdminResources(resources);
        final Principals principals = new Principals();
        final AdminRouting routing = new AdminRouting(res, principals);
        final AdminView adminView = new AdminView(title,
                                                  routing,
                                                  res,
                                                  auth,
                                                  principals,
                                                  pubSub);
        final AdminPageLifeCycle pageLifeCycle = new AdminPageLifeCycle(pubSub);
        return new App<>(routing.routes(),
                         adminView).stateToPath(AdminRouting::stateToPath)
                                   .pageLifeCycle(pageLifeCycle);
    }

}
