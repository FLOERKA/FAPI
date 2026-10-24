package ru.floerka.api.utils.plugin;

import org.bukkit.plugin.java.JavaPlugin;

public class PluginUtil {

    public static JavaPlugin getCurrentPlugin() {
        return JavaPlugin.getProvidingPlugin(PluginUtil.class);
    }
}
