package rsp.admin.samples.jsonplaceholder;

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
import rsp.admin.samples.authorsbooks.SimpleDb;
import rsp.jetty.JettyServer;
import rsp.server.StaticResources;

import java.io.File;


public class JsonPlaceholderAdmin {
    public static final String JSON_PLACEHOLDER_BASE_URL="http://localhost:3000/";
    public static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        final SimpleDb db = new SimpleDb();
        final EntityService<String, User> userService = new UsersService();

        final Admin admin = new Admin("JsonPlaceholder",
                                        new ResourceView<>("users",
                                                        "Users",
                                                        userService,
                                                        new ListView<>(new Column<>("Name", e -> new TextField<>(e.name)),
                                                                            new Column<>(e -> new EditButton())),
                                                        new EditView<>(d -> new Form(m -> d.accept(new User(m.get("name"))),
                                                                                    new TextInput("name",
                                                                                                   TextInput.Type.TEXT,
                                                                                                   "Name",
                                                                                                   d.get().toString(),
                                                                                                   new RequiredValidation()))),
                                                        new CreateView<>(d -> new Form((m -> d.accept(new User(m.get("name")))),
                                                                                        new TextInput("name",
                                                                                                      TextInput.Type.TEXT,
                                                                                                      "Name",
                                                                                                      "",
                                                                                                      new RequiredValidation())))));

        final var s = new JettyServer<>(DEFAULT_PORT,
                                       "/",
                                        admin.app(),
                                        new StaticResources(new File("src/main/java/rsp/admin"),
                                                            "/res/*"));
        s.start();
        s.join();
    }

}
