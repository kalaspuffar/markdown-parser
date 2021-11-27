package org.ea.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Paragraph {
    public static final double LINE_SPACING = 1.5f;

    protected double width;
    protected long marginLeft;
    protected long marginRight;
    protected PDType0Font font;
    protected double fontSize;
    protected String content;
    protected List<String> renderedContentLines = null;

    public Paragraph(PDDocument doc, String line, JSONObject config) {
        content = line;
        JSONObject pageSize = (JSONObject) config.get("page_size");
        width = (Double)pageSize.get("width");

        JSONObject margins = (JSONObject) config.get("margin");
        marginLeft = (Long)margins.get("left");
        marginRight = (Long)margins.get("right");
    }

    public void init(PDDocument doc, JSONObject config) throws IOException {
        JSONObject element = this.getElement((JSONObject)config.get("elements"));
        String fontFile = (String)element.get("font_file");
        fontSize = (double)element.get("font_size");
        font = PDType0Font.load(doc, new File(fontFile));
    }

    protected JSONObject getElement(JSONObject elements) {
        return (JSONObject) elements.get("paragraph");
    }

    public static Paragraph createParagraph(PDDocument doc, String line, JSONObject config) throws IOException {
        Paragraph p = null;
        if (Heading.is(line)) {
            p = new Heading(doc, line, config);
        } else if (BulletedList.is(line)) {
            p = new BulletedList(doc, line, config);
        } else if (NumberedList.is(line)) {
            p = new NumberedList(doc, line, config);
        } else if (HorizontalRuler.is(line)) {
            p = new HorizontalRuler(doc, line, config);
        } else {
            p = new Paragraph(doc, line, config);
        }
        p.init(doc, config);
        return p;
    }

    protected double widthOfText(String text) throws IOException {
        return fontSize * font.getStringWidth(text) / 1000.0;
    }

    protected void renderContent() throws IOException {
        renderedContentLines = new ArrayList<>();
        double availableSpace = width - marginRight - marginLeft;
        String renderedContent = "";
        for (String s : content.split(" ")) {
            double renderedSize = widthOfText(renderedContent + " " + s);
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
        return (renderedContentLines.size() + 0.3f) * fontSize * LINE_SPACING;
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

    public double render(PDPageContentStream contentStream, double yPos, boolean pageStart) throws IOException {
        contentStream.setFont(font, (float)fontSize);

        if (!pageStart) {
            yPos -= (fontSize / 2) * LINE_SPACING;
        }

        boolean first = true;
        for (String s : renderedContentLines) {
            yPos -= first ? fontSize : fontSize * LINE_SPACING;
            first = false;
            contentStream.beginText();
            contentStream.newLineAtOffset(marginLeft, (float) yPos);
            contentStream.showText(s);
            contentStream.endText();
        }

        return yPos;
    }
}
