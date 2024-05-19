package loganalyzer;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvBindByName;

// todo: this should be a basic class, then we have nginx_apache log class / modsecurity log class
// modsecurity has some interesting fields that worth logging
public class Apache {
    public Apache(String ipAddress, String timeStamp, String method, String protocol, String requestPath, int statusCode, int contentLength, String userAgent) {
        this.ipAddress = ipAddress;
        this.timeStamp = timeStamp;
        this.method = method;
        this.protocol = protocol;
        this.requestPath = requestPath;
        this.statusCode = statusCode;
        this.contentLength = contentLength;
        this.userAgent = userAgent;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getTimestamp() {
        return timeStamp;
    }

    public String getMethod() {
        return method;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getUserAgent() {
        return userAgent;
    }
    @CsvBindByName(column = "ipAddr", required = true)
    @CsvBindByPosition(position = 0)
    private final String ipAddress;
    @CsvBindByName(column = "timestamp", required = true)
    @CsvBindByPosition(position = 1)
    private final String timeStamp;
    @CsvBindByName(column = "method", required = true)
    @CsvBindByPosition(position = 2)
    private final String method;
    @CsvBindByName(column = "protocol", required = true)
    @CsvBindByPosition(position = 3)
    private final String protocol;
    @CsvBindByName(column = "path", required = true)
    @CsvBindByPosition(position = 4)
    private final String requestPath;
    @CsvBindByName(column = "statusCode", required = true)
    @CsvBindByPosition(position = 5)
    private final int statusCode;
    @CsvBindByName(column = "contentLength", required = true)
    @CsvBindByPosition(position = 6)
    private final int contentLength;
    @CsvBindByName(column = "userAgent", required = true)
    @CsvBindByPosition(position = 7)
    private final String userAgent;
}
