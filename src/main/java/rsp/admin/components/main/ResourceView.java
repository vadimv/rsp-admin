package rsp.admin.components.main;

import rsp.Component;
import rsp.admin.components.details.DetailsViewState;
import rsp.admin.components.list.ListView;
import rsp.admin.data.entity.KeyedEntity;
import rsp.admin.data.provider.EntityService;
import rsp.html.DocumentPartDefinition;
import rsp.state.UseState;
import rsp.util.StreamUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static rsp.html.HtmlDsl.*;
import static rsp.state.UseState.readWrite;

public class ResourceView<T> implements Component<ResourceView.State<T>> {

    public final int DEFAULT_PAGE_SIZE = 10;

    public final String name;
    public final String title;
    public final EntityService<String, T> entityService;

    private final Component<ListView.ListViewState<String, T>> listComponent;
    private final Optional<Component<DetailsViewState<T>>> editComponent;
    private final Optional<Component<DetailsViewState<T>>> createComponent;

    public ResourceView(String name,
                        String title,
                        EntityService<String, T> entityService,
                        Component<ListView.ListViewState<String, T>> listComponent,
                        Component<DetailsViewState<T>> editComponent,
                        Component<DetailsViewState<T>> createComponent) {
        this.name = name;
        this.title = title;
        this.entityService = entityService;
        this.listComponent = listComponent;
        this.editComponent = Optional.of(editComponent);
        this.createComponent = Optional.of(createComponent);
    }

    public CompletableFuture<ResourceView.State<T>> initialListState() {
        return entityService.getList(0, DEFAULT_PAGE_SIZE)
                .thenApply(entities -> new ListView.ListViewState<>(entities, new HashSet<>()))
                .thenApply(gridState -> new ResourceView.State<>(name, title, gridState, Optional.empty()));
    }

    public CompletableFuture<ResourceView.State<T>> initialListStateWithEdit(String key) {
            return entityService.getList(0, DEFAULT_PAGE_SIZE)
                .thenApply(entities -> new ListView.ListViewState<>(entities, new HashSet<>()))
                .thenCombine(entityService.getOne(key).thenApply(keo -> new DetailsViewState<>(keo.map(ke -> ke.data),
                                                                                             keo.map(ke -> ke.key))),
                        (gridState, edit) ->  new ResourceView.State<>(name, title, gridState, Optional.of(edit)));
    }



    @Override
    public DocumentPartDefinition render(UseState<ResourceView.State<T>> us) {
        return div(div(when(createComponent.isPresent(), button(attr("type", "button"),
                                                                text("Create"),
                                                                on("click", ctx -> us.accept(us.get().withCreate())))),
                    button(attr("type", "button"),
                            when(us.get().list.selectedRows.size() == 0, () -> attr("disabled")),
                            text("Delete"),
                            on("click", ctx -> {
                                    final Set<KeyedEntity<String, T>> rows = us.get().list.selectedRows;
                                    StreamUtils.sequence(rows.stream().map(r -> entityService.delete(r.key))
                                               .collect(Collectors.toList()))
                                               .thenAccept(l -> entityService.getList(0, DEFAULT_PAGE_SIZE)
                                                                             .thenAccept(entities -> us.accept(us.get().withList(new ListView.ListViewState<>(entities,
                                                                                                                                                      new HashSet<>())))));
                                }))),
                    listComponent.render(us.get().list,
                                         gridState -> gridState.editRowKey.ifPresentOrElse(
                                                 editKey -> entityService.getOne(editKey).thenAccept(keo ->
                                                         us.accept(us.get().withEditData(keo.get()))).join(),
                                                                                     () -> us.accept(us.get().withList(gridState)))),

                when(us.get().details.isPresent() && !us.get().details.get().currentKey.isPresent(),
                        () -> of(createComponent.map(cc -> cc.render(detailsViewState(us))).stream())),

                when(us.get().details.isPresent() && us.get().details.get().currentKey.isPresent(),
                        () -> of(editComponent.map(ec -> ec.render(detailsViewState(us))).stream())));
    }

    private UseState<DetailsViewState<T>> detailsViewState(UseState<State<T>> us) {
        return readWrite(() -> us.get().details.get(),
                         editState -> {
            if (!editState.validationErrors.isEmpty()) {
                // show the validation errors
                us.accept(us.get().withEdit(editState));
            } else if (editState.currentValue.isPresent() && editState.currentKey.isPresent()) {
                // edit
                entityService.update(new KeyedEntity<>(editState.currentKey.get(), editState.currentValue.get()))
                        .thenCompose(u -> entityService.getList(0, DEFAULT_PAGE_SIZE))
                        .thenAccept(entities ->
                                us.accept(us.get().withList(new ListView.ListViewState<>(entities, new HashSet<>())))).join();

            } else if (editState.currentValue.isPresent()) {
                // create
                entityService.create(editState.currentValue.get())
                        .thenCompose(u -> entityService.getList(0, DEFAULT_PAGE_SIZE))
                        .thenAccept(entities ->
                                us.accept(us.get().withList(new ListView.ListViewState<>(entities,
                                        new HashSet<>())))).join();
            } else {
                us.accept(us.get().hideDetails());
            }
        });
    }

    public static class State<T> {
        public final String name;
        public final String title;
        public final ListView.ListViewState<String, T> list;
        public final Optional<DetailsViewState<T>> details; //TODO to Optional<DetailsViewState<T>> , verify DetailsViewState.isActive

        public State(String name,
                     String title,
                     ListView.ListViewState<String, T> list,
                     Optional<DetailsViewState<T>> details) {
            this.name = name;
            this.title = title;
            this.list = list;
            this.details = details;
        }

        public State<T> withList(ListView.ListViewState<String, T> gs) {
            return new State<T>(name, title, gs, Optional.empty());
        }

        public State<T> withEdit(DetailsViewState<T> edit) {
            return new State<>(name, title, list, Optional.of(edit));
        }

        public State<T> hideDetails() {
            return new State<>(name, title, list, Optional.empty());
        }

        public State<T> withEditData(KeyedEntity<String, T> data) {
            return new State<>(name, title, list, Optional.of(new DetailsViewState<>(Optional.of(data.data), Optional.of(data.key))));
        }

        public State<T> withCreate() {
            return new State<>(name, title, list, Optional.of(new DetailsViewState<>(Optional.empty(), Optional.empty())));
        }

    }

}
