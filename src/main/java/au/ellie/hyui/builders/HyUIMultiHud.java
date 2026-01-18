package au.ellie.hyui.builders;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A CustomUIHud that can aggregate multiple HyUIHud instances.
 * This allows multiple HUD elements to coexist in the single HUD slot provided by Hytale.
 */
public class HyUIMultiHud extends CustomUIHud {
    private final Map<String, HyUIHud> huds = new LinkedHashMap<>();
    private final Map<String, HyUIHud> removedHuds = new LinkedHashMap<>();

    public HyUIMultiHud(PlayerRef playerRef) {
        super(playerRef);
    }

    /**
     * Adds or updates a HUD in this multi-hud.
     * @param hud The HyUIHud instance.
     */
    public void setHud(String name, HyUIHud hud) {
        huds.put(name, hud);
        hud.showWithMultiHud(this);
    }

    /**
     * Removes a HUD from this multi-hud.
     * @param name The name of the HUD to remove.
     */
    public void removeHud(String name) {
        HyUIHud removed = huds.remove(name);
        if (removed != null) {
            removed.showWithMultiHud(null);
            // Redraw self.
            this.show();
        }
    }
    
    /**
     * Removes a HUD from this multi-hud.
     * @param instance The instance to remove.
     */
    public void removeHud(HyUIHud instance) {
        String keyToRemove = null;
        for (Map.Entry<String, HyUIHud> entry : huds.entrySet()) {
            if (entry.getValue() == instance) {
                keyToRemove = entry.getKey();
                break;
            }
        }
        if (keyToRemove != null) {
            removeHud(keyToRemove);
            return;
        }

        for (Map.Entry<String, HyUIHud> entry : removedHuds.entrySet()) {
            if (entry.getValue() == instance) {
                keyToRemove = entry.getKey();
                break;
            }
        }
        if (keyToRemove != null) {
            removedHuds.remove(keyToRemove);
        }
        
        // Redraw self.
        this.show();
    }

    /**
     * Hides a HUD from display by moving it to the removed list.
     * @param name The name of the HUD to hide.
     */
    public void hideHud(String name) {
        HyUIHud hud = huds.remove(name);
        if (hud != null) {
            removedHuds.put(name, hud);
            // Redraw self.
            this.show();
        }
    }

    /**
     * Hides a HUD from display by moving it to the removed list.
     * @param instance The HUD instance to hide.
     */
    public void hideHud(HyUIHud instance) {
        String keyToHide = null;
        for (Map.Entry<String, HyUIHud> entry : huds.entrySet()) {
            if (entry.getValue() == instance) {
                keyToHide = entry.getKey();
                break;
            }
        }
        if (keyToHide != null) {
            hideHud(keyToHide);
        }
    }

    /**
     * Shows a HUD by moving it back to the main list.
     * @param name The name of the HUD to show.
     */
    public void showHud(String name) {
        HyUIHud hud = removedHuds.remove(name);
        if (hud != null) {
            huds.put(name, hud);
            // Redraw self.
            this.show();
        }
    }

    /**
     * Shows a HUD by moving it back to the main list.
     * @param instance The HUD instance to show.
     */
    public void showHud(HyUIHud instance) {
        String keyToShow = null;
        for (Map.Entry<String, HyUIHud> entry : removedHuds.entrySet()) {
            if (entry.getValue() == instance) {
                keyToShow = entry.getKey();
                break;
            }
        }
        if (keyToShow != null) {
            showHud(keyToShow);
        }
    }

    @Override
    public void build(@Nonnull UICommandBuilder uiCommandBuilder) {
        for (HyUIHud hud : huds.values()) {
            hud.build(uiCommandBuilder);
        }
    }
}
