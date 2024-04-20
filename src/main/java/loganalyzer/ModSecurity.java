package loganalyzer;

public class ModSecurity {
    public ModSecurity (String timestamp, String ipAddr, String attackName, String attackMsg,
             String attackData, String severity, String path, String unique_id, String eventType) {
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.ipAddr = ipAddr;
        this.attackName = attackName;
        this.attackMsg = attackMsg;
        this.attackData = attackData;
        this.severity = severity;
        this.path = path;
        this.unique_id = unique_id;
    }

    public String getEventType() {
        return eventType;
    }
    public String getTimestamp() {
        return timestamp;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public String getAttackName() {
        return attackName;
    }

    public String getAttackMsg() {
        return attackMsg;
    }

    public String getAttackData() {
        return attackData;
    }

    public String getSeverity() {
        return severity;
    }

    public String getPath() {
        return path;
    }

    public String getUnique_id() {
        return unique_id;
    }

    private final String eventType;
    private final String timestamp;
    private final String ipAddr;
    private final String attackName;
    private final String attackMsg;
    private final String attackData;
    private final String severity;
    private final String path;
    private final String unique_id;
}
