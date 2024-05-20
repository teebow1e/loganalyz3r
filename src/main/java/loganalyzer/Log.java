package loganalyzer;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

public class Log {
    /**
     * This is a sample Log object
     * @param remoteAddress: IP address of remote client
     * @param timestamp: the time when the request reached webserver
     * @param method: HTTP method
     * @param protocol: HTTP version
     * @param requestPath: the path of the request
     * @param statusCode: the returned Status code
     * @param userAgent: the User-Agent send in the request
     */
    public Log(String remoteAddress, String timestamp, String method, String protocol,
               String requestPath, int statusCode, String userAgent) {
        this.remoteAddress = remoteAddress;
        this.timestamp = timestamp;
        this.method = method;
        this.protocol = protocol;
        this.requestPath = requestPath;
        this.statusCode = statusCode;
        this.userAgent = userAgent;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public String getTimestamp() {
        return timestamp;
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


    public String getUserAgent() {
        return userAgent;
    }

    @CsvBindByName(column = "remoteAddress", required = true)
    @CsvBindByPosition(position = 0)
    private final String remoteAddress;
    @CsvBindByName(column = "timestamp", required = true)
    @CsvBindByPosition(position = 1)
    private final String timestamp;
    @CsvBindByName(column = "method", required = true)
    @CsvBindByPosition(position = 2)
    private final String method;
    @CsvBindByName(column = "protocol", required = true)
    @CsvBindByPosition(position = 3)
    private final String protocol;
    @CsvBindByName(column = "requestPath", required = true)
    @CsvBindByPosition(position = 4)
    private final String requestPath;
    @CsvBindByName(column = "statusCode", required = true)
    @CsvBindByPosition(position = 5)
    private final int statusCode;
    @CsvBindByName(column = "userAgent", required = true)
    @CsvBindByPosition(position = 6)
    private final String userAgent;
}
