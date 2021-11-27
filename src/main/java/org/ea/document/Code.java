package org.ea.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Code extends Paragraph {
    public static final int CODE_NOT_SET = -1;
    public static final int CODE_TAB = 1;
    public static final int CODE_SPACE = 2;
    public static final int CODE_PREFIX = 3;

    private int codeMode = CODE_NOT_SET;
    public List<String> codeLines = new ArrayList<>();

    public Code(PDDocument doc, String line, JSONObject config) throws IOException {
        super(doc, line, config);
        if (line.startsWith("\t")) {
            codeLines.add("  " + line.substring(1));
            codeMode = CODE_TAB;
        } else if (line.startsWith("    ")) {
            codeLines.add("  " + line.substring(4));
            codeMode = CODE_SPACE;
        } else if (line.equals("```")) {
            codeMode = CODE_PREFIX;
        }

        this.init(doc, config);
    }

    @Override
    public void addContent(String line) {
        if (line.isBlank()) {
        } else if (codeMode == CODE_TAB) {
            line = line.substring(1);
        } else if (codeMode == CODE_SPACE) {
            line = line.substring(4);
        }
        if (codeMode != CODE_PREFIX || !line.equals("```")) {
            codeLines.add("  " + line);
        }
    }

    @Override
    protected void renderContent() throws IOException {
        renderedContentLines = codeLines;
    }

    @Override
    public double calculateHeight() throws IOException {
        renderContent();
        double height = ((renderedContentLines.size() + 0.3f) * fontSize * LINE_SPACING);
        height += 10;
        height += (fontSize / 2) * LINE_SPACING;

        return height;
    }

    @Override
    protected JSONObject getElement(JSONObject elements) {
        return (JSONObject) elements.get("code");
    }

    @Override
    public double render(PDPageContentStream contentStream, double yPos, boolean pageStart) throws IOException {
        contentStream.saveGraphicsState();

        contentStream.setNonStrokingColor(Color.BLACK);

        double yPosStart = yPos;
        yPosStart -= pageStart ? 0 : (fontSize / 2) * LINE_SPACING;

        double yPosEnd = yPos;
        yPosEnd -= pageStart ? 0 : (fontSize / 2) * LINE_SPACING;
        yPosEnd -= fontSize;
        yPosEnd -= fontSize * LINE_SPACING * (renderedContentLines.size() - 1);

        contentStream.moveTo(marginLeft, (float) yPosStart);
        contentStream.lineTo((float)width - marginRight, (float) yPosStart);
        contentStream.lineTo((float)width - marginRight, (float) yPosEnd);
        contentStream.lineTo((float)marginLeft, (float) yPosEnd);
        contentStream.closePath();
        contentStream.fill();

        contentStream.setNonStrokingColor(Color.WHITE);
        contentStream.setStrokingColor(Color.BLACK);

        double yPosRet = super.render(contentStream, yPos - 5, pageStart);

        contentStream.restoreGraphicsState();

        return yPosRet - 5;
    }
}
