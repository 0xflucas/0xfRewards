package io.qzz._0xflucas.utils;

public class TimeFormat {

    public static String formatTimeRemaining(long seconds) {
        long months = seconds / (60 * 60 * 24 * 30);
        seconds %= (60 * 60 * 24 * 30);

        long weeks = seconds / (60 * 60 * 24 * 7);
        seconds %= (60 * 60 * 24 * 7);

        long days = seconds / (60 * 60 * 24);
        seconds %= (60 * 60 * 24);

        long hours = seconds / (60 * 60);
        seconds %= (60 * 60);

        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;

        StringBuilder sb = new StringBuilder();
        if (months > 0) sb.append(months).append("mo ");
        if (weeks > 0) sb.append(weeks).append("w ");
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (remainingSeconds > 0 || sb.length() == 0) sb
            .append(remainingSeconds)
            .append("s");

        return sb.toString().trim();
    }
}
