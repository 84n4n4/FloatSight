package com.watchthybridle.floatsight.configparser;

import java.util.Locale;

public class ConfigSetting {

    private static final String TEMPLATE_VALUE_LINE = "%s: %d ; %s\n";
    private static final String TEMPLATE_COMMENT_LINE = "                ;   %s\n";

    public String name;
    public int value;
    public String description;

    public String[] comments;

    public ConfigSetting(String name, int value, String description, String... comments) {
        this.name = name;
        this.value = value;
        this.description = description;
        this.comments = comments;
    }

    public String getString() {
        StringBuilder builder = new StringBuilder();

        builder.append(String.format(Locale.getDefault(), TEMPLATE_VALUE_LINE, name, value, description));

        for (String comment : comments) {
            builder.append(String.format(Locale.getDefault(), TEMPLATE_COMMENT_LINE, comment));
        }

        return builder.toString();
    }
}
