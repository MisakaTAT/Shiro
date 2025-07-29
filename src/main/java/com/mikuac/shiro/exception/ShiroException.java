package com.mikuac.shiro.exception;

/**
 * @author zero
 */
public class ShiroException extends RuntimeException {

    public ShiroException() {
    }

    public ShiroException(String s) {
        super(s);
    }

    public ShiroException(Throwable cause) {
        super(cause);
    }

    public ShiroException(String s, Throwable cause) {
        super(s, cause);
    }

    /**
     * session 断联状态, 但是会尝试恢复.
     */
    public static class SendMessageException extends ShiroException {
        public SendMessageException() {
            super("session been closed, but you can attempt again later.");
        }
    }

    /**
     * session 断联, 且未恢复.
     */
    public static class SessionCloseException extends ShiroException {
        public SessionCloseException() {
            super("session been closed.");
        }
    }
}
