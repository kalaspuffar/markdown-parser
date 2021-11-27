package org.ea.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.simple.JSONObject;

import java.io.IOException;

public class Heading extends Paragraph {
    private int level = 0;

    public Heading(PDDocument doc, String line, JSONObject config) throws IOException {
        super(doc, line, config);
        if (line.contains(" ")) {
            int headEnd = line.indexOf("# ");
            String lvlStr = line.substring(0, headEnd + 1);
            level = lvlStr.trim().length();
            content = line.substring(headEnd + 2);
        }
    }

    @Override
    protected JSONObject getElement(JSONObject elements) {
        switch (level) {
            case 1:
                return (JSONObject) elements.get("header1");
            case 2:
                return (JSONObject) elements.get("header2");
            case 3:
                return (JSONObject) elements.get("header3");
            case 4:
                return (JSONObject) elements.get("header4");
            case 5:
                return (JSONObject) elements.get("header5");
            case 6:
                return (JSONObject) elements.get("header6");
        }

        return (JSONObject) elements.get("paragraph");
    }

    public static boolean is(String line) {
        return line.trim().startsWith("#");
    }
}
