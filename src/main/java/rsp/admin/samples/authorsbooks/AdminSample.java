package rsp.admin.samples.authorsbooks;

import rsp.admin.Admin;
import rsp.admin.components.details.CreateView;
import rsp.admin.components.details.EditView;
import rsp.admin.components.details.Form;
import rsp.admin.components.list.Column;
import rsp.admin.components.list.EditButton;
import rsp.admin.components.list.ListView;
import rsp.admin.components.list.TextField;
import rsp.admin.components.main.ResourceView;
import rsp.admin.components.text.RequiredValidation;
import rsp.admin.components.text.TextInput;
import rsp.admin.data.provider.EntityService;
import rsp.jetty.JettyServer;
import rsp.server.StaticResources;

import java.io.File;


public class AdminSample {

    public static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        final SimpleDb db = new SimpleDb();
        final EntityService<String, Author> authorsService = db.authorsService();
        final EntityService<String, Book> booksService = db.booksService();

        AuthorsBooksServiceStubInit.init(authorsService, booksService);

        final Admin admin = new Admin("Authors and Books",
                                        new ResourceView<>("authors",
                                                        "Authors",
                                                        authorsService,
                                                        new ListView<>(new Column<>("Name", e -> new TextField<>(e.name)),
                                                                            new Column<>(e -> new EditButton())),
                                                        new EditView<>(d -> new Form(m -> m.apply("name").ifPresent(v -> d.accept(Author.of(v))),
                                                                                    new TextInput("name",
                                                                                                   TextInput.Type.TEXT,
                                                                                                   "Name",
                                                                                                   d.get().toString(),
                                                                                                   new RequiredValidation()))),
                                                        new CreateView<>(d -> new Form(m -> m.apply("name").ifPresent(v -> d.accept(Author.of(v))),
                                                                                        new TextInput("name",
                                                                                                      TextInput.Type.TEXT,
                                                                                                      "Name",
                                                                                                      "",
                                                                                                      new RequiredValidation())))));

                /*
                                      new Resource<Book>("books",
                                                     booksService,
                                                     new Grid<>(new TextField("title"),
                                                                new EditButton()),
                                                     new EditForm<>(new TextInput<>("title", s -> s)),
                                                     new EditForm<String, Book>(new InitialValue<>(new TextInput<>("title", s -> s), "")))));
*/
        final var s = new JettyServer<>(DEFAULT_PORT,
                                       "/",
                                        admin.app(),
                                        new StaticResources(new File("src/main/java/rsp/admin"),
                                                            "/res/*"));
        s.start();
        s.join();
    }

}
