package ru.floerka.api.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})");

    public static String color(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder builder = new StringBuilder();

        while (matcher.find()) {
            String hex = matcher.group(1);
            matcher.appendReplacement(builder, ChatColor.of("#" + hex).toString());
        }
        matcher.appendTail(builder);

        return ChatColor.translateAlternateColorCodes('&', builder.toString());
    }
    public static String compilation(Player player, String message) {
        String colored = color(message);
        if(PaPiUtil.isEnabled()) {
            colored = PlaceholderAPI.setPlaceholders(player, colored);
        }
        return colored;
    }

    public static List<String> color(List<String> reference) {
        return reference.stream().map(ColorUtils::color).toList();
    }
}
