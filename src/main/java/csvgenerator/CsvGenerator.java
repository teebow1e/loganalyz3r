package csvgenerator;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import loganalyzer.Log;

import java.io.*;
import java.util.List;

public class CsvGenerator {
    private CsvGenerator() {
        throw new IllegalStateException("Utility class");
    }
    public static void generateCSV(List<Log> logList)
            throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        File file = new File("logs/parsed/log_new.csv");
        try (FileWriter writer = new FileWriter(file)){
            var mappingStrategy = new CustomColumnPositionStrategy<Log>();
            mappingStrategy.setType(Log.class);

            var builder = new StatefulBeanToCsvBuilder<Log>(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withMappingStrategy(mappingStrategy)
                    .build();
            builder.write(logList);
        }
    }
}
