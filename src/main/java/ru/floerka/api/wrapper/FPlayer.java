package ru.floerka.api.wrapper;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleType;
import com.github.retrooper.packetevents.protocol.sound.SoundCategory;
import com.github.retrooper.packetevents.protocol.sound.Sounds;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import lombok.experimental.Delegate;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.floerka.api.managers.ParticleManager;
import ru.floerka.api.managers.SoundManager;
import ru.floerka.api.starter.FAPI;
import ru.floerka.api.utils.converter.VectorUtils;

import java.util.List;

// Класс предназначен для переопределения игрока и использование обычных методов, но имеющих также пакетные версии
public class FPlayer implements Player {

    @Delegate
    private final Player handle;

    public FPlayer(Player player) {
        this.handle = player;
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull Sound sound, float v, float v1) {
        String soundName = sound.getKey().getKey();
        com.github.retrooper.packetevents.protocol.sound.Sound packetSound = Sounds.getByName(soundName);
        SoundManager soundManager = FAPI.getManager(SoundManager.class);
        if(packetSound != null && soundManager != null) {
            soundManager.playSound(packetSound, SoundCategory.NEUTRAL,
                    VectorUtils.vector3iFromLocation(location), v,v1, List.of(this));
        } else {
            this.playSound(location,soundName,v,v1);
        }
    }

    public void sendParticle(ParticleType<?> type, boolean longDistance, Vector3d position, Vector3f offset,
                             float maxSpeed, int particleCount) {
        ParticleManager particleManager = FAPI.getManager(ParticleManager.class);
        if(particleManager != null) {
            Particle<?> particle = new Particle<>(type);
            particleManager.sendParticle(particle, longDistance, position, offset, maxSpeed, particleCount, List.of(this));
        }
    }

    public void sendParticle(ParticleType<?> type, Location location, int speed, int count) {
        sendParticle(type, false, VectorUtils.vector3dFromLocation(location)
        , VectorUtils.zeroVector3f(), speed, count);
    }
    public void sendParticle(ParticleType<?> type, Location location, int count) {
        sendParticle(type, false, VectorUtils.vector3dFromLocation(location)
                , VectorUtils.zeroVector3f(), 1f, count);
    }
    public void sendParticle(ParticleType<?> type, Location location) {
        sendParticle(type, false, VectorUtils.vector3dFromLocation(location)
                , VectorUtils.zeroVector3f(), 1f, 1);
    }

    public void sendPacket(Object wrapper) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(handle, wrapper);
    }
    public void receivePacket(Object packet) {
        PacketEvents.getAPI().getPlayerManager().receivePacket(handle, packet);
    }
    public FWorld getFWorld() {
        return new FWorld(getWorld());
    }


}
