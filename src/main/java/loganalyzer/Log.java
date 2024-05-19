package loganalyzer;

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

    private final String remoteAddress;
    private final String timestamp;
    private final String method;
    private final String protocol;
    private final String requestPath;
    private final int statusCode;
    private final String userAgent;
}
