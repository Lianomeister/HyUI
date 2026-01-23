package au.ellie.hyui.html;

import au.ellie.hyui.HyUIPlugin;
import au.ellie.hyui.builders.*;
import au.ellie.hyui.elements.BackgroundSupported;
import au.ellie.hyui.elements.LayoutModeSupported;
import au.ellie.hyui.utils.StyleUtils;
import com.hypixel.hytale.server.core.Message;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * Interface for handling a specific HTML tag and converting it to a HyUI builder.
 */
public interface TagHandler {
    /**
     * Checks if this handler can handle the given element.
     *
     * @param element The Jsoup element to check.
     * @return true if this handler can process the element, false otherwise.
     */
    boolean canHandle(Element element);

    /**
     * Handles the conversion of the HTML element to a UIElementBuilder.
     *
     * @param element The Jsoup element to convert.
     * @param parser  The parser instance for recursive calls if needed.
     * @return A UIElementBuilder representing the element, or null if it should be ignored.
     */
    UIElementBuilder<?> handle(Element element, HtmlParser parser);

    /**
     * Applies common attributes like id, style, data-*, etc. to the builder.
     *
     * @param builder The builder to apply attributes to.
     * @param element The HTML element containing the attributes.
     */
    default void applyCommonAttributes(UIElementBuilder<?> builder, Element element) {
        if (element.hasAttr("id")) {
            builder.withId(element.attr("id"));
        }

        if (element.hasAttr("data-hyui-tooltiptext")) {
            builder.withTooltipTextSpan(Message.raw(element.attr("data-hyui-tooltiptext")));
        }

        if (element.hasAttr("data-hyui-flexweight")) {
            try {
                builder.withFlexWeight(Integer.parseInt(element.attr("data-hyui-flexweight")));
            } catch (NumberFormatException ignored) {}
        }

        if (element.hasAttr("style")) {
            Map<String, String> styles = parseStyleAttribute(element.attr("style"));
            applyStyles(builder, styles);
        }

        if (element.hasAttr("data-hyui-hover-style")) {
            Map<String, String> hoverStyles = parseStyleAttribute(element.attr("data-hyui-hover-style"));
            ParsedStyles parsed = getStylesAnchorsPadding(hoverStyles, builder);
            if (parsed.hasStyle) {
                HyUIStyle currentStyle = builder.getHyUIStyle();
                if (currentStyle == null) {
                    currentStyle = new HyUIStyle();
                }
                HyUIPlugin.getLog().logInfo("Applying hover style: " + parsed.style.toString());
                HyUIPlugin.getLog().logInfo("Applying style: " + currentStyle.toString());
                builder.withStyle(currentStyle.setHoverStyle(parsed.style));
            }
        }

        if (element.tagName().equalsIgnoreCase("img")) {
            HyUIAnchor anchor = builder.getAnchor();
            if (anchor == null) {
                anchor = new HyUIAnchor();
            }
            boolean hasImgAttr = false;
            if (element.hasAttr("width")) {
                try {
                    anchor.setWidth(Integer.parseInt(element.attr("width")));
                    hasImgAttr = true;
                } catch (NumberFormatException ignored) {}
            }
            if (element.hasAttr("height")) {
                try {
                    anchor.setHeight(Integer.parseInt(element.attr("height")));
                    hasImgAttr = true;
                } catch (NumberFormatException ignored) {}
            }
            if (hasImgAttr) {
                builder.withAnchor(anchor);
            }
        }
    }

    private Map<String, String> parseStyleAttribute(String styleAttr) {
        Map<String, String> styles = new HashMap<>();
        String[] declarations = styleAttr.split(";");
        for (String declaration : declarations) {
            String[] parts = declaration.split(":", 2);
            if (parts.length == 2) {
                styles.put(parts[0].trim().toLowerCase(), parts[1].trim());
            }
        }
        return styles;
    }

    private void applyStyles(UIElementBuilder<?> builder, Map<String, String> styles) {
        ParsedStyles parsed = getStylesAnchorsPadding(styles, builder);
        if (parsed.hasStyle) {
            builder.withStyle(parsed.style);
        }
        if (parsed.hasAnchor) {
            builder.withAnchor(parsed.anchor);
        }
        if (parsed.hasPadding) {
            builder.withPadding(parsed.padding);
        }
    }

    class ParsedStyles {
        HyUIStyle style = new HyUIStyle();
        HyUIAnchor anchor = new HyUIAnchor();
        HyUIPadding padding = new HyUIPadding();
        boolean hasStyle = false;
        boolean hasAnchor = false;
        boolean hasPadding = false;
    }

    private ParsedStyles getStylesAnchorsPadding(Map<String, String> styles, UIElementBuilder<?> builder) {
        ParsedStyles parsed = new ParsedStyles();

        for (Map.Entry<String, String> entry : styles.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            switch (key) {
                case "color":
                    parsed.style.setTextColor(value);
                    parsed.hasStyle = true;
                    break;
                case "font-size":
                    parsed.style.setFontSize(value);
                    parsed.hasStyle = true;
                    break;
                case "font-weight":
                    if (value.equalsIgnoreCase("bold")) {
                        parsed.style.setRenderBold(true);
                        parsed.hasStyle = true;
                    }
                    break;
                case "font-style":
                    if (value.equalsIgnoreCase("italic")) {
                        parsed.style.setRenderItalics(true);
                        parsed.hasStyle = true;
                    } else if (value.equalsIgnoreCase("normal")) {
                        parsed.style.setRenderItalics(false);
                        parsed.hasStyle = true;
                    }
                    break;
                case "text-transform":
                    if (value.equalsIgnoreCase("uppercase")) {
                        parsed.style.setRenderUppercase(true);
                        parsed.hasStyle = true;
                    }
                    break;
                case "letter-spacing":
                    try {
                        parsed.style.setLetterSpacing(Integer.parseInt(value));
                        parsed.hasStyle = true;
                    } catch (NumberFormatException ignored) {}
                    break;
                case "white-space":
                    if (value.equalsIgnoreCase("nowrap")) {
                        parsed.style.setWrap(false);
                        parsed.hasStyle = true;
                    } else if (value.equalsIgnoreCase("normal") || value.equalsIgnoreCase("wrap")) {
                        parsed.style.setWrap(true);
                        parsed.hasStyle = true;
                    }
                    break;
                case "font-family":
                case "font-name":
                    parsed.style.setFontName(value);
                    parsed.hasStyle = true;
                    break;
                case "outline-color":
                case "text-outline-color":
                    parsed.style.setOutlineColor(value);
                    parsed.hasStyle = true;
                    break;
                case "text-align":
                case "layout-mode":
                case "layout":
                    if (builder instanceof LayoutModeSupported) {
                        ((LayoutModeSupported<?>) builder).withLayoutMode(normalizeLayoutMode(value));
                    }
                    break;
                case "vertical-align":
                    parsed.style.setVerticalAlignment(capitalize(value));
                    parsed.hasStyle = true;
                    break;
                case "horizontal-align":
                    parsed.style.setHorizontalAlignment(capitalize(value));
                    parsed.hasStyle = true;
                    break;
                case "align":
                    parsed.style.setAlignment(capitalize(value));
                    parsed.hasStyle = true;
                    break;
                case "visibility":
                    if (value.equalsIgnoreCase("hidden")) {
                        builder.withVisible(false);
                    } else if (value.equalsIgnoreCase("shown")) {
                        builder.withVisible(true);
                    }
                    break;
                case "display":
                    if (value.equalsIgnoreCase("none")) {
                        builder.withVisible(false);
                    } else if (value.equalsIgnoreCase("block")) {
                        builder.withVisible(true);
                    }
                    break;
                case "flex-weight":
                    try {
                        builder.withFlexWeight(Integer.parseInt(value));
                    } catch (NumberFormatException ignored) {}
                    break;
                case "anchor-left":
                    try {
                        parsed.anchor.setLeft(Integer.parseInt(value));
                        parsed.hasAnchor = true;
                    } catch (NumberFormatException ignored) {}
                    break;
                case "anchor-right":
                    try {
                        parsed.anchor.setRight(Integer.parseInt(value));
                        parsed.hasAnchor = true;
                    } catch (NumberFormatException ignored) {}
                    break;
                case "anchor-top":
                    try {
                        parsed.anchor.setTop(Integer.parseInt(value));
                        parsed.hasAnchor = true;
                    } catch (NumberFormatException ignored) {}
                    break;
                case "anchor-bottom":
                    try {
                        parsed.anchor.setBottom(Integer.parseInt(value));
                        parsed.hasAnchor = true;
                    } catch (NumberFormatException ignored) {}
                    break;
                case "anchor-width":
                    try {
                        parsed.anchor.setWidth(Integer.parseInt(value));
                        parsed.hasAnchor = true;
                    } catch (NumberFormatException ignored) {}
                    break;
                case "anchor-height":
                    try {
                        parsed.anchor.setHeight(Integer.parseInt(value));
                        parsed.hasAnchor = true;
                    } catch (NumberFormatException ignored) {}
                    break;
                case "anchor-full", "anchor":
                    try {
                        parsed.anchor.setFull(Integer.parseInt(value));
                        parsed.hasAnchor = true;
                    } catch (NumberFormatException ignored) {}
                    break;
                case "anchor-horizontal":
                    try {
                        parsed.anchor.setHorizontal(Integer.parseInt(value));
                        parsed.hasAnchor = true;
                    } catch (NumberFormatException ignored) {}
                    break;
                case "anchor-vertical":
                    try {
                        parsed.anchor.setVertical(Integer.parseInt(value));
                        parsed.hasAnchor = true;
                    } catch (NumberFormatException ignored) {}
                    break;
                case "anchor-min-width":
                    try {
                        parsed.anchor.setMinWidth(Integer.parseInt(value));
                        parsed.hasAnchor = true;
                    } catch (NumberFormatException ignored) {}
                    break;
                case "anchor-max-width":
                    try {
                        parsed.anchor.setMaxWidth(Integer.parseInt(value));
                        parsed.hasAnchor = true;
                    } catch (NumberFormatException ignored) {}
                    break;
                case "padding-left":
                    try {
                        parsed.padding.setLeft(Integer.parseInt(value));
                        parsed.hasPadding = true;
                    } catch (NumberFormatException ignored) {}
                    break;
                case "padding-right":
                    try {
                        parsed.padding.setRight(Integer.parseInt(value));
                        parsed.hasPadding = true;
                    } catch (NumberFormatException ignored) {}
                    break;
                case "padding-top":
                    try {
                        parsed.padding.setTop(Integer.parseInt(value));
                        parsed.hasPadding = true;
                    } catch (NumberFormatException ignored) {}
                    break;
                case "padding-bottom":
                    try {
                        parsed.padding.setBottom(Integer.parseInt(value));
                        parsed.hasPadding = true;
                    } catch (NumberFormatException ignored) {}
                    break;
                case "padding":
                    String[] values = value.split("\\s+");
                    try {
                        if (values.length == 1) {
                            parsed.padding.setFull(Integer.parseInt(values[0]));
                            parsed.hasPadding = true;
                        } else if (values.length >= 2) {
                            int v = Integer.parseInt(values[0]);
                            int h = Integer.parseInt(values[1]);
                            parsed.padding.setSymmetric(v, h);
                            parsed.hasPadding = true;
                        }
                    } catch (NumberFormatException ignored) {}
                    break;
                case "background-image":
                    if (builder instanceof BackgroundSupported) {
                        StyleUtils.BackgroundParts parts = StyleUtils.parseBackgroundParts(value, true);
                        String realUrl = parts.value();
                        HyUIPatchStyle background = ((BackgroundSupported<?>) builder).getBackground();
                        if (background == null) {
                            HyUIPatchStyle bg = new HyUIPatchStyle().setTexturePath(realUrl);
                            StyleUtils.applyBorders(bg, parts);
                            ((BackgroundSupported<?>) builder).withBackground(bg);
                        } else {
                            background.setTexturePath(realUrl);
                            StyleUtils.applyBorders(background, parts);
                        }
                    }
                    break;
                case "background-color":
                    if (builder instanceof BackgroundSupported) {
                        StyleUtils.BackgroundParts parts = StyleUtils.parseBackgroundParts(value, false);
                        String normalizedColor = StyleUtils.normalizeBackgroundColor(parts.value());
                        HyUIPatchStyle background = ((BackgroundSupported<?>) builder).getBackground();
                        if (background == null) {
                            HyUIPatchStyle bg = new HyUIPatchStyle().setColor(normalizedColor);
                            StyleUtils.applyBorders(bg, parts);
                            ((BackgroundSupported<?>) builder).withBackground(bg);
                        } else {
                            background.setColor(normalizedColor);
                            StyleUtils.applyBorders(background, parts);
                        }
                    }
                    break;
                case "hyui-style-reference":
                    String[] styleParts = value.split("\\s+");
                    if (styleParts.length == 1) {
                        parsed.style.withStyleReference(styleParts[0].replace("\"", "").replace("'", ""));
                        parsed.hasStyle = true;
                    } else if (styleParts.length >= 2) {
                        parsed.style.withStyleReference(
                                styleParts[0].replace("\"", "").replace("'", ""),
                                styleParts[1].replace("\"", "").replace("'", "")
                        );
                        parsed.hasStyle = true;
                    }
                    break;
                case "hyui-entry-label-style":
                    builder.withSecondaryStyle("EntryLabelStyle", parseStyleReference(value));
                    break;
                case "hyui-selected-entry-label-style":
                    builder.withSecondaryStyle("SelectedEntryLabelStyle", parseStyleReference(value));
                    break;
                case "hyui-popup-style":
                    builder.withSecondaryStyle("PopupStyle", parseStyleReference(value));
                    break;
                case "hyui-number-field-style":
                    builder.withSecondaryStyle("NumberFieldStyle", parseStyleReference(value));
                    break;
                case "hyui-checked-style":
                    builder.withSecondaryStyle("CheckedStyle", parseStyleReference(value));
                    break;
                case "hyui-unchecked-style":
                    builder.withSecondaryStyle("UncheckedStyle", parseStyleReference(value));
                    break;
            }
        }
        return parsed;
    }

    private HyUIStyle parseStyleReference(String value) {
        HyUIStyle style = new HyUIStyle();
        String[] parts = value.split("\\s+");
        if (parts.length == 1) {
            style.withStyleReference(parts[0].replace("\"", "").replace("'", ""));
        } else if (parts.length >= 2) {
            style.withStyleReference(
                    parts[0].replace("\"", "").replace("'", ""),
                    parts[1].replace("\"", "").replace("'", "")
            );
        }
        return style;
    }

    private String normalizeLayoutMode(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        String normalized = value.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        for (LayoutModeSupported.LayoutMode mode : LayoutModeSupported.LayoutMode.values()) {
            String modeNormalized = mode.name().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            if (modeNormalized.equals(normalized)) {
                return mode.name();
            }
        }
        return capitalize(value);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
