package au.ellie.hyui.builders;

import au.ellie.hyui.HyUIPlugin;
import au.ellie.hyui.elements.BackgroundSupported;
import au.ellie.hyui.elements.LayoutModeSupported;
import au.ellie.hyui.elements.UIElements;
import au.ellie.hyui.events.UIContext;
import au.ellie.hyui.theme.Theme;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for creating tab navigation UI elements.
 * Creates a horizontal row of tab buttons for navigation between different content sections.
 *
 * Example usage:
 * TabNavigationBuilder.tabNavigation()
 *     .withId("main-tabs")
 *     .addTab("inventory", "Inventory")
 *     .addTab("stats", "Statistics", "stats-content")
 *     .addTab("settings", "Settings")
 *     .withSelectedTab("inventory")
 */
public class TabNavigationBuilder extends UIElementBuilder<TabNavigationBuilder>
        implements LayoutModeSupported<TabNavigationBuilder>, BackgroundSupported<TabNavigationBuilder> {

    public record Tab(String id, String label, String contentId, boolean selected) {
        public Tab withSelected(boolean selected) {
            return new Tab(id, label, contentId, selected);
        }

        public Tab withContentId(String contentId) {
            return new Tab(id, label, contentId, selected);
        }
    }

    private final List<Tab> tabs = new ArrayList<>();
    private String selectedTabId;
    private String layoutMode = "Left";
    private HyUIPatchStyle background;
    private HyUIStyle selectedTabStyle;
    private HyUIStyle unselectedTabStyle;
    private int tabSpacing = 0;

    public TabNavigationBuilder() {
        super(UIElements.GROUP, "Group");
    }

    public TabNavigationBuilder(Theme theme) {
        super(theme, UIElements.GROUP, "Group");
    }

    /**
     * Factory method to create a new TabNavigationBuilder.
     */
    public static TabNavigationBuilder tabNavigation() {
        return new TabNavigationBuilder();
    }

    /**
     * Adds a tab to the navigation.
     *
     * @param id    Unique identifier for the tab (used in events)
     * @param label Display text for the tab button
     */
    public TabNavigationBuilder addTab(String id, String label) {
        tabs.add(new Tab(id, label, null, false));
        return this;
    }

    /**
     * Adds a tab to the navigation with a linked content element ID.
     *
     * @param id        Unique identifier for the tab (used in events)
     * @param label     Display text for the tab button
     * @param contentId ID of the content element to show when selected
     */
    public TabNavigationBuilder addTab(String id, String label, String contentId) {
        tabs.add(new Tab(id, label, contentId, false));
        return this;
    }

    /**
     * Sets which tab is currently selected.
     */
    public TabNavigationBuilder withSelectedTab(String tabId) {
        this.selectedTabId = tabId;
        return this;
    }

    /**
     * Sets the style for selected tabs.
     */
    public TabNavigationBuilder withSelectedTabStyle(HyUIStyle style) {
        this.selectedTabStyle = style;
        return this;
    }

    /**
     * Sets the style for unselected tabs.
     */
    public TabNavigationBuilder withUnselectedTabStyle(HyUIStyle style) {
        this.unselectedTabStyle = style;
        return this;
    }

    /**
     * Sets spacing between tabs (in pixels).
     */
    public TabNavigationBuilder withTabSpacing(int spacing) {
        this.tabSpacing = spacing;
        return this;
    }

    /**
     * Gets all tabs.
     */
    public List<Tab> getTabs() {
        return new ArrayList<>(tabs);
    }

    public boolean hasTab(String tabId) {
        if (tabId == null) {
            return false;
        }
        for (Tab tab : tabs) {
            if (tab.id().equals(tabId)) {
                return true;
            }
        }
        return false;
    }

    public void linkTabContent(String tabId, String contentId) {
        if (tabId == null || contentId == null) {
            return;
        }
        for (int i = 0; i < tabs.size(); i++) {
            Tab tab = tabs.get(i);
            if (tab.id().equals(tabId)) {
                if (tab.contentId() == null || tab.contentId().isBlank()) {
                    tabs.set(i, tab.withContentId(contentId));
                }
                return;
            }
        }
    }

    /**
     * Gets the currently selected tab ID.
     */
    public String getSelectedTabId() {
        return selectedTabId;
    }

    @Override
    public TabNavigationBuilder withLayoutMode(String layoutMode) {
        this.layoutMode = layoutMode;
        return this;
    }

    @Override
    public String getLayoutMode() {
        return this.layoutMode;
    }

    @Override
    public TabNavigationBuilder withBackground(HyUIPatchStyle background) {
        this.background = background;
        return this;
    }

    @Override
    public HyUIPatchStyle getBackground() {
        return this.background;
    }

    @Override
    protected boolean supportsStyling() {
        return false;
    }

    private boolean tabButtonsCreated = false;

    @Override
    protected void onBuild(UICommandBuilder commands, UIEventBuilder events) {
        String selector = getSelector();
        if (selector == null) return;

        applyLayoutMode(commands, selector);
        applyBackground(commands, selector);

        if (selectedTabId == null && !tabs.isEmpty()) {
            selectedTabId = tabs.get(0).id();
        }

        // Only create tab buttons once, we're dealing with builders here, not raw set commands.
        if (tabButtonsCreated) return;
        tabButtonsCreated = true;

        // Create tab buttons as children
        for (int i = 0; i < tabs.size(); i++) {
            Tab tab = tabs.get(i);
            boolean isSelected = tab.id().equals(selectedTabId);

            ButtonBuilder tabButton = ButtonBuilder.secondaryTextButton()
                    .withId(tab.id())
                    .withText(tab.label())
                    .addEventListener(CustomUIEventBindingType.Activating, (_, ctx) -> {
                        applyTabSelection(ctx, tab.id());
                        ctx.updatePage(true);
                    })
                    .withFlexWeight(1);

            // Apply selected/unselected styling
            if (isSelected) {
                tabButton.withStyle(selectedTabStyle != null ? selectedTabStyle : defaultSelectedStyle());
            } else {
                tabButton.withStyle(unselectedTabStyle != null ? unselectedTabStyle : defaultUnselectedStyle());
            }

            this.addChild(tabButton);

            HyUIPlugin.getLog().logInfo("Added tab: " + tab.id() + " (selected: " + isSelected + ")");
        }
    }

    private void applyTabSelection(UIContext ctx, String tabId) {
        this.selectedTabId = tabId;
        for (Tab tab : tabs) {
            boolean isSelected = tab.id().equals(tabId);
            ctx.getById(tab.id(), ButtonBuilder.class).ifPresent(button -> {
                HyUIStyle style = isSelected
                        ? selectedTabStyle != null ? selectedTabStyle : defaultSelectedStyle()
                        : unselectedTabStyle != null ? unselectedTabStyle : defaultUnselectedStyle();
                button.withStyle(style);
            });
            String contentId = tab.contentId();
            if (contentId != null && !contentId.isBlank()) {
                ctx.getById(contentId, TabContentBuilder.class).ifPresent(content -> content.withVisible(isSelected));
            }
        }
    }

    /**
     * Creates a default style for selected tabs (primary button)
     */
    public static HyUIStyle defaultSelectedStyle() {
        return new HyUIStyle()
                .withStyleReference("Common.ui", "DefaultTextButtonStyle");
    }

    /**
     * Creates a default style for unselected tabs (secondary button).
     */
    public static HyUIStyle defaultUnselectedStyle() {
        return new HyUIStyle()
                .withStyleReference("Common.ui", "SecondaryTextButtonStyle");
    }
}
