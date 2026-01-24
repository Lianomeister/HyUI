package au.ellie.hyui.html.handlers;

import au.ellie.hyui.builders.DynamicImageBuilder;
import au.ellie.hyui.builders.ImageBuilder;
import au.ellie.hyui.builders.UIElementBuilder;
import au.ellie.hyui.html.HtmlParser;
import au.ellie.hyui.html.TagHandler;
import org.jsoup.nodes.Element;

public class ImgHandler implements TagHandler {
    @Override
    public boolean canHandle(Element element) {
        return element.tagName().equalsIgnoreCase("img");
    }

    @Override
    public UIElementBuilder<?> handle(Element element, HtmlParser parser) {
        UIElementBuilder<?> builder;

        boolean isDynamicImage = element.hasAttr("class") && element.attr("class").contains("dynamic-image");
        if (isDynamicImage) {
            DynamicImageBuilder dynamicImage = DynamicImageBuilder.dynamicImage();
            if (element.hasAttr("src")) {
                dynamicImage.withImageUrl(element.attr("src"));
            }
            builder = dynamicImage;
        } else {
            ImageBuilder image = ImageBuilder.image();
            if (element.hasAttr("src")) {
                image.withImage(element.attr("src"));
            }
            builder = image;
        }
        
        applyCommonAttributes(builder, element);
        
        return builder;
    }
}
