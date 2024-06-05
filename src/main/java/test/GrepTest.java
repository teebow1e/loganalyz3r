package test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class GrepTest {
    public static void grep(String filePath, String pattern) {
        Pattern regex = Pattern.compile(pattern);

        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.filter(regex.asPredicate())
                    .forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String filepath = System.getProperty("user.dir") + "\\logs\\apache_nginx\\access_log_special.log";
        String pattern = "142\\.4\\.7\\.20"; // pattern to match a specific ip
        grep(filepath, pattern);
    }
}
