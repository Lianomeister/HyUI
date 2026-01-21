package au.ellie.hyui.builders;

import au.ellie.hyui.HyUIPlugin;
import au.ellie.hyui.elements.BackgroundSupported;
import au.ellie.hyui.elements.LayoutModeSupported;
import au.ellie.hyui.elements.UIElements;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;

/**
 * Builder for the AssetImage UI element.
 */
public class ImageBuilder extends UIElementBuilder<ImageBuilder> implements LayoutModeSupported<ImageBuilder> {
    private String imagePath;
    private String layoutMode;
    private HyUIPatchStyle background;

    public ImageBuilder() {
        super(UIElements.ASSET_IMAGE, "#HyUIAssetImage");
        withWrappingGroup(true);
        withUiFile("Pages/Elements/AssetImage.ui");
    }

    /**
     * Creates an ImageBuilder instance for an asset image element.
     *
     * @return an ImageBuilder configured for creating an asset image with predefined settings.
     */
    public static ImageBuilder image() {
        return new ImageBuilder();
    }

    /**
     * Sets the image file path relative to the Custom/UI/Common folder.
     *
     * @param imagePath the relative path to the image file.
     * @return this ImageBuilder instance.
     */
    public ImageBuilder withImage(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    @Override
    public ImageBuilder withLayoutMode(String layoutMode) {
        this.layoutMode = layoutMode;
        return this;
    }
    
    @Override
    public String getLayoutMode() {
        return this.layoutMode;
    }
    

    @Override
    protected boolean supportsStyling() {
        return false;
    }

    @Override
    protected void onBuild(UICommandBuilder commands, UIEventBuilder events) {
        String selector = getSelector();
        if (selector == null) return;

        String wrappingGroupSelector = "#" + getWrappingGroupId();
        applyLayoutMode(commands, wrappingGroupSelector);

        if (imagePath != null) {
            HyUIPlugin.getLog().logInfo("Setting AssetPath on " + selector + " to " + imagePath);
            commands.set(selector + ".AssetPath", "UI/Custom/" + imagePath);
        }
    }
}
