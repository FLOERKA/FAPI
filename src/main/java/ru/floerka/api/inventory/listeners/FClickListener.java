package ru.floerka.api.inventory.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import ru.floerka.api.inventory.holder.FHolder;

import java.lang.reflect.Method;

public class FClickListener implements Listener {


    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory inventory = e.getClickedInventory();
        if(inventory != null && inventory.getHolder() != null && inventory.getHolder() instanceof FHolder holder) {
            try {
                Method method = holder.getClass().getDeclaredMethod("onClickEvent", InventoryClickEvent.class);
                method.setAccessible(true);
                method.invoke(holder, e);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if(inventory.getHolder() != null && inventory.getHolder() instanceof FHolder holder) {
            try {
                Method method = holder.getClass().getDeclaredMethod("onCloseEvent", InventoryCloseEvent.class);
                method.setAccessible(true);
                method.invoke(holder, event);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
