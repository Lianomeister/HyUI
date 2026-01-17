package au.ellie.hyui.html.handlers;

import au.ellie.hyui.builders.LabelBuilder;
import au.ellie.hyui.builders.UIElementBuilder;
import au.ellie.hyui.html.HtmlParser;
import au.ellie.hyui.html.TagHandler;
import org.jsoup.nodes.Element;

public class LabelHandler implements TagHandler {
    @Override
    public boolean canHandle(Element element) {
        String tag = element.tagName().toLowerCase();
        return tag.equals("label") || tag.equals("p");
    }

    @Override
    public UIElementBuilder<?> handle(Element element, HtmlParser parser) {
        LabelBuilder builder = LabelBuilder.label().withText(element.text());
        applyCommonAttributes(builder, element);
        return builder;
    }
}
