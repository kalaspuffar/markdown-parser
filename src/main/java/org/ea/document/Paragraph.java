package org.ea.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Paragraph {
    public static final double LINE_SPACING = 1.2f;

    protected long width;
    protected long marginLeft;
    protected long marginRight;
    protected long textHeight;
    protected PDType0Font font;
    protected double fontSize;
    private String content;
    private List<String> renderedContentLines = new ArrayList<>();

    public Paragraph(PDDocument doc, String line, JSONObject config) throws IOException {
        content = line;
        JSONObject pageSize = (JSONObject) config.get("page_size");
        width = (Long)pageSize.get("width");

        JSONObject margins = (JSONObject) config.get("margin");
        marginLeft = (Long)margins.get("left");
        marginRight = (Long)margins.get("right");

        JSONObject element = this.getElement((JSONObject)config.get("elements"));
        String fontFile = (String)element.get("font_file");
        fontSize = (double)element.get("font_size");
        font = PDType0Font.load(doc, new File(fontFile));
    }

    protected JSONObject getElement(JSONObject elements) {
        return (JSONObject) elements.get("paragraph");
    }

    public static Paragraph createParagraph(PDDocument doc, String line, JSONObject config) throws IOException {
        return new Paragraph(doc, line, config);
    }

    protected double widthOfText(String text) throws IOException {
        return fontSize * font.getStringWidth(text) / 1000.0;
    }

    protected void renderContent() throws IOException {
        double availableSpace = width - marginRight - marginLeft;
        String renderedContent = "";
        for (String s : content.split(" ")) {
            double renderedSize = widthOfText(renderedContent);
            if (renderedSize > availableSpace) {
                renderedContentLines.add(renderedContent);
                renderedContent = "";
            }
            if(!renderedContent.isBlank()) {
                renderedContent += " ";
            }
            renderedContent += s;
        }

        if(!renderedContent.isBlank()) {
            renderedContentLines.add(renderedContent);
        }
    }

    public double calculateHeight() throws IOException {
        renderContent();
        return renderedContentLines.size() * fontSize * LINE_SPACING;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String s : renderedContentLines) {
            if (!first) sb.append("\n");
            sb.append(s);
            first = false;
        }
        return sb.toString();
    }

    public void addContent(String line) {
        content += " " + line;
    }
}
