package au.ellie.hyui.builders;

import au.ellie.hyui.events.UIContext;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A HUD for Hytale. 
 * It is important to store references to your existing HUDs to assist with updating elements.
 */
public class HyUIHud extends CustomUIHud implements UIContext {
    protected final HyUInterface delegate;

    private boolean isHidden;
    private HyUIMultiHud parentMultiHud;
    
    public HyUIHud(PlayerRef playerRef, String uiFile, 
                   List<UIElementBuilder<?>> elements, 
                   List<Consumer<UICommandBuilder>> editCallbacks) {
        super(playerRef);
        this.delegate = new HyUInterface(uiFile, elements, editCallbacks) {};
    }
    
    @Override
    public void build(UICommandBuilder uiCommandBuilder) {
        delegate.buildFromCommandBuilder(uiCommandBuilder);
    }

    /**
     * Updates the HUD with the provided builder.
     * The builder can be a completely new configuration.
     * 
     * @param updatedHudBuilder The builder containing updated HUD configuration.
     */
    public void update(HudBuilder updatedHudBuilder) {
        UICommandBuilder builder = configureFrom(updatedHudBuilder);
        this.update(true, builder);
    }

    /**
     * Remove the HUD from its parent multi-HUD. 
     * This will remove it from the screen for the player.
     * and stop refreshing it.
     * 
     * You can later associate it with another, or the same multi-HUD and show it.
     */
    public void removeFromMultiHud() {
        if (parentMultiHud != null) {
            parentMultiHud.removeHud(this);

        }
    }
    
    /**
     * Sets the multi-hud parent, this will show the HUD.
     *
     * @param parentMultiHud The multi-hud to associate with. If it is null, it will clear the parent.
     */
    public void showWithMultiHud(HyUIMultiHud parentMultiHud) {
        this.parentMultiHud = parentMultiHud;
        if (this.parentMultiHud != null)
            // Redraw parent.
            this.parentMultiHud.show();
    }
    
    /**
     * Hides the UI from view of player.
     */
    public void hide() {
        setVisibilityOnFirstElement(false);
    }

    /**
     * Shows the UI to the player if it has previously been hidden.
     */
    public void unhide() {
        setVisibilityOnFirstElement(true);
    }
    
    @Override
    public Optional<Object> getValue(String id) {
        return delegate.getValue(id);
    }

    private void setVisibilityOnFirstElement(boolean value) {
        for (UIElementBuilder<?> element : delegate.getElements()) {
            element.withVisible(value);
            break;
        }
        UICommandBuilder builder = new UICommandBuilder();
        delegate.buildFromCommandBuilder(builder);
        this.update(true, builder);
        isHidden = !isHidden;
    }

    private UICommandBuilder configureFrom(HudBuilder updatedHudBuilder) {
        UICommandBuilder builder = new UICommandBuilder();
        delegate.setEditCallbacks(updatedHudBuilder.editCallbacks);
        delegate.setElements(updatedHudBuilder.getTopLevelElements());
        delegate.setUiFile(updatedHudBuilder.uiFile);
        return builder;
    }
}
