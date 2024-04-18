package loganalyzer;

public class Log {
    public Log(String ipAddress, String timeStamp, String method, String protocol, String requestPath, int statusCode, int contentLength, String userAgent) {
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

    private final String ipAddress;
    private final String timeStamp;
    private final String method;
    private final String protocol;
    private final String requestPath;
    private final int statusCode;
    private final int contentLength;
    private final String userAgent;
}
