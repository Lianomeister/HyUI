package au.ellie.hyui.builders;

import au.ellie.hyui.HyUIPlugin;
import au.ellie.hyui.elements.UIElements;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;

/**
 * Builder for the Container UI element.
 */
public class ContainerBuilder extends UIElementBuilder<ContainerBuilder> {
    private String titleText;

    public ContainerBuilder() {
        super(UIElements.CONTAINER, "#HyUIContainer");
        withWrappingGroup(true);
        withUiFile("Pages/Elements/Container.ui");
    }

    /**
     * Creates a ContainerBuilder instance for a container element.
     *
     * @return a ContainerBuilder configured for creating a container with predefined settings.
     */
    public static ContainerBuilder container() {
        return new ContainerBuilder();
    }

    /**
     * Sets the text for the container's title.
     *
     * @param titleText the title text to set
     * @return the {@code ContainerBuilder} instance for method chaining
     */
    public ContainerBuilder withTitleText(String titleText) {
        this.titleText = titleText;
        return this;
    }

    @Override
    protected boolean supportsStyling() {
        return true;
    }

    @Override
    protected void onBuild(UICommandBuilder commands, UIEventBuilder events) {
        String selector = getSelector();
        if (selector == null) return;

        if (titleText != null) {
            String titleSelector = selector + " #Title #HyUIContainerTitle";
            HyUIPlugin.getInstance().logInfo("Setting Title Text: " + titleText + " for " + titleSelector);
            commands.set(titleSelector + ".Text", titleText);
        }
    }

    @Override
    protected void buildChildren(UICommandBuilder commands, UIEventBuilder events) {
        String selector = getSelector();
        if (selector != null) {
            for (UIElementBuilder<?> child : children) {
                // We want to make sure children can be placed in #Title or #Content.
                // UIElementBuilder.inside() sets parentSelector.
                String childParent = child.parentSelector;
                if (childParent.equals("#Content")) {
                    child.inside(selector + " #Content").build(commands, events);
                } else if (childParent.equals("#Title")) {
                    child.inside(selector + " #Title").build(commands, events);
                } else if (childParent.startsWith("#")) {
                    // If it starts with #, assume it's a sub-element ID of the container
                    child.inside(selector + " " + childParent).build(commands, events);
                } else {
                    // Fallback
                    child.inside(selector + " " + childParent).build(commands, events);
                }
            }
        }
    }
}
