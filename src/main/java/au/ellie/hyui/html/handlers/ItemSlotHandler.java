package au.ellie.hyui.html.handlers;

import au.ellie.hyui.builders.ItemSlotBuilder;
import au.ellie.hyui.builders.UIElementBuilder;
import au.ellie.hyui.html.HtmlParser;
import au.ellie.hyui.html.TagHandler;
import org.jsoup.nodes.Element;

public class ItemSlotHandler implements TagHandler {
    @Override
    public boolean canHandle(Element element) {
        return element.tagName().equalsIgnoreCase("span") && element.hasClass("item-slot");
    }

    @Override
    public UIElementBuilder<?> handle(Element element, HtmlParser parser) {
        ItemSlotBuilder builder = ItemSlotBuilder.itemSlot();

        if (element.hasAttr("data-hyui-item-id")) {
            builder.withItemId(element.attr("data-hyui-item-id"));
        } else if (element.hasAttr("src")) {
            builder.withItemId(element.attr("src"));
        }

        if (element.hasAttr("data-hyui-show-quality-background")) {
            builder.withShowQualityBackground(Boolean.parseBoolean(
                    element.attr("data-hyui-show-quality-background")));
        }
        if (element.hasAttr("data-hyui-show-quantity")) {
            builder.withShowQuantity(Boolean.parseBoolean(
                    element.attr("data-hyui-show-quantity")));
        }

        applyCommonAttributes(builder, element);
        return builder;
    }
}
