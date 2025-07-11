package jp.houlab.mochidsuki.pin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * ENTITY_METADATA パケットをインターセプトし、
 * Utilitiesの判定に基づいてエンティティのグロー状態を偽装するクラス。
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

        // パケットがどのエンティティに関するものかIDを取得
        int targetEntityId = event.getPacket().getIntegers().read(0);

        // ProtocolLibのヘルパーメソッドを使い、エンティティIDから安全にエンティティを取得
        Entity targetEntity = ProtocolLibrary.getProtocolManager().getEntityFromID(observer.getWorld(), targetEntityId);

        // エンティティが見つからない、プレイヤーでない、または自分自身のパケットは処理しない
        // instanceof パターンマッチング (Java 16+) を使うと、より簡潔になります。
        if (!(targetEntity instanceof Player sourcePlayer) || sourcePlayer.getUniqueId().equals(observer.getUniqueId())) {
            return;
        }

        // Utilitiesの判定メソッドを呼び出し、このobserverに対してsourcePlayerを光らせるべきか判定
        if (!Utilities.getGlowPlayerList(observer).contains(sourcePlayer.getUniqueId())) {
            return; // 光らせる必要がなければ何もしない
        }

        // --- ★★★【最重要修正箇所】★★★ ---
        // 元のパケットへの副作用を防ぐため、パケットのディープクローンを作成します。
        PacketContainer newPacket = event.getPacket().deepClone();
        // イベントが送信するパケットを、安全なクローンに差し替えます。
        event.setPacket(newPacket);

        // これ以降の操作は、すべて安全なクローンに対して行われます。
        List<WrappedDataValue> metadata = newPacket.getDataValueCollectionModifier().read(0);

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
                        WrappedDataWatcher.Registry.get(Byte.class),
                        GLOWING_FLAG
                )
        );
    }
}