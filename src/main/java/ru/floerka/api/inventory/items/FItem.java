package ru.floerka.api.inventory.items;

import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.PatchableComponentMap;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemEnchantments;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemProfile;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemUnbreakable;
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes;
import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;
import ru.floerka.api.utils.ColorUtils;
import ru.floerka.api.utils.IntUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FItem {

    private final ItemStack itemStack;
    private final Map<String, Object> customLocalData = new ConcurrentHashMap<>();

    public FItem(ItemStack stack) {
        this.itemStack = stack;
    }
    public FItem(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public static FItem fromItemStack(ItemStack itemStack) {
        return new FItem(itemStack);
    }
    public static FItem empty() {
        return new FItem(new ItemStack(Material.AIR));
    }
    public static FItem fromMaterial(Material material) {
        return new FItem(material);
    }
    public static FItem fromConfig(Section section, String... replacement) {
        FItem fItem = FItem.empty();
        if (section.contains("material")) {
            Material material = Material.matchMaterial(section.getString("material"));
            if(material != null)
                fItem.getItemStack().setType(material);
        }
        fItem.setAmount(section.getInt("amount", 1));

        if(section.contains("enchants") || section.contains("enchantments")) {
            List<String> enchantList = section.getStringList("enchants");
            if(enchantList.isEmpty())
                enchantList = section.getStringList("enchantments");
            enchantList.forEach(enchant -> {
                if(enchant.contains(";")) {
                    String[] enchantSplit = enchant.split(";");
                    if(IntUtils.isInt(enchantSplit[1])) {
                        String name = enchantSplit[0].toUpperCase();
                        int level = Integer.parseInt(enchantSplit[1]);
                        Enchantment enchantment = Enchantment.getByName(name);
                        if(enchantment != null) {
                            fItem.addEnchantment(enchantment, level);
                        }
                    }
                }
            });
        }

        if(section.contains("item-flags")) {
            List<String> flags = section.getStringList("item-flags");
            flags.forEach(flag -> {
                ItemFlag itemFlag = ItemFlag.valueOf(flag.toUpperCase());
                fItem.addItemFlag(itemFlag);
            });
        }

        if(section.contains("lore")) {
            List<String> lore = section.getStringList("lore");
            fItem.setLore(lore, replacement);
        }
        if(section.contains("display")) {
            fItem.setDisplay(section.getString("display"), replacement);
        }
        if(section.contains("skull")) {
            if (fItem.isCustomMeta(SkullMeta.class)) {
                fItem.setCustomHead(section.getString("skull"), "CustomSkull");
            }
        }

        return fItem;
    }

    public void addEnchantment(Enchantment enchantment, int level) {
        itemStack.addUnsafeEnchantment(enchantment, level);
    }
    public void setAmount(int amount) {
        itemStack.setAmount(amount);
    }
    public void addItemFlag(ItemFlag flag) {
        ItemMeta meta = getDefaultMeta();
        meta.getItemFlags().add(flag);
        returnMeta(meta);
    }

    public ItemMeta getDefaultMeta() {
        return itemStack.getItemMeta();
    }

    public void setLore(List<String> lore, String... replacement) {
        List<String> colored = lore.stream().map(s -> {
            if(replacement.length % 2 == 0) {
                for(int i = 0; i < replacement.length-1; i += 2) {
                    s = s.replace(replacement[i], replacement[i + 1]);
                }
            }
            s = ColorUtils.color(s);
            return s;
        }).toList();

        ItemMeta meta = getDefaultMeta();
        meta.setLore(lore);
        returnMeta(meta);
    }
    public void setDisplay(String display, String... replacement) {
        if(replacement.length % 2 == 0) {
            for(int i = 0; i < replacement.length-1; i+=2) {
                display = display.replace(replacement[i], replacement[i+1]);
            }
        }
        display = ColorUtils.color(display);
        ItemMeta meta = getDefaultMeta();
        meta.setDisplayName(display);
        returnMeta(meta);
    }

    public <T extends ItemMeta> boolean isCustomMeta(Class<T> check) {
        ItemMeta meta = getDefaultMeta();
        return check.isInstance(meta);
    }

    public <T extends ItemMeta> T getCustomMeta(Class<T> clazz) {
        ItemMeta meta = getDefaultMeta();
        if(clazz.isInstance(meta)) {
            return clazz.cast(meta);
        }
        return null;
    }

    public void setCustomHead(String base64, String profileName) {
        if (base64 == null || base64.isEmpty()) return;
        if(!isCustomMeta(SkullMeta.class)) return;
        SkullMeta skull = getCustomMeta(SkullMeta.class);

        GameProfile profile = new GameProfile(UUID.randomUUID(), profileName);

        profile.getProperties().put("textures", new Property("textures", base64));

        try {
            Field profileField = skull.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skull, profile);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        returnMeta(skull);
    }

    public void setUnbreakable() {
        ItemMeta meta =getDefaultMeta();
        meta.setUnbreakable(true);
        returnMeta(meta);
    }

    public CustomDataUtil getLocalDataManager() {
        return CustomDataUtil.of(customLocalData);
    }

    public void returnMeta(ItemMeta meta) {
        itemStack.setItemMeta(meta);
    }

    public com.github.retrooper.packetevents.protocol.item.ItemStack toPacketItem() {
        com.github.retrooper.packetevents.protocol.item.ItemStack.Builder builder = com.github.retrooper.packetevents.protocol.item.ItemStack.builder();
        ItemType type = ItemTypes.getByName(getItemStack().getType().name());
        if(type == null) {
            throw new RuntimeException("Invalid itemstack type: " + getItemStack().getType().name());
        }
        builder.type(type);
        builder.amount(getItemStack().getAmount());

        PatchableComponentMap components = PatchableComponentMap.EMPTY;
        ItemMeta defaultMeta = getDefaultMeta();
        if(defaultMeta.hasDisplayName()) {
            components.set(ComponentTypes.CUSTOM_NAME, Component.text(defaultMeta.getDisplayName()));
        }
        if(defaultMeta.hasLore()) {
            ItemLore itemLore = ItemLore.EMPTY;
            itemLore.setLines(defaultMeta.getLore().stream().map(s -> (Component)Component.text(s)).toList());
            components.set(ComponentTypes.LORE, itemLore);
        }
        if(defaultMeta.hasEnchants()) {
            ItemEnchantments enchantments = ItemEnchantments.EMPTY;
            for(var enchant : defaultMeta.getEnchants().entrySet()) {
                enchantments.setEnchantmentLevel(EnchantmentTypes.getByName(enchant.getKey().getKey().getKey()),enchant.getValue());
            }
            components.set(ComponentTypes.ENCHANTMENTS, enchantments);
        }
        if(defaultMeta.isUnbreakable()) {
            components.set(ComponentTypes.UNBREAKABLE_MODERN, new ItemUnbreakable(true));
        }
        if(isCustomMeta(SkullMeta.class)) {
            SkullMeta skullMeta = getCustomMeta(SkullMeta.class);
            ItemProfile.Property textureProperty = new ItemProfile.Property("textures", getBase64FromSkullMeta(skullMeta), null);

            UUID profileId = UUID.randomUUID();
            ItemProfile itemProfile = new ItemProfile("CustomSkull", profileId, Collections.singletonList(textureProperty));
            components.set(ComponentTypes.PROFILE,itemProfile);
        }


        // Были перенесены только базовые свойства предмета. Дописать

        builder.components(components);

        return builder.build();
    }

    public com.github.retrooper.packetevents.protocol.item.ItemStack toPacket() {
        return SpigotConversionUtil.fromBukkitItemStack(getItemStack());
    }

    private String getBase64FromSkullMeta(SkullMeta skullMeta) {
        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            Object gameProfile = profileField.get(skullMeta);

            if (gameProfile == null) return null;

            Field propertiesField = gameProfile.getClass().getDeclaredField("properties");
            propertiesField.setAccessible(true);
            Object propertyMap = propertiesField.get(gameProfile);

            Collection<?> properties = (Collection<?>) propertyMap.getClass().getMethod("values").invoke(propertyMap);

            for (Object property : properties) {
                String name = (String) property.getClass().getMethod("getName").invoke(property);
                if ("textures".equals(name)) {
                    return (String) property.getClass().getMethod("getValue").invoke(property);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    @AllArgsConstructor
    public static class CustomDataUtil {
        private final Map<String,Object> map;

        public <T> void addNote(String key, T object) {
            map.put(key, object);
        }
        public @Nullable <T> T getNote(String key, Class<T> clazz) {
            if(map.containsKey(key)) {
                Object object = map.get(key);
                if(clazz.isInstance(object)) {
                    return clazz.cast(object);
                }
            }
            return null;
        }
        public void deleteNote(String key) {
            map.remove(key);
        }

        public static CustomDataUtil of(Map<String, Object> map) {
            return new CustomDataUtil(map);
        }

    }

}
