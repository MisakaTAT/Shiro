package com.mikuac.shiro.constant;

public class Connection {

    public static final String API_RESULT_KEY = "echo";
    public static final String FAILED_STATUS = "failed";
    public static final String RESULT_STATUS_KEY = "status";
    public static final String RESULT_CODE = "retcode";
    public static final String RESULT_WORDING = "wording";
    public static final String FUTURE_KEY = "future";
    public static final String SESSION_STATUS_KEY = "session_status";
    public static final String ADAPTER_KEY = "adapter";
    public static final String X_SELF_ID = "x-self-id";
    public static final String SELF_ID = "self_id";

    // 心跳相关
    public static final String LAST_HEARTBEAT_AT_MS_KEY = "last_heartbeat_at_ms";
    public static final String HEARTBEAT_INTERVAL_MS_KEY = "heartbeat_interval_ms";
    public static final String LAST_HEARTBEAT_ONLINE_KEY = "last_heartbeat_online";
    public static final long ONE_BOT_DEFAULT_HEARTBEAT_INTERVAL_MS = 15000L;

    private Connection() {
    }

}
