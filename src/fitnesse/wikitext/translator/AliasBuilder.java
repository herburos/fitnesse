package fitnesse.wikitext.translator;

import fitnesse.html.HtmlTag;
import fitnesse.wikitext.parser.Parser;
import fitnesse.wikitext.parser.ParsingPage;
import fitnesse.wikitext.parser.Symbol;
import fitnesse.wikitext.parser.SymbolType;
import fitnesse.wikitext.Utils;

public class AliasBuilder implements Translation {

    public String toHtml(Translator translator, Symbol symbol) {
        if (symbol.childAt(0).childAt(0).getType() == SymbolType.WikiWord) return translator.translate(symbol.childAt(0));
        
        String linkBody = translator.translate(symbol.childAt(0));
        String linkReferenceString = Utils.unescapeHTML(translator.translate(symbol.childAt(1)));
        Symbol linkReference = Parser.make(new ParsingPage(translator.getPage()), linkReferenceString).parse();

        if (linkReference.childAt(0).getType() == SymbolType.WikiWord) {
            return new WikiWordBuilder().buildLink(
                    translator.getPage(),
                    linkReference.childAt(0).getContent(),
                    translator.translate(linkReference.childrenAfter(0)),
                    linkBody,
                    linkBody
                    );
        }

        if (linkReference.childAt(0).getType() == SymbolType.Link) {
            return new LinkBuilder().buildLink(translator, linkBody, linkReference.childAt(0));
        }

        HtmlTag alias = new HtmlTag("a", linkBody);
        alias.addAttribute("href", translator.translate(linkReference));
        return alias.htmlInline();
    }
}
