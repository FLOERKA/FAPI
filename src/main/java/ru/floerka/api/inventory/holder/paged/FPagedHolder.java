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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class FPagedHolder extends FHolder {

    private final List<Integer> itemsSlots = new ArrayList<>();

    private final List<ItemStack> filledItems = new ArrayList<>();
    private final AtomicInteger currentPlayerPage = new AtomicInteger(0);

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
        System.out.println(itemsSlots);
    }

    public <T> void paintItems(List<T> allItems, ItemConsumer<T> consumer) {
        this.filledItems.clear();

        int playerPage = getCurrentPage();
        int pagesAmount = allItems.size() / itemsSlots.size();
        if(playerPage > pagesAmount) return;
        int itemsPerPage = allItems.size() / (pagesAmount > 0 ? pagesAmount : 1);
        int startSlot = playerPage * itemsPerPage;
        int endSlot = startSlot + itemsPerPage;

        int inventorySlot = 0;
        for(int i = startSlot; i < endSlot; i++) {
            if(allItems.size() > i) {
                T item = allItems.get(i);
                ItemStack stack = consumer.createItem(item);
                getInventory().setItem(itemsSlots.get(inventorySlot), stack);
                this.filledItems.add(stack);
                inventorySlot++;
            }
        }
    }

    public int getCurrentPage() {
        return currentPlayerPage.get();
    }
    public void updatePage(int newPage) {
        currentPlayerPage.set(newPage);
    }
    public boolean nextPage() {
        int currentPage = getCurrentPage();
        if(currentPage+1 > getMaxPages()) {
            return false;
        }
        updatePage(currentPage+1);
        return true;
    }
    public boolean prevPage() {
        int currentPage = getCurrentPage();
        if(currentPage <= 1) return false;
        updatePage(currentPage-1);
        return true;
    }
    public int getMaxPages() {
        int allItems = filledItems.size();
        int itemsPerPage = itemsSlots.size();

        return allItems / itemsPerPage;
    }

    public void clearPlayerPage() {
        currentPlayerPage.set(1);
    }


    public interface ItemConsumer<T> {
        ItemStack createItem(T from);
    }

}
