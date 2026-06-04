package ru.floerka.api.inventory.holder.paged;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import ru.floerka.api.inventory.holder.FHolder;
import ru.floerka.api.utils.IntUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class FPagedHolder extends FHolder {

    private final List<Integer> itemsSlots = new ArrayList<>();

    private final Cache<UUID, Integer> pages = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.MINUTES)
            .build();

    public FPagedHolder(int size, String title) {
        super(size, title);
        setDefaultItemsSlots();
    }

    public FPagedHolder(int size) {
        super(size);
        setDefaultItemsSlots();
    }

    public FPagedHolder(InventoryType type, String title) {
        super(type, title);
        setDefaultItemsSlots();
    }

    public FPagedHolder(InventoryType type) {
        super(type);
        setDefaultItemsSlots();
    }

    private void setDefaultItemsSlots() {
        IntStream.range(0,getInventory().getSize()).forEach(itemsSlots::add);
    }

    public void setItemsSlots(List<Integer> list) {
        itemsSlots.clear();
        itemsSlots.addAll(list);
    }
    public void setItemsSlots(String list) {
        itemsSlots.clear();
        if(list.contains(",")) {
            String[] split = list.replace(" ", "").split(",");
            for(String s : split) {
                if(IntUtils.isInt(s)) {
                    itemsSlots.add(Integer.parseInt(s));
                }
            }
        }
    }

    public <T> void paintItems(List<T> allItems, UUID player, ItemConsumer<T> consumer) {
        int playerPage = getPlayerPage(player);
        int pagesAmount = allItems.size() / itemsSlots.size();
        if(playerPage > pagesAmount) return;
        int itemsPerPage = allItems.size() / pagesAmount;
        int startSlot = playerPage * itemsPerPage;
        int endSlot = startSlot + itemsPerPage;

        for(int i = startSlot; i < endSlot; i++) {
            if(allItems.size() > i) {
                T item = allItems.get(i);
                ItemStack stack = consumer.createItem(item);
                getInventory().setItem(i, stack);
            }
        }
    }

    public int getPlayerPage(UUID player) {
        if(pages.asMap().containsKey(player)) {
            return pages.getIfPresent(player);
        } else return 1;
    }

    public void clearPlayerPage(UUID player) {
        pages.invalidate(player);
    }


    public interface ItemConsumer<T> {
        ItemStack createItem(T from);
    }

}
