package dataanalyzer;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import static csvgenerator.CSVReader.read;

public class TableVisualizer {
    public static void CreateTable(Parent root) throws IOException {
        List<String> data = read("logs/parsed/log.csv");
        VBox mainVBox = (VBox) root.lookup("#mainVBox");
        TableView<String[]> tableView = (TableView<String[]>) mainVBox.lookup("#LogTable");
        tableView.getItems().clear();
        tableView.getColumns().clear();

        if (!data.isEmpty()) {
            // Extract headers and add columns
            String[] headers = data.get(0).split(",");
            for (int i = 0; i < headers.length; i++) {
                final int index = i;
                TableColumn<String[], String> column = new TableColumn<>(headers[i]);
                column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()[index]));
                tableView.getColumns().add(column);
            }

            // Add data rows
            ObservableList<String[]> rows = FXCollections.observableArrayList();
            for (int i = 1; i < data.size(); i++) {
                String[] rowData = data.get(i).split(",");
                rows.add(rowData);
            }
            tableView.setItems(rows);
        }
    }
}
