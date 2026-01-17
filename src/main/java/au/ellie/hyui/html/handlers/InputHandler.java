package au.ellie.hyui.html.handlers;

import au.ellie.hyui.builders.*;
import au.ellie.hyui.html.HtmlParser;
import au.ellie.hyui.html.TagHandler;
import org.jsoup.nodes.Element;

public class InputHandler implements TagHandler {
    @Override
    public boolean canHandle(Element element) {
        return element.tagName().equalsIgnoreCase("input");
    }

    @Override
    public UIElementBuilder<?> handle(Element element, HtmlParser parser) {
        String type = element.attr("type").toLowerCase();
        UIElementBuilder<?> builder = null;

        switch (type) {
            case "text":
                builder = TextFieldBuilder.textInput();
                if (element.hasAttr("value")) {
                    ((TextFieldBuilder) builder).withValue(element.attr("value"));
                }
                break;
            case "number":
                builder = NumberFieldBuilder.numberInput();
                if (element.hasAttr("value")) {
                    try {
                        ((NumberFieldBuilder) builder).withValue(Double.parseDouble(element.attr("value")));
                    } catch (NumberFormatException ignored) {}
                }
                break;
            case "range":
                builder = SliderBuilder.gameSlider();
                if (element.hasAttr("value")) {
                    try {
                        ((SliderBuilder) builder).withValue(Integer.parseInt(element.attr("value")));
                    } catch (NumberFormatException ignored) {}
                }
                if (element.hasAttr("min")) {
                    try {
                        ((SliderBuilder) builder).withMin(Integer.parseInt(element.attr("min")));
                    } catch (NumberFormatException ignored) {}
                }
                if (element.hasAttr("max")) {
                    try {
                        ((SliderBuilder) builder).withMax(Integer.parseInt(element.attr("max")));
                    } catch (NumberFormatException ignored) {}
                }
                if (element.hasAttr("step")) {
                    try {
                        ((SliderBuilder) builder).withStep(Integer.parseInt(element.attr("step")));
                    } catch (NumberFormatException ignored) {}
                }

                // Support data-hyui-* attributes for slider as well
                if (element.hasAttr("data-hyui-min")) {
                    try {
                        ((SliderBuilder) builder).withMin(Integer.parseInt(element.attr("data-hyui-min")));
                    } catch (NumberFormatException ignored) {}
                }
                if (element.hasAttr("data-hyui-max")) {
                    try {
                        ((SliderBuilder) builder).withMax(Integer.parseInt(element.attr("data-hyui-max")));
                    } catch (NumberFormatException ignored) {}
                }
                if (element.hasAttr("data-hyui-step")) {
                    try {
                        ((SliderBuilder) builder).withStep(Integer.parseInt(element.attr("data-hyui-step")));
                    } catch (NumberFormatException ignored) {}
                }
                break;
            case "checkbox":
                builder = new CheckBoxBuilder();
                if (element.hasAttr("checked")) {
                    ((CheckBoxBuilder) builder).withValue(true);
                } else if (element.hasAttr("value")) {
                    ((CheckBoxBuilder) builder).withValue(Boolean.parseBoolean(element.attr("value")));
                }
                break;
            case "color":
                builder = new ColorPickerBuilder();
                if (element.hasAttr("value")) {
                    ((ColorPickerBuilder) builder).withValue(element.attr("value"));
                }
                break;
            case "submit":
            case "reset":
                return new ButtonHandler().handle(element, parser);
        }

        if (builder != null) {
            applyCommonAttributes(builder, element);
        }

        return builder;
    }
}
