package ru.floerka.api.starter;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import ru.floerka.api.inventory.listeners.FClickListener;
import ru.floerka.api.managers.EquipmentManager;
import ru.floerka.api.managers.ParticleManager;
import ru.floerka.api.managers.SoundManager;
import ru.floerka.api.starter.models.Manager;

import java.util.HashMap;
import java.util.Map;

public class FAPI {

    private static final Map<Class<? extends Manager>, Manager> managerMap;
    private static boolean init = false;

    static {
        managerMap = new HashMap<>();
        managerMap.put(ParticleManager.class, new ParticleManager());
        managerMap.put(SoundManager.class, new SoundManager());
        managerMap.put(EquipmentManager.class, new EquipmentManager());
    }

    public static void init(JavaPlugin plugin) {
        enableInventoryListener(plugin);
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));
        PacketEvents.getAPI().load();
        init = true;
    }
    public static void disable() {
        PacketEvents.getAPI().terminate();
    }

    public static void enableInventoryListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(new FClickListener(), plugin);
    }

    public static @Nullable <T extends Manager> T getManager(Class<T> clazz) {
        if(!isInit()) {
            throw new RuntimeException("PacketEvents not init!!");
        }
        if(managerMap.containsKey(clazz)) {
            return clazz.cast(managerMap.get(clazz));
        }
        return null;
    }

    public static boolean isInit() {
        if(!init) {
            return PacketEvents.getAPI().isLoaded();
        }
        return true;
    }
}
