package csvgenerator;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import loganalyzer.Apache;
import loganalyzer.ModSecurity;

import java.io.*;
import java.util.List;

public class CsvGenerator {
    private CsvGenerator() {
        throw new IllegalStateException("Utility class");
    }
    public static void generateCSVNormalLog(List<Apache> logList)
            throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        // CONSTANT VALUE HERE
        File file = new File("logs/parsed/log_new.csv");
        try (FileWriter writer = new FileWriter(file)){
            var mappingStrategy = new CustomColumnPositionStrategy<Apache>();
            mappingStrategy.setType(Apache.class);

            var builder = new StatefulBeanToCsvBuilder<Apache>(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withMappingStrategy(mappingStrategy)
                    .build();
            builder.write(logList);
        }
    }

    public static void generateCSVModSecurity(List<ModSecurity> logList)
            throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        // Check if the log list is empty
        if (logList.isEmpty()) {
            System.out.println("The log list is empty. No data to write.");
            return;
        }

        // CONSTANT VALUE HERE
        File file = new File("logs/parsed/modsecurity.csv");

        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileWriter writer = new FileWriter(file)) {
            var mappingStrategy = new CustomColumnPositionStrategy<ModSecurity>();
            mappingStrategy.setType(ModSecurity.class);

            var builder = new StatefulBeanToCsvBuilder<ModSecurity>(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withMappingStrategy(mappingStrategy)
                    .build();

            try {
                builder.write(logList);
                System.out.println("CSV file written successfully.");
            } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
                System.err.println("Error writing CSV data: " + e.getMessage());
                throw e;
            }
        }
    }
}
