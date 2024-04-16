import me.bush.translator.Language;
import me.bush.translator.Translation;
import me.bush.translator.Translator;

public class Translate {
    public static String translator(String input) {
        Translator translator = new Translator();
        //input text, target language, source language
        Translation tr = translator.translateBlocking(input, Language.ENGLISH, Language.AUTO);
        return tr.getTranslatedText();
    }
}
