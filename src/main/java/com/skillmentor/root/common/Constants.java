package com.skillmentor.root.common;

public class Constants {
    public static final String APPLICATION_JSON = "application/json";

    public static final String ADMIN_ROLE_PERMISSION = "hasRole('ADMIN')";
    //public static final String STUDENT_ROLE_PERMISSION = "hasRole('STUDENT')";
    //public static final String MENTOR_ROLE_PERMISSION = "hasRole('MENTOR')";
    public static final String ADMIN_OR_STUDENT_ROLE_PERMISSION = "hasAnyRole('ADMIN', 'STUDENT')";
    //public static final String ADMIN_OR_MENTOR_ROLE_PERMISSION = "hasAnyRole('ADMIN', 'MENTOR')";
    //public static final String ADMIN_OR_STUDENT_OR_MENTOR_ROLE_PERMISSION = "hasAnyRole('ADMIN', 'STUDENT', 'MENTOR')";

    public enum SessionStatus {
        PENDING,
        ACCEPTED,
        COMPLETED
    }
}
