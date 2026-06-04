package ru.floerka.api.managers;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;
import org.bukkit.entity.Player;
import ru.floerka.api.starter.models.Manager;

import java.util.List;

public class ParticleManager extends Manager {

    public void sendParticle(Particle<?> particle, boolean longDistance, Vector3d position, Vector3f offset,
                             float maxSpeed, int particleCount, List<Player> visitors) {
        WrapperPlayServerParticle wrapper = new WrapperPlayServerParticle(particle, longDistance, position, offset, maxSpeed, particleCount);
        visitors.forEach(visitor -> PacketEvents.getAPI().getPlayerManager().sendPacket(visitor, wrapper));
    }


}
