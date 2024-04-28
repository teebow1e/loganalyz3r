package dataanalyzer;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import static csvgenerator.CSVReader.read;

public class TableVisualizer {
    public static void CreateLogTable(Parent root) throws IOException {
        List<String> data = read("logs/parsed/log_test.csv");
        VBox mainVBox = (VBox) root.lookup("#mainVBox");
        TableView<String[]> tableView = (TableView<String[]>) mainVBox.lookup("#Table");

        tableView.getItems().clear();
        tableView.getColumns().clear();

        String[] headers = data.get(0).split(",");
        for (int i = 0; i < headers.length; i++) {
            final int columnIndex = i;
            TableColumn<String[], String> column = new TableColumn<>(headers[i]);
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()[columnIndex]));
            column.setResizable(false);
            column.getStyleClass().add("log-table-column-" + (i + 1));
            tableView.getColumns().add(column);
        }


        // Add a checkbox column
        TableColumn<String[], Boolean> checkboxColumn = new TableColumn<>("Checkbox");
        checkboxColumn.setCellValueFactory(param -> new SimpleBooleanProperty(true)); // Replace with your data model property
        checkboxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkboxColumn));
        checkboxColumn.getStyleClass().add("log-table-column-" + 9);
        tableView.getColumns().add(checkboxColumn);



        ObservableList<String[]> rows = FXCollections.observableArrayList();
        for (int i = 1; i < data.size(); i++) {
            String[] rowData = data.get(i).split(",");
            rows.add(rowData);
        }

        tableView.setItems(rows);
    }
}
