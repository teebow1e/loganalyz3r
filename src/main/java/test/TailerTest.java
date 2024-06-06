package test;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.io.File;

public class TailerTest {

    // Define a flag to indicate whether the tailer should keep running
    private static volatile boolean running = true;

    public static void main(String[] args) {
        File file = new File("logs/apache_nginx/access_log_100.log");
        System.out.println(file.length());

        TailerListenerAdapter listener = new TailerListenerAdapter() {
            @Override
            public void handle(String line) {
                // Check if the tailer should keep running
                if (!running) {
                    // If not, interrupt the thread
                    System.out.println("the thread is instructed to stop");
                    Thread.currentThread().interrupt();
                    return;
                }
                System.out.println("handler called");
                System.out.printf("new line added: %s\n", line);
            }
        };

        Tailer tailer = new Tailer(file, listener, 200, true);
        Thread thread = new Thread(tailer);
        System.out.println("tailer ready to run");
        thread.start();

        // Wait for some time and then stop the tailer
        try {
            Thread.sleep(10000);
            System.out.println("finished watiing");
            // Wait for 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("terminate thread now");
        thread.interrupt();

        // Set the running flag to false to stop the tailer
//        running = false;
        tailer.stop();
    }
}

