package com.blog.blogbackend.common;

public final class ErrorCode {
    public static final int PARAM_INVALID = 1001;
    public static final int UNAUTHORIZED = 1002;
    public static final int FORBIDDEN = 1003;
    public static final int RESOURCE_NOT_FOUND = 1004;
    public static final int TOO_MANY_REQUESTS = 1005;
    public static final int POST_NOT_FOUND = 2001;
    public static final int COMMENTS_CLOSED = 3001;
    public static final int GUESTBOOK_CLOSED = 3002;
    public static final int SERVER_ERROR = 5000;

    private ErrorCode() {}
}