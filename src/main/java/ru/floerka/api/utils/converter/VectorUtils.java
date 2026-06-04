package ru.floerka.api.utils.converter;

import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.util.Vector3i;
import org.bukkit.Location;
import org.bukkit.World;

public class VectorUtils {


    public static Location locationFromVector(Vector3d vector3d, World world) {
        return new Location(world, vector3d.getX(), vector3d.getY(), vector3d.getZ());
    }
    public static Vector3d vector3dFromLocation(Location location) {
        return new Vector3d(location.getX(), location.getY(), location.getZ());
    }
    public static Vector3f vector3fFromLocation(Location location) {
        return new Vector3f((float) location.getX(), (float) location.getY(), (float) location.getZ());
    }
    public static Vector3i vector3iFromLocation(Location location) {
        return new Vector3i(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
    public static Vector3f zeroVector3f() {
        return new Vector3f(0f, 0f, 0f);
    }
}
