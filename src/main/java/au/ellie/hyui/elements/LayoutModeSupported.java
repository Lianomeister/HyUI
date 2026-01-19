package au.ellie.hyui.elements;

import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import au.ellie.hyui.HyUIPlugin;

/**
 * Interface for UI elements that support LayoutMode.
 */
public interface LayoutModeSupported<T extends LayoutModeSupported<T>> {
    
    /**
     * Sets the layout mode for the element.
     * 
     * @param layoutMode The layout mode to set (e.g., Left, Top, TopScrolling, Right, Full).
     * @return This builder instance for method chaining.
     */
    T withLayoutMode(String layoutMode);

    /**
     * Gets the current layout mode.
     * 
     * @return The layout mode, or null if not set.
     */
    String getLayoutMode();

    /**
     * Default implementation to apply the layout mode to the UICommandBuilder.
     * 
     * @param commands The UICommandBuilder to use.
     * @param selector The selector for the element.
     */
    default void applyLayoutMode(UICommandBuilder commands, String selector) {
        String mode = getLayoutMode();
        if (mode != null && selector != null) {
            HyUIPlugin.getLog().logInfo("Setting LayoutMode: " + mode + " for " + selector);
            commands.set(selector + ".LayoutMode", mode);
        }
    }
}
