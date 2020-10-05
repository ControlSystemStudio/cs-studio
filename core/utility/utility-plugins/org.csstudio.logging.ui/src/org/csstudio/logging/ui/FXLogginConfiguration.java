package org.csstudio.logging.ui;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.ComboBoxTreeTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.application.Platform;

import org.eclipse.fx.ui.workbench3.FXViewPart;

import com.google.common.base.Joiner;


/**
 * This view lists all the loggers currently available in cs-studio.
 *
 * The users can add or remove the consoleViewHandler from any of the logger
 * (since the Handler is only attached to the root Logger the current
 * mechanism simply enables/disables the use of parentHandlers)
 *
 * The users can configure the logging Level for these loggers too, thus allow
 * temporary FINE logging.
 *
 * @author Kunal Shroff
 *
 */
public class FXLogginConfiguration extends FXViewPart {

    private static final String ID = "org.csstudio.logging.ui.FXLoggingConfiguration";

    private static Logger logger = Logger.getLogger(FXLogginConfiguration.class.getName());

    private static LogManager manager = LogManager.getLogManager();
    final static TreeItem<NameNode> root = new TreeItem<>(new NameNode(null, null, null, true));


    static {
        Platform.setImplicitExit(false);
    }
    @SuppressWarnings("unchecked")
    @Override
    protected Scene createFxScene() {

    root.setExpanded(true);

    AnchorPane anchorpane = new AnchorPane();

    final Scene scene = new Scene(anchorpane);

    TreeTableColumn<NameNode, String> loggerNameColumn = new TreeTableColumn<>(
        "Logger Name");
    loggerNameColumn.setPrefWidth(350);
    loggerNameColumn
        .setCellValueFactory((
            TreeTableColumn.CellDataFeatures<NameNode, String> param) -> new ReadOnlyStringWrapper(
            param.getValue().getValue().getFullName()));

    ObservableList<Level> levels = FXCollections.observableArrayList(
        Level.OFF, Level.SEVERE, Level.WARNING, Level.INFO,
        Level.CONFIG, Level.FINE, Level.FINER, Level.FINEST, Level.ALL);

    TreeTableColumn<NameNode, Level> dropBoxColumn = new TreeTableColumn<>(
        "Logging Level");
    dropBoxColumn.setPrefWidth(100);
    dropBoxColumn.setCellFactory(ComboBoxTreeTableCell
        .forTreeTableColumn(levels));
    dropBoxColumn
        .setCellValueFactory(new Callback<CellDataFeatures<NameNode, Level>, ObservableValue<Level>>() {

            @Override
            public ObservableValue<Level> call(
                CellDataFeatures<NameNode, Level> param) {
            Logger logger = param.getValue().getValue().getLogger();
            if (logger != null) {
                Level level = logger.getLevel();

                return new ObservableValue<Level>() {

                @Override
                public void removeListener(
                    InvalidationListener listener) {

                }

                @Override
                public void addListener(
                    InvalidationListener listener) {

                }

                @Override
                public void removeListener(
                    ChangeListener<? super Level> listener) {

                }

                @Override
                public Level getValue() {
                    if (level != null) {
                    return levels.get(levels.indexOf(level));
                    }
                    return Level.INFO;
                }

                @Override
                public void addListener(
                    ChangeListener<? super Level> listener) {

                }

                };
            }
            return null;
            }

        });
    dropBoxColumn.setEditable(true);
    dropBoxColumn.setOnEditCommit((CellEditEvent<NameNode, Level> value) -> {
            Logger logger = manager.getLogger(value.getRowValue()
                .getValue().getFullName());
            if (logger != null) {
            logger.setLevel(value.getNewValue());
            }
        });

    TreeTableColumn<NameNode, Boolean> useParentHandlersColumn = new TreeTableColumn<NameNode, Boolean>("use ConsoleViewer");
    useParentHandlersColumn.setPrefWidth(100);
    useParentHandlersColumn.setCellFactory(CheckBoxTreeTableCell
        .forTreeTableColumn(useParentHandlersColumn));
    useParentHandlersColumn
        .setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<NameNode, Boolean>, ObservableValue<Boolean>>() {

            @Override
            public ObservableValue<Boolean> call(CellDataFeatures<NameNode, Boolean> param) {
            SimpleBooleanProperty s = new SimpleBooleanProperty(param.getValue().getValue().getEnableParentHandler());
            s.addListener(new ChangeListener<Boolean>() {

                @Override
                public void changed(
                    ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                param.getValue().getValue().setEnableParentHandler(newValue);
                Logger logger = manager.getLogger(param.getValue().getValue().getFullName());
                if(logger != null)
                    logger.setUseParentHandlers(newValue);
                }
            });
            return s;
            }
        });

    TreeTableView<NameNode> treeTableView = new TreeTableView<>(root);
    treeTableView.showRootProperty().setValue(Boolean.FALSE);
    treeTableView.getColumns().setAll(loggerNameColumn, dropBoxColumn,
        useParentHandlersColumn);
    treeTableView.setEditable(true);
    treeTableView.getSelectionModel().setSelectionMode(
        SelectionMode.MULTIPLE);
    treeTableView.getSelectionModel().setCellSelectionEnabled(true);

    AnchorPane.setTopAnchor(treeTableView, 10.0);
    AnchorPane.setBottomAnchor(treeTableView, 10.0);
    AnchorPane.setLeftAnchor(treeTableView, 10.0);
    AnchorPane.setRightAnchor(treeTableView, 10.0);

    anchorpane.getChildren().add(treeTableView);

    updateLoggerMap();
    return scene;
    }

    @Override
    protected void setFxFocus() {

    }

    static class NameNode {
    private final String name;
    private final String fullName;
    private final Logger logger;

    private boolean enableParentHandler;

    public NameNode(String name, String fullName, Logger logger,
        boolean enableParentHandler) {
        this.name = name;
        this.fullName = fullName;
        this.logger = logger;
        this.enableParentHandler = enableParentHandler;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {
        return getFullName();
    }

    public Logger getLogger() {
        return logger;
    }

    public void setEnableParentHandler(boolean enableParentHandler) {
        this.enableParentHandler = enableParentHandler;
    }

    public Boolean getEnableParentHandler() {
        return enableParentHandler;
    }

    }

    /**
     * creates the TreeItems using the currently registered loggers
     */
    public static void updateLoggerMap() {

    Enumeration<String> loggerNames = manager.getLoggerNames();
    while (loggerNames.hasMoreElements()) {
        String completeName = loggerNames.nextElement().trim();

        TreeItem<NameNode> parent = root;
        Joiner joiner = Joiner.on(".").skipNulls();

        List<String> nameComponents = Arrays.asList(completeName
            .split("\\."));
        for (String name : nameComponents) {
        if (!name.isEmpty()) {
            boolean nodeExists = false;
            for (TreeItem<NameNode> child : parent.getChildren()) {
            if (name.equals(child.getValue().getName())) {
                parent = child;
                nodeExists = true;
            }
            }
            if (!nodeExists) {
            String fullName = joiner.join(parent.getValue().getFullName(), name);
            logger.config(() -> {
                return "adding: " + name + " " + joiner.join(fullName, name);
            });
            TreeItem<NameNode> newNode = new TreeItem<NameNode>(
                new NameNode(name, fullName, manager
                    .getLogger(fullName), manager
                    .getLogger(fullName) != null ? true
                    : false));
            newNode.setExpanded(true);
            parent.getChildren().add(newNode);
            parent = newNode;
            }
        }
        }
    }

    }

}
