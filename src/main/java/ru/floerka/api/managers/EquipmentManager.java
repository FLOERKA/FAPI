package ru.floerka.api.managers;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import ru.floerka.api.starter.models.Manager;

import java.util.*;

public class EquipmentManager extends Manager {

    private final Map<UUID, List<UUID>> fakedEquipment = new HashMap<>();

    public void fakeEquipment(Player player, List<Player> viewers, List<Equipment> equipment) {
        WrapperPlayServerEntityEquipment wrapper = new WrapperPlayServerEntityEquipment(player.getEntityId(), equipment);
        viewers.forEach(viewer -> {
            PacketEvents.getAPI().getPlayerManager().sendPacket(viewer, wrapper);
            fakedEquipment.compute(player.getUniqueId(), (a,b) -> {
                List<UUID> list;
                if(b == null) {
                    list = new ArrayList<>();
                } else {
                    list = b;
                    b.add(viewer.getUniqueId());
                }
                return list;
            });
        });
    }
    public void returnEquipment(Player player) {
        if(fakedEquipment.containsKey(player.getUniqueId())) {
            List<UUID> viewers = fakedEquipment.get(player.getUniqueId());
            List<Player> players = new ArrayList<>(viewers.stream().map(Bukkit::getPlayer).toList());
            players.removeIf(Objects::isNull);

            EntityEquipment equipment = player.getEquipment();
            List<Equipment> newEquipment = new ArrayList<>();
            if(equipment != null) {
                if(equipment.getHelmet() != null) {
                    newEquipment.add(new Equipment(EquipmentSlot.HELMET, SpigotConversionUtil.fromBukkitItemStack(equipment.getHelmet())));
                }
                if(equipment.getChestplate() != null) {
                    newEquipment.add(new Equipment(EquipmentSlot.CHEST_PLATE, SpigotConversionUtil.fromBukkitItemStack(equipment.getChestplate())));
                }
                if(equipment.getLeggings() != null) {
                    newEquipment.add(new Equipment(EquipmentSlot.LEGGINGS, SpigotConversionUtil.fromBukkitItemStack(equipment.getLeggings())));
                }
                if(equipment.getBoots() != null) {
                    newEquipment.add(new Equipment(EquipmentSlot.BOOTS, SpigotConversionUtil.fromBukkitItemStack(equipment.getBoots())));
                }
                if(equipment.getItemInMainHand() != null) {
                    newEquipment.add(new Equipment(EquipmentSlot.MAIN_HAND, SpigotConversionUtil.fromBukkitItemStack(equipment.getItemInMainHand())));
                }
                if(equipment.getItemInOffHand() != null) {
                    newEquipment.add(new Equipment(EquipmentSlot.OFF_HAND, SpigotConversionUtil.fromBukkitItemStack(equipment.getItemInOffHand())));
                }
            }

            fakeEquipment(player, players, newEquipment);
        }
    }
}
