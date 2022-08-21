package rsp.admin.crud.components.list;

import rsp.Component;
import rsp.admin.data.KeyedEntity;
import rsp.html.DocumentPartDefinition;
import rsp.state.UseState;

import java.util.*;

import static rsp.html.HtmlDsl.*;

public final class ListView<T> implements Component<ListView.ListViewState<String, T>> {

    private final List<Column<T>> columns;

    @SafeVarargs
    public ListView(Column<T>... columns) {
        this.columns = Arrays.asList(columns);
    }

    @Override
    public DocumentPartDefinition render(UseState<ListViewState<String, T>> state) {
        return div(
                table(
                        thead(tr(th(""), of(columns.stream().map(h -> th(h.title))))),
                        tbody(
                                of(state.get().rows.stream().map(row -> tr(
                                        td(input(attr("type", "checkbox"),
                                                 when(state.get().selectedRows.contains(row), () -> attr("checked")),
                                                 attr("autocomplete", "off"),
                                                 on("click", ctx -> state.accept(state.get().toggleRowSelection(row))))),
                                        of(columns.stream().map(column -> td(column.fieldComponent.apply(row.data)
                                                .render(row.key, k -> state.accept(state.get().withEditRowKey(Optional.of(row.key)))))))
                                )))))
                );
    }

    public static final class ListViewState<K, T> {
        public final List<KeyedEntity<K, T>> rows;
        public final Set<KeyedEntity<K, T>> selectedRows;
        public final Optional<String> editRowKey;

        public ListViewState(List<KeyedEntity<K, T>> rows, Set<KeyedEntity<K, T>> selectedRows, Optional<String> editRowKey) {
            this.rows = Objects.requireNonNull(rows);
            this.selectedRows = Objects.requireNonNull(selectedRows);
            this.editRowKey = editRowKey;
        }

        public ListViewState(List<KeyedEntity<K, T>> rows, Set<KeyedEntity<K, T>> selectedRows) {
            this.rows = Objects.requireNonNull(rows);
            this.selectedRows = Objects.requireNonNull(selectedRows);
            this.editRowKey = Optional.empty();
        }

        public static <K, T> ListViewState<K, T> empty() {
            return new ListViewState<>(List.of(), Set.of());
        }

        public ListViewState<K, T> toggleRowSelection(KeyedEntity<K, T> row) {
            final Set<KeyedEntity<K, T>> sr = new HashSet<>(selectedRows);
            if (selectedRows.contains(row)) {
                sr.remove(row);
            } else {
                sr.add(row);
            }
            return new ListViewState<>(rows, sr);
        }

        public ListViewState<K, T> withEditRowKey(Optional<String> rowKey) {
            return new ListViewState<>(this.rows, this.selectedRows, rowKey);
        }
    }
}
