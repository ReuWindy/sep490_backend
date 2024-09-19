package com.fpt.sep490.utils;

import java.util.Locale;

public final class ProjectConstants {
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final Locale VIETNAM_LOCALE = new Locale.Builder().setLanguage("vi").setRegion("VN").build();

    private ProjectConstants() {
        throw new UnsupportedOperationException();
    }
}