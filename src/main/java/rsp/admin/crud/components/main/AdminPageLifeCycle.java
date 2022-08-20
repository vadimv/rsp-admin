package rsp.admin.crud.components.main;

import rsp.admin.pubsub.PubSub;
import rsp.page.PageLifeCycle;
import rsp.page.QualifiedSessionId;
import rsp.state.UseState;

public final class AdminPageLifeCycle implements PageLifeCycle<ViewState> {
    private final PubSub pubSub;

    public AdminPageLifeCycle(PubSub pubSub) {
        this.pubSub = pubSub;
    }

    @Override
    public void beforeLivePageCreated(QualifiedSessionId sid, UseState<ViewState> useState) {
        pubSub.subscribe(sid.deviceId, sid.sessionId, message -> {
            synchronized (useState) {
                useState.accept(((ViewState.Success)useState.get()).withoutPrincipal());
            }
        });
    }

    @Override
    public void afterLivePageClosed(QualifiedSessionId sid, ViewState state) {
        pubSub.unsubscribe(sid.deviceId, sid.sessionId);
    }
}
