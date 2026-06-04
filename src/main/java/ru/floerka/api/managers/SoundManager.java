package ru.floerka.api.managers;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.sound.Sound;
import com.github.retrooper.packetevents.protocol.sound.SoundCategory;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSoundEffect;
import org.bukkit.entity.Player;
import ru.floerka.api.starter.models.Manager;

import java.util.List;

public class SoundManager extends Manager {


    public void playSound(Sound sound, SoundCategory category, Vector3i position,
                                 float volume, float pitch, List<Player> entities) {
        WrapperPlayServerSoundEffect wrapper = new WrapperPlayServerSoundEffect(
                sound, category, position, volume, pitch
        );
        entities.forEach(entity -> PacketEvents.getAPI().getPlayerManager().sendPacket(entities, wrapper));
    }
}
