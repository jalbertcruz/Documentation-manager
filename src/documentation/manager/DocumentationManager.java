package documentation.manager;

import com.google.gson.Gson;
import config.Conf;
import config.SearchConfig;
import documentationchecker.TextTransfer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import pkgnew.books.checker.NewBooksChecker;

public class DocumentationManager extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    private TableView<DocEntry> table = new TableView();
    SearchConfig sconf;
    final ObservableList<DocEntry> data =
            FXCollections.observableArrayList();

    private void saveConfigReptds(Long ind) throws NumberFormatException {
        try (PrintWriter fw = new PrintWriter(new File("sconfig.json"))) {
            Gson g = new Gson();
            sconf.setFrom(ind);
            fw.write(g.toJson(sconf));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DocumentationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void mkTable() {
        TableColumn fname = TableColumnBuilder.create().
                text("File name").
                prefWidth(600).
                build();

        fname.setCellValueFactory(
                new PropertyValueFactory<DocEntry, String>("name"));

        TableColumn fpath = TableColumnBuilder.create().
                text("File path").
                prefWidth(450).
                build();

        fpath.setCellValueFactory(
                new PropertyValueFactory<DocEntry, String>("path"));

        TableColumn fdel = TableColumnBuilder.create().
                text("Delete").
                prefWidth(50).
                build();

        Callback<TableColumn, DelButtonCell> cellFactory =
                new Callback<TableColumn, DelButtonCell>() {
                    @Override
                    public DelButtonCell call(TableColumn p) {
                        return new DelButtonCell("Del");
                    }
                };

        fdel.setCellFactory(cellFactory);

        TableColumn fcopy = TableColumnBuilder.create().
                text("Copy path").
                prefWidth(74).
                build();

        Callback<TableColumn, CopyButtonCell> cellFactory2 =
                new Callback<TableColumn, CopyButtonCell>() {
                    @Override
                    public CopyButtonCell call(TableColumn p) {
                        return new CopyButtonCell("Copy");
                    }
                };
        fcopy.setCellFactory(cellFactory2);

        TableColumn fopen = TableColumnBuilder.create().
                text("Open file").
                prefWidth(70).
                build();

        Callback<TableColumn, OpenFileButtonCell> cellFactory3 =
                new Callback<TableColumn, OpenFileButtonCell>() {
                    @Override
                    public OpenFileButtonCell call(TableColumn p) {
                        return new OpenFileButtonCell("Open");
                    }
                };
        fopen.setCellFactory(cellFactory3);

        table.setItems(data);

        table.prefHeightProperty().set(1300);
//        table.getColumns().addAll(fname, fpath, fdel, fcopy, fopen);
        table.getColumns().add(fname);
        table.getColumns().add(fpath);
//        table.getColumns().add(fdel);
        table.getColumns().add(fcopy);
        table.getColumns().add(fopen);

    }

    @Override
    public void start(final Stage st) throws Exception {

        st.setTitle("Documentation manager");

        maximizingStage(st);

        Label lDesde = new Label("Desde");
        Label lHasta = new Label("hasta");
        Label lTexto = new Label("Texto: ");

        try (FileReader fr = new FileReader(new File("sconfig.json"))) {
            Gson g = new Gson();
            sconf = g.fromJson(fr, SearchConfig.class);
        }

        nbc = new NewBooksChecker();
        
//        nbc = new NewBooksChecker(sconf.getConnectString(), sconf.getUser(), sconf.getPassword());

        final long stepSize = sconf.getStepSize();

        final TextField findRepValueFrom = TextFieldBuilder.create().
                text("" + sconf.getFrom()).
                prefWidth(50).
                build();

        final TextField findRepValueTo = TextFieldBuilder.create().
                text("" + (sconf.getFrom() + stepSize - 1)).
                prefWidth(50).
                build();

        final CheckBox chBsaveConfigOnClose = CheckBoxBuilder.create().
                text("sv").
                selected(false).build();

        final CheckBox searchInPath = CheckBoxBuilder.create().
                selected(false).
                build();

        st.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent arg0) {
                if (chBsaveConfigOnClose.isSelected()) {
                    saveConfigReptds(Long.parseLong(findRepValueTo.getText()) + 1);
                }
            }
        });

        final TextField text2Find = TextFieldBuilder.create().
                prefWidth(200).
                build();

        mkTable();

        VBox vb = new VBox();
        Button buscar = mkBuscarFromClipboardBt(st, text2Find, searchInPath);

        Button indexar = mkIndexarBt(st, findRepValueFrom, findRepValueTo, stepSize);

        Button limpiar = mkLimpiarBt(st);

        Button bRepetidos = mkRepetidosBt(findRepValueFrom, findRepValueTo, st);

        Button bpRepetidos = mkProxRepetidosBt(stepSize, st, findRepValueFrom, findRepValueTo);

        Button bRepEsts = mkRedundanciasBt(st);

        Button buscarBt = mkBuscarBt(searchInPath, text2Find, st);

        Button about = mkAboutBt();

        Button bDelRep = mkDelRepBt();

        HBox hb = new HBox(15);

        hb.getChildren().addAll(
                searchInPath,
                buscar,
                lTexto,
                text2Find,
                buscarBt,
                lDesde,
                findRepValueFrom,
                lHasta,
                findRepValueTo,
                bRepetidos,
                chBsaveConfigOnClose,
                bpRepetidos,
                bRepEsts);

        if (sconf.isIndexar()) {
            hb.getChildren().add(indexar);
        }
        if (sconf.isVaciarDB()) {
            hb.getChildren().add(limpiar);
        }
        if (sconf.isEliminarUltimosRepetidos()) {
            hb.getChildren().add(bDelRep);
        }

        hb.getChildren().add(about);

        vb.getChildren().addAll(hb, table);

        st.setScene(new Scene(vb, 1300, 700));

        st.show();
    }
    NewBooksChecker nbc;

    private void maximizingStage(final Stage st) {
        Screen screen = Screen.getPrimary();
        javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();

        st.setX(bounds.getMinX());
        st.setY(bounds.getMinY());
        st.setWidth(bounds.getWidth());
        st.setHeight(bounds.getHeight());
    }

    private Button mkAboutBt() {
        Button about = new Button("About");
        about.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                About a = new About(null, true);
                a.setVisible(true);
            }
        });
        return about;
    }

    private Button mkDelRepBt() {
        Button bDelRep = new Button("Purgar");
        bDelRep.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                try {
                    nbc.purgRep();
                } catch (SQLException ex) {
                    Logger.getLogger(DocumentationManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        return bDelRep;
    }

    private Button mkBuscarBt(final CheckBox searchInPath, final TextField text2Find, final Stage st) {
        Button buscarBt = new Button("Buscar");
        buscarBt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                try {

                    data.clear();

                    ArrayList<DocEntry> ch;

                    if (searchInPath.isSelected()) {
                        ch = nbc.checkPathNameExistence(text2Find.getText());
                    } else {
                        ch = nbc.checkFileNameExistence(text2Find.getText());
                    }

                    st.setTitle(ch.size() + " casos encontrados");

                    for (DocEntry s : ch) {
                        data.add(s);
                    }

                } catch (SQLException | IOException ex) {
                    Logger.getLogger(DocumentationManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        return buscarBt;
    }

    private Button mkRedundanciasBt(final Stage st) {
        Button bRepEsts = new Button("Redundancias");
        bRepEsts.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {

                st.setTitle("Calculando redundancias...");

                try {

                    RedundantInfo ri = nbc.redundantInfo();

                    st.setTitle("Cantidad de ficheros repetidos: " + ri.frepetidos + ", " + ri.mbRedundantes + " MBs redundantes");

                } catch (SQLException ex) {
                    Logger.getLogger(DocumentationManager.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
        return bRepEsts;
    }

    private Button mkProxRepetidosBt(final long stepSize, final Stage st, final TextField findRepValueFrom, final TextField findRepValueTo) {
        Button bpRepetidos = new Button("Próx. " + stepSize + " repets");
        bpRepetidos.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {

                try {

                    st.setTitle("Buscando...");

                    data.clear();

                    findRepValueFrom.setText((Integer.parseInt(findRepValueFrom.getText()) + stepSize) + "");
                    findRepValueTo.setText((Integer.parseInt(findRepValueTo.getText()) + stepSize) + "");

                    ArrayList<DocEntry> ch = nbc.checkRepited(Integer.parseInt(findRepValueFrom.getText()), Integer.parseInt(findRepValueTo.getText()));

                    st.setTitle(ch.size() + " casos encontrados");

                    for (DocEntry s : ch) {
                        data.add(s);
                    }

                    table.scrollTo(0);

                } catch (SQLException ex) {
                    Logger.getLogger(DocumentationManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        return bpRepetidos;
    }

    private Button mkRepetidosBt(final TextField findRepValueFrom, final TextField findRepValueTo, final Stage st) {
        Button bRepetidos = new Button("Repts");
        bRepetidos.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {

                try {

                    data.clear();

                    ArrayList<DocEntry> ch = nbc.checkRepited(Integer.parseInt(findRepValueFrom.getText()), Integer.parseInt(findRepValueTo.getText()));

                    st.setTitle(ch.size() + " casos encontrados");

                    for (DocEntry s : ch) {
                        data.add(s);
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(DocumentationManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        return bRepetidos;
    }

    private Button mkLimpiarBt(final Stage st) {
        Button limpiar = new Button("Vaciar DB");
        limpiar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                try {

                    nbc.cleanDB();

                    st.setTitle("DB vaciada");

                } catch (SQLException ex) {
                    Logger.getLogger(DocumentationManager.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
        return limpiar;
    }

    private Button mkIndexarBt(final Stage st, final TextField findRepValueFrom, final TextField findRepValueTo, final long stepSize) {
        Button indexar = new Button("Indexar");
        indexar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {

                try {

                    st.setTitle("Indexando...");

                    Conf mData;

                    try (FileReader fr = new FileReader(new File("config.json"))) {

                        Gson g = new Gson();

                        mData = g.fromJson(fr, Conf.class);

                    }

                    for (String m : mData.getDirs2check()) {
                        nbc.walk(new File(m));
                    }

                    st.setTitle("Indexado completo.");
                    saveConfigReptds(1L);

                    findRepValueFrom.setText("1");
                    findRepValueTo.setText("" + stepSize);

                } catch (Exception ex) {
                    Logger.getLogger(DocumentationManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        return indexar;
    }

    private Button mkBuscarFromClipboardBt(final Stage st, final TextField text2Find, final CheckBox searchInPath) {
        Button buscar = new Button("Buscar desde ClipB");
        buscar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                try {

                    data.clear();

                    st.setTitle("Buscando...");

                    TextTransfer textTransfer = new TextTransfer();
                    final String txt = textTransfer.getClipboardContents();

                    text2Find.setText(txt);

                    ArrayList<DocEntry> ch;

                    if (searchInPath.isSelected()) {
                        ch = nbc.checkPathNameExistence(txt);
                    } else {
                        ch = nbc.checkFileNameExistence(txt);
                    }

                    st.setTitle(ch.size() + " casos encontrados");

                    for (DocEntry s : ch) {
                        data.add(s);
                    }

                } catch (SQLException | IOException ex) {
                    Logger.getLogger(DocumentationManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        return buscar;
    }
}
