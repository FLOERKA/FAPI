package ru.floerka.api.wrapper;

import com.github.retrooper.packetevents.protocol.particle.type.ParticleType;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import lombok.experimental.Delegate;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.floerka.api.managers.ParticleManager;
import ru.floerka.api.starter.FAPI;
import ru.floerka.api.utils.converter.VectorUtils;

import java.util.List;

public class FWorld implements World {

    @Delegate
    private final World world;

    public FWorld(World world){
        this.world = world;
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, @NotNull Location location, int i) {
        ParticleType<?> packetParticleType = ParticleTypes.getByName(particle.getKey().getKey());
        com.github.retrooper.packetevents.protocol.particle.Particle<?> packetParticle =
                new com.github.retrooper.packetevents.protocol.particle.Particle<>(packetParticleType);
        ParticleManager particleManager = FAPI.getManager(ParticleManager.class);
        if(particleManager != null) {
            List<Player> viewers = this.getPlayers();
            particleManager.sendParticle(packetParticle, false,
                    VectorUtils.vector3dFromLocation(location), VectorUtils.zeroVector3f(),
                    1, i, viewers);
        }
    }

    public List<FPlayer> getFPlayers() {
        return getPlayers().stream().map(FPlayer::new).toList();
    }
}
