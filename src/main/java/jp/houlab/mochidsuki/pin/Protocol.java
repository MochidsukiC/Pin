package jp.houlab.mochidsuki.pin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static jp.houlab.mochidsuki.pin.Pin.protocolManager;

/**
 * パケットを作成、編集し通信にねじ込む
 * @author Mochidsuki
 */
public class Protocol {
    /**
     * ピンを作成、送信する
     * @param player 送信先
     * @param locations ピンの地点
     * @param entityType ピンのタイプ
     * @param entityIdPlus エンティティIDオフセット
     */
    public void pushPin(Player player, Location[] locations, EntityType entityType, int entityIdPlus) {
        int entityId = 10000 + entityIdPlus;


        for(Location location : locations) {

            Location loc = new Location(player.getWorld(),0,0,0);
            if(location != null) {
                double x = location.getX() - player.getLocation().getX();
                double y = location.getY() - player.getLocation().getY();
                double z = location.getZ() - player.getLocation().getZ();
                double d = Math.sqrt(Math.abs(y * y + (x * x + z * z)));
                if (d > 160) {
                    loc.setX(player.getLocation().getX() + x * 160 / d);
                    loc.setY(player.getLocation().getY() + y * 160 / d);
                    loc.setZ(player.getLocation().getZ() + z * 160 / d);
                } else {
                    loc = location;
                }
            }



            entityId++;


            PacketContainer packet0 = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);

            packet0.getIntegers().write(0, entityId);
            packet0.getUUIDs().write(0, UUID.randomUUID());
            packet0.getEntityTypeModifier().write(0, entityType);

            packet0.getDoubles()
                    .write(0, loc.getX())
                    .write(1, loc.getY())
                    .write(2, loc.getZ());

            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
            WrappedDataWatcher.Serializer byteType = WrappedDataWatcher.Registry.get(Byte.class);

            //EasyMetadataPacket metadata = new EasyMetadataPacket(null); // Pass the NMS entity, or null as we're dealing with a client-side entity

            byte bitmask = 0x00; // First bitmask, 0x00 by default
            bitmask |= 0x20; // is invisible
            if(location != null) {
                bitmask |= 0x40; // is glowing
            }

            //metadata.write(0, bitmask); // Write the first bitmask

            packet.getIntegers().write(0, entityId);

            List<WrappedDataValue> values = Lists.newArrayList(
                    new WrappedDataValue(0, byteType, bitmask)
            );
            packet.getDataValueCollectionModifier().write(0,values);
            /*
            WrappedDataWatcher watcher = new WrappedDataWatcher();
            WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
            watcher.setEntity(player);
            watcher.setObject(0, serializer, (byte) (0x40));
            packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());


             */



            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet0);
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            } catch (Exception ignored) {
            }
        }

    }

    /**
     * 一部のプレイヤーのみ見える発光エフェクトを付与する。
     * @param glowingPlayer 発光させるプレイヤー
     * @param sendPacket 送信先
     */
    public void setGlowing(Player glowingPlayer, Player sendPacket) {

        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getIntegers().write(0, glowingPlayer.getEntityId());

        WrappedDataWatcher.Serializer byteType = WrappedDataWatcher.Registry.get(Byte.class);


        byte bitmask = 0x00; // First bitmask, 0x00 by default
        bitmask |= 0x40; // is glowing

        List<WrappedDataValue> values = Lists.newArrayList(
                new WrappedDataValue(0, byteType, bitmask)
        );
        packet.getDataValueCollectionModifier().write(0,values);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(sendPacket, packet);
        } catch (Exception e) {
            System.out.println("There was an issue with one of the glowing enchants!");
        }

    }

}