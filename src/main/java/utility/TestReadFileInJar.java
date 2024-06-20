package utility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TestReadFileInJar {
    public static void ReadSpecialFile(String[] args) {
        ClassLoader classLoader = Utility.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("samplelog/access_log_100.log");
             InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
