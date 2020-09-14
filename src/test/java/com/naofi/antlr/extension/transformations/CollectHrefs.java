package com.naofi.antlr.extension.transformations;

import com.naofi.antlr.extension.annotation.Pre;
import com.naofi.antlr.extension.annotation.Stop;
import com.naofi.antlr.extension.annotation.Transform;
import com.naofi.antlr.extension.generated.HTMLParser;

@Transform("HTML")
public class CollectHrefs {
    private final StringBuilder builder;

    public CollectHrefs(StringBuilder builder) {
        this.builder = builder;
    }

    @Stop
    @Pre(value = "href=$hrefVal", mode = "TAG", rule = HTMLParser.RULE_htmlAttribute)
    public void collectHrefs(HTMLParser.HtmlAttributeValueContext hrefVal) {
        builder.append(hrefVal.getText()).append('\n');
    }
}
