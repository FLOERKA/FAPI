package ru.floerka.api.utils.skull;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

public final class SkullUtils {

    private static Field profileField;
    private static Method resolvableProfileFactoryMethod;
    private static Constructor<?> resolvableProfileConstructor;

    static {
        try {
            Class<?> craftMetaSkullClass = null;
            try {
                craftMetaSkullClass = Class.forName("org.bukkit.craftbukkit.inventory.CraftMetaSkull");
            } catch (ClassNotFoundException e) {
                for (Package pkg : Package.getPackages()) {
                    if (pkg.getName().startsWith("org.bukkit.craftbukkit.v")) {
                        craftMetaSkullClass = Class.forName(pkg.getName() + ".inventory.CraftMetaSkull");
                        break;
                    }
                }
            }

            if (craftMetaSkullClass != null) {
                profileField = craftMetaSkullClass.getDeclaredField("profile");
                profileField.setAccessible(true);

                Class<?> fieldType = profileField.getType();

                if (!fieldType.equals(GameProfile.class)) {
                    try {
                        resolvableProfileConstructor = fieldType.getDeclaredConstructor(GameProfile.class);
                        resolvableProfileConstructor.setAccessible(true);
                    } catch (NoSuchMethodException e) {
                        for (Method method : fieldType.getDeclaredMethods()) {
                            if (method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(GameProfile.class)) {
                                resolvableProfileFactoryMethod = method;
                                resolvableProfileFactoryMethod.setAccessible(true);
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private SkullUtils() {}

    public static void applySkin(SkullMeta skull, String base64, String profileName) {
        if (skull == null || base64 == null || base64.isEmpty()) {
            return;
        }

        GameProfile profile = new GameProfile(UUID.randomUUID(), profileName != null ? profileName : "CustomSkin");
        profile.getProperties().put("textures", new Property("textures", base64));

        try {
            if (profileField != null) {
                if (resolvableProfileConstructor != null) {
                    Object resolvable = resolvableProfileConstructor.newInstance(profile);
                    profileField.set(skull, resolvable);
                } else if (resolvableProfileFactoryMethod != null) {
                    Object resolvable = resolvableProfileFactoryMethod.invoke(null, profile);
                    profileField.set(skull, resolvable);
                } else {
                    profileField.set(skull, profile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}