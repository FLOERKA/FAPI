package ru.floerka.api.commands.listeners;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandListener extends PacketListenerAbstract implements Listener {

    private final ExecutorService service;

    public CommandListener(JavaPlugin javaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, javaPlugin);
        service = Executors.newCachedThreadPool();
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CHAT_COMMAND) {
            WrapperPlayClientChatCommand wrapper = new WrapperPlayClientChatCommand(event);
            String command = wrapper.getCommand();

            String mainCommand;
            String[] args;
            if(command.contains(" ")) {
                String[] split = command.split(" ");
                args = Arrays.copyOfRange(split, 1, split.length);
                mainCommand = split[0];
            } else {
                mainCommand = command;
                args = new String[0];
            }


        }
        super.onPacketReceive(event);
    }


    public void terminate() {
        service.shutdown();
    }
}
