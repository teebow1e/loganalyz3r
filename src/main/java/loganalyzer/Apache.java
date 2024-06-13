package loganalyzer;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvBindByName;

public class Apache extends Log{
    /**
     * Standard Apache Log object
     * This object is mostly inherited from the normal Log object
     * @param contentLength: the length of request
     */
    public Apache(String ipAddress, String timeStamp, String method, String protocol, String requestPath,
                  int statusCode, int contentLength, String userAgent) {
        super(ipAddress, timeStamp, method, protocol, requestPath, statusCode, userAgent);
        this.contentLength = contentLength;
    }

    public int getContentLength() {
        return contentLength;
    }

    @CsvBindByName(column = "contentLength", required = true)
    @CsvBindByPosition(position = 7)
    private final int contentLength;
}