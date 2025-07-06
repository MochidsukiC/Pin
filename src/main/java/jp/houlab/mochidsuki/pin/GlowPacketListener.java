package jp.houlab.mochidsuki.pin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary; // ProtocolLibの本体をインポートします
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * ENTITY_METADATA パケットをインターセプトし、
 * Vクラスの判定に基づいてエンティティのグロー状態を偽装するクラス。
 */
public class GlowPacketListener extends PacketAdapter {

    // 発光状態を示すビットフラグ (6番目のビット)
    private static final byte GLOWING_FLAG = (byte) (1 << 6);

    public GlowPacketListener(Plugin plugin) {
        super(plugin, PacketType.Play.Server.ENTITY_METADATA);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        // パケットの受信者 (このパケットを見るプレイヤー)
        Player observer = event.getPlayer();
        PacketContainer packet = event.getPacket();

        // パケットがどのエンティティに関するものかIDを取得
        int targetEntityId = packet.getIntegers().read(0);

        // ▼▼▼【修正箇所】▼▼▼
        // ProtocolLibのヘルパーメソッドを使い、エンティティIDから安全にエンティティを取得します
        Entity targetEntity = ProtocolLibrary.getProtocolManager().getEntityFromID(observer.getWorld(), targetEntityId);

        // エンティティが見つからない、プレイヤーでない、または自分自身のパケットは処理しない
        // getEntityFromIDはエンティティが見つからない場合にnullを返すため、nullチェックを追加するとより安全です
        if (targetEntity == null || !(targetEntity instanceof Player) || targetEntity.getUniqueId().equals(observer.getUniqueId())) {
            return;
        }
        Player sourcePlayer = (Player) targetEntity;

        // Vクラスの判定メソッドを呼び出し、このobserverに対してsourcePlayerを光らせるべきか判定
        if (!Utilities.getGlowPlayerList(observer).contains(sourcePlayer.getUniqueId())) {
            return; // 光らせる必要がなければ何もしない
        }

        // --- メタデータの書き換え処理 ---
        List<WrappedDataValue> metadata = packet.getDataValueCollectionModifier().read(0);

        // インデックス0のメタデータ(基本状態フラグ)を探して編集する
        for (WrappedDataValue value : metadata) {
            if (value.getIndex() == 0) {
                byte originalFlags = (byte) value.getValue();
                // ビット演算で既存の状態を壊さずにグローフラグだけをONにする
                byte newFlags = (byte) (originalFlags | GLOWING_FLAG);
                value.setValue(newFlags); // 値を直接更新
                return; // 処理が完了したのでメソッドを抜ける
            }
        }

        /*
         * 通常、プレイヤーのメタデータパケットには必ずインデックス0が含まれますが、
         * 万が一含まれていなかった場合のフォールバック処理です。
         * 新しくグローフラグを持つメタデータをリストに追加します。
         */
        metadata.add(
                new WrappedDataValue(
                        0,
                        com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry.get(Byte.class),
                        GLOWING_FLAG
                )
        );
    }
}