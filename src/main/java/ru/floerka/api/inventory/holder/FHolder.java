package ru.floerka.api.inventory.holder;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.floerka.api.inventory.exceptions.InvalidSlotException;
import ru.floerka.api.inventory.holder.paged.FPagedHolder;
import ru.floerka.api.inventory.items.FItem;
import ru.floerka.api.utils.IntUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FHolder implements InventoryHolder {


    private final Map<Integer, FItem> customItems = new HashMap<>();
    private OnClick onClick = event -> {};
    private OnClose onClose = event -> {};
    private final Inventory inventory;

    public FHolder(int size, String title) {
        this.inventory = Bukkit.createInventory(this, parseSize(size), title);
    }
    public FHolder(int size) {
        this.inventory = Bukkit.createInventory(this, parseSize(size));
    }

    public FHolder(InventoryType type, String title) {
        this.inventory = Bukkit.createInventory(this, type, title);
    }
    public FHolder(InventoryType type) {
        this.inventory = Bukkit.createInventory(this, type);
    }

    public void onClick(OnClick click) {
        this.onClick = click;
    }
    public void onClose(OnClose close) {
        this.onClose = close;
    }

    private int parseSize(int size) {
        if(size <= 6) return size * 9;
        if(size % 9 != 0) {
            for(int i = 1; i <= 9; i++) {
                int i1 = size + i;
                int i2 = size - i;
                if(i1 % 9 == 0)
                    return i1;
                else if(i2 % 9 == 0) return i2;
            }
        }
        throw new RuntimeException("Incorrect inventory size: " + size);
    }

    public void setMatrixItems(List<String> matrix, Map<Character, ItemStack> items) {
        int slot = 0;
        cycle: for(String matrixRow : matrix) {
            for(char matrixSlot : matrixRow.toCharArray()) {
                if(items.containsKey(matrixSlot)) {
                    ItemStack itemStack = items.get(matrixSlot);
                    if(itemStack != null) {
                        inventory.setItem(slot, itemStack);
                    }
                }
                slot++;
                if(slot >= inventory.getSize()) {
                    break cycle;
                }
            }
        }
    }
    private void setIntSlot(int slot, Object item) {
        if(item instanceof ItemStack stack) {
            inventory.setItem(slot, stack);
        } else if(item instanceof FItem fItem) {
            customItems.put(slot, fItem);
            setItem(slot, fItem.getItemStack());
        }
    }

    public void setItem(Object slot, Object itemStack) {

        if (slot instanceof Integer) {
            int intSlot = (int) slot;
            if (intSlot >= inventory.getSize()) {
                throw new InvalidSlotException(intSlot);
            }
            setIntSlot(intSlot, itemStack);
        } else if (slot instanceof String strSlot) {
            if (IntUtils.isInt(strSlot)) {
                setItem(Integer.parseInt(strSlot), itemStack);
            } else {
                if (strSlot.contains(",")) {
                    String[] split = strSlot.replace(" ", "").split(",");
                    for (String s : split) {
                        setItem(s, itemStack);
                    }
                }
            }

        } else if (slot instanceof Section section) {
            if (section.isInt("slot")) {
                int intSlot = section.getInt("slot");
                setItem(intSlot, itemStack);
            } else if (section.isString("slot")) {
                String strSlot = section.getString("slot");
                setItem(strSlot, itemStack);
            } else if (!section.getStringList("slot").isEmpty()) {
                List<String> abstarctList = section.getStringList("slot");
                abstarctList.forEach(strSlot -> setItem(strSlot, itemStack));
            } else if (!section.getIntList("slot").isEmpty()) {
                List<Integer> intList = section.getIntList("slot");
                intList.forEach(intSlot -> setItem(intSlot, itemStack));
            }

        }
    }

    private void onClickEvent(InventoryClickEvent event) {
        if(event.getWhoClicked() instanceof Player player) {
            ItemStack clicked = event.getCurrentItem();
            FItem fItem = null;
            if (customItems.containsKey(event.getSlot())) {
                fItem = customItems.get(event.getSlot());
            } else {
                if(clicked != null) {
                    fItem = FItem.fromItemStack(clicked);
                }
            }
            onClick.onClick(new ClickEvent(player,fItem,event));
        }
    }
    private void onCloseEvent(InventoryCloseEvent event) {
        if(event.getPlayer() instanceof Player player) {
            Inventory inv = event.getInventory();
            if(this instanceof FPagedHolder page) {
                page.clearPlayerPage(player.getUniqueId());
            }
            this.onClose.onClose(new CloseEvent(player, inv, event));
        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public record ClickEvent(Player player, FItem item, InventoryClickEvent event) {}

    public interface OnClick {
        void onClick(ClickEvent event);
    }

    public record CloseEvent(Player player, Inventory inventory, InventoryCloseEvent event) {}

    public interface OnClose {
        void onClose(CloseEvent event);
    }
}
