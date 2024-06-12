package dataanalyzer;

import controller.TableCheckBox;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.*;

import static csvgenerator.CSVReader.read;

public class TableVisualizer {
    public static void LogTable(TableView<String[]> tableView, String columnStyle) throws IOException {
        // CONSTANT VALUE HERE
        List<String> data = read("logs/parsed/log_new.csv");
        tableView.getItems().clear();
        tableView.getColumns().clear();

        String[] headers = data.get(0).split(",");
        List<Integer> nonEmptyColumnIndices = new ArrayList<>();
        for (int i = 0; i < headers.length; i++) {
            if (!headers[i].trim().isEmpty()) {
                nonEmptyColumnIndices.add(i);
            }
        }

        for (int i = 0; i < (nonEmptyColumnIndices.size()); i++) {
            if (!headers[i].trim().isEmpty()) {
                final int columnIndex = i;
                TableColumn<String[], String> column = new TableColumn<>(headers[i]);
                column.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue()[columnIndex]));
                column.setResizable(true);
                column.getStyleClass().add(columnStyle + (nonEmptyColumnIndices.indexOf(i) + 1));
                tableView.getColumns().add(column);
            }
        }

        ObservableList<String[]> rows = FXCollections.observableArrayList();
        for (int i = 1; i < data.size(); i++) {
            String[] rowData = data.get(i).split(",");
            String[] filteredRowData = Arrays.stream(rowData)
                    .filter(s -> !s.trim().isEmpty())
                    .toArray(String[]::new);
            rows.add(filteredRowData);
        }

        tableView.setItems(rows);
        tableView.setEditable(true);

        tableView.setRowFactory(tv -> {
            TableRow<String[]> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    String[] rowData = row.getItem();
                    showRowContent(headers, rowData);
                    System.out.println("Double clicked row: " + Arrays.toString(rowData));
                }
            });
            return row;
        });

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public static void ShowLogTable(TableView<String[]> tableView) throws IOException {
        LogTable(tableView, "access-log-table-column-");
        updateTableView(tableView);
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {;
//            }
//        }, 0, 10000); // Update every 10 seconds

        // No need to access the stage or set an event handler here
    }

    public static void ManageLogTable(TableView<String[]> tableView) throws IOException {
        LogTable(tableView, "manage-access-log-table-column-");

        TableColumn<String[], Boolean> checkboxColumn = new TableColumn<>("Checkbox");
        checkboxColumn.setCellValueFactory(param -> {
            String[] rowData = param.getValue();
            TableCheckBox checkBox = new TableCheckBox(rowData); // Create TableCheckBox instance for each row
            BooleanProperty selectedProperty = checkBox.selectedProperty();
            selectedProperty.addListener((observable, oldValue, newValue) -> {
                System.out.println(String.join(",", rowData)); // Print the row data when checkbox is toggled
            });
            return selectedProperty;
        });
        checkboxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkboxColumn));
        checkboxColumn.getStyleClass().add("manage-access-log-table-column-" + 9);
        checkboxColumn.setEditable(true); // Make the checkbox column editable
        checkboxColumn.setResizable(true);
        tableView.getColumns().add(checkboxColumn);

        // Set column resize policy to ensure all columns cover the full width of the table
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // No need to access the stage or set an event handler here
    }

    private static void updateTableView(TableView<String[]> tableView) {
        try {
            LogTable(tableView, "access-log-table-column-");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void showRowContent(String[] headers, String[] rowData) {
        VBox contentBox = new VBox();
        contentBox.setSpacing(5);

        Map<String, String> headerDataMap = new LinkedHashMap<>();
        for (int i = 0; i < headers.length; i++) {
            if (!headers[i].trim().isEmpty()) {
                headerDataMap.put(headers[i], rowData[i]);
            }
        }

        for (Map.Entry<String, String> entry : headerDataMap.entrySet()) {
            Text text = new Text(entry.getKey() + ": " + entry.getValue());
            contentBox.getChildren().add(text);
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(contentBox);
        dialog.setTitle("Row Details");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
}