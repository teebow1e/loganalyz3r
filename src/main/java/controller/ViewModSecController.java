package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static csvgenerator.CSVReader.read;

public class ViewModSecController {

    @FXML
    private TableView<String[]> Table;
    @FXML
    private DatePicker datePicker;

    @FXML
    private void initialize() {
        datePicker.setValue(LocalDate.now());
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                viewLog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        try {
            viewLog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void viewLog() throws Exception {
        ShowLogTable((TableView<String[]>) Table, datePicker.getValue());
    }

    public static void LogTable(TableView<String[]> tableView, LocalDate selectedDate) throws IOException {
        List<String> data = read("logs/parsed/modsecurity.csv");

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
                tableView.getColumns().add(column);
            }
        }

        ObservableList<String[]> rows = FXCollections.observableArrayList();

        for (int i = 1; i < data.size(); i++) {
            String[] rowData = data.get(i).split(",");
            String dateStr = rowData[1];
            LocalDate rowDate = parseDate(dateStr);

            if (rowDate.equals(selectedDate)) {
                String[] filteredRowData = Arrays.stream(rowData)
                        .filter(s -> !s.trim().isEmpty())
                        .toArray(String[]::new);
                rows.add(filteredRowData);
            }
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
    }

    public static void ShowLogTable(TableView<String[]> tableView, LocalDate selectedDate) throws IOException {
        LogTable(tableView, selectedDate);
        updateTableView(tableView, selectedDate);
    }

    private static void updateTableView(TableView<String[]> tableView, LocalDate selectedDate) {
        try {
            LogTable(tableView, selectedDate);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void showRowContent(String[] headers, String[] rowData) {
        VBox contentBox = new VBox();
        contentBox.setSpacing(5);

        Map<String, String> headerDataMap = Arrays.stream(headers)
                .filter(header -> !header.trim().isEmpty())
                .collect(Collectors.toMap(header -> header, header -> rowData[Arrays.asList(headers).indexOf(header)]));

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

    public static LocalDate parseDate(String inputDate) {
        DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss.SSSSSS Z");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(inputDate, INPUT_FORMATTER);
        return zonedDateTime.toLocalDate();
    }
}
