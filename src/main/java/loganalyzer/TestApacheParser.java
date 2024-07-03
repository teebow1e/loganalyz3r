package loganalyzer;

import static loganalyzer.ApacheParser.parseLogLine;

public class TestApacheParser {
    public static void main(String[] args) {
        Apache apacheParsed = parseLogLine("127.0.0.1 - - [03/Jul/2024:18:21:46 +0700] \"GET /dashboard/ HTTP/1.1\" 304 - \"-\" \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36 Edg/126.0.0.0\"");
        System.out.println(apacheParsed);
    }
}
