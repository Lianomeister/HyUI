package au.ellie.hyui.builders;

import au.ellie.hyui.HyUIPlugin;
import au.ellie.hyui.elements.BackgroundSupported;
import au.ellie.hyui.elements.UIElements;
import au.ellie.hyui.theme.Theme;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import java.util.Set;

/**
 * Builder for creating label UI elements. 
 * Labels are used to display text or other static content.
 */
public class LabelBuilder extends UIElementBuilder<LabelBuilder> implements BackgroundSupported<LabelBuilder> {
    private String text;
    private HyUIPatchStyle background;

    /**
     * Constructs a new instance of {@code LabelBuilder} for creating label UI elements.
     * This class is used to define and customize labels which are used to display text 
     * or other static content in the user interface.
     *
     * By default, the label element type is set to {@code UIElements.LABEL}.
     */
    public LabelBuilder() {
        super(UIElements.LABEL, "Label");
    }

    public LabelBuilder(Theme theme) {
        super(theme, UIElements.LABEL, "Label");
    }

    /**
     * Factory method to create a new instance of {@code LabelBuilder}.
     *
     * @return A new {@code LabelBuilder} instance for creating and customizing labels.
     */
    public static LabelBuilder label() {
        return new LabelBuilder();
    }

    /**
     * Sets the text to be displayed by the label being built.
     *
     * @param text The text content to set for the label. This will replace any
     *             previously set text value.
     *
     * @return The current instance of the {@code LabelBuilder} for method chaining.
     */
    public LabelBuilder withText(String text) {
        this.text = text;
        return this;
    }

    public String getText() {
        return text;
    }

    @Override
    public LabelBuilder withBackground(HyUIPatchStyle background) {
        this.background = background;
        return this;
    }

    @Override
    public HyUIPatchStyle getBackground() {
        return this.background;
    }

    @Override
    protected boolean supportsStyling() {
        return true;
    }

    @Override
    protected boolean isStyleWhitelist() {
        return true;
    }

    @Override
    protected Set<String> getSupportedStyleProperties() {
        return Set.of(
                "FontSize",
                "RenderBold",
                "RenderUppercase",
                "TextColor",
                "HorizontalAlignment",
                "VerticalAlignment",
                "Alignment",
                "RenderItalics",
                "FontName",
                "Wrap",
                "LetterSpacing",
                "OutlineColor"
        );
    }

    @Override
    protected void onBuild(UICommandBuilder commands, UIEventBuilder events) {
        String selector = getSelector();
        if (selector == null) return;

        applyBackground(commands, selector);

        if (text != null) {
            HyUIPlugin.getLog().logInfo("Setting Text: " + text + " for " + selector);
            commands.set(selector + ".Text", text);
        }

        if (hyUIStyle == null && style != null) {
            HyUIPlugin.getLog().logInfo("Setting Raw Style: " + style + " for " + selector);
            commands.set(selector + ".Style", style);
        }
    }
}
