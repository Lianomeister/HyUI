package au.ellie.hyui.html.handlers;

import au.ellie.hyui.builders.ProgressBarBuilder;
import au.ellie.hyui.builders.UIElementBuilder;
import au.ellie.hyui.html.HtmlParser;
import au.ellie.hyui.html.TagHandler;
import org.jsoup.nodes.Element;

public class ProgressBarHandler implements TagHandler {
    @Override
    public boolean canHandle(Element element) {
        return element.tagName().equalsIgnoreCase("progress");
    }

    @Override
    public UIElementBuilder<?> handle(Element element, HtmlParser parser) {
        ProgressBarBuilder builder = ProgressBarBuilder.progressBar();

        if (element.hasAttr("value")) {
            try {
                float value = Float.parseFloat(element.attr("value"));
                // Value in a progress bar is between 0.0 and 1.0. 50 on a value attribute for html would be 0.5.
                if (value > 1.0f) {
                    value = value / 100.0f;
                }
                builder.withValue(value);
            } catch (NumberFormatException ignored) {}
        }

        if (element.hasAttr("data-hyui-effect-width")) {
            try {
                builder.withEffectWidth(Integer.parseInt(element.attr("data-hyui-effect-width")));
            } catch (NumberFormatException ignored) {}
        }
        if (element.hasAttr("data-hyui-effect-height")) {
            try {
                builder.withEffectHeight(Integer.parseInt(element.attr("data-hyui-effect-height")));
            } catch (NumberFormatException ignored) {}
        }
        if (element.hasAttr("data-hyui-effect-offset")) {
            try {
                builder.withEffectOffset(Integer.parseInt(element.attr("data-hyui-effect-offset")));
            } catch (NumberFormatException ignored) {}
        }

        if (element.hasAttr("data-hyui-direction")) {
            builder.withDirection(element.attr("data-hyui-direction"));
        }

        if (element.hasAttr("data-hyui-bar-texture-path")) {
            builder.withBarTexturePath(element.attr("data-hyui-bar-texture-path"));
        }
        if (element.hasAttr("data-hyui-effect-texture-path")) {
            builder.withEffectTexturePath(element.attr("data-hyui-effect-texture-path"));
        }

        if (element.hasAttr("data-hyui-alignment")) {
            builder.withAlignment(element.attr("data-hyui-alignment"));
        }

        applyCommonAttributes(builder, element);
        return builder;
    }
}
