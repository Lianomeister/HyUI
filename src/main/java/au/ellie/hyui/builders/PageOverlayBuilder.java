package au.ellie.hyui.builders;

import au.ellie.hyui.elements.UIElements;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;

import java.util.Set;

/**
 * Builder for the PageOverlay UI element.
 */
public class PageOverlayBuilder extends UIElementBuilder<PageOverlayBuilder> {

    public PageOverlayBuilder() {
        super(UIElements.PAGE_OVERLAY, "#HyUIPageOverlay");
        withWrappingGroup(true);
        withUiFile("Pages/Elements/PageOverlay.ui");
    }

    /**
     * Creates a PageOverlayBuilder instance for a page overlay element.
     *
     * @return a PageOverlayBuilder configured for creating a page overlay with predefined settings.
     */
    public static PageOverlayBuilder pageOverlay() {
        return new PageOverlayBuilder();
    }

    @Override
    protected boolean supportsStyling() {
        return false;
    }

    @Override
    protected void onBuild(UICommandBuilder commands, UIEventBuilder events) {
        // No specific properties for PageOverlay itself yet, but it supports styling via base class.
    }
}
