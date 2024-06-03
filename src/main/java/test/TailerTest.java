package test;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.io.File;

public class TailerTest {
    public static void main(String[] args) {
        File file = new File("logs/apache_nginx/access_log_0.log");
        System.out.println(file.length());

        TailerListenerAdapter listener = new TailerListenerAdapter() {
            @Override
            public void handle(String line) {
                System.out.println(line);
            }
        };

        Tailer tailer = new Tailer(file, listener, 1000, true);
        Thread thread = new Thread(tailer);
        System.out.println("tailer ready to run");
        thread.start();
    }
}
