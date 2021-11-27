package org.ea.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.json.simple.JSONObject;

import java.io.IOException;

public class HorizontalRuler extends Paragraph {
    public HorizontalRuler(PDDocument doc, String line, JSONObject config) {
        super(doc, line, config);
    }

    public static boolean is(String line) {
        return line.trim().equals("----");
    }

    @Override
    public double render(PDPageContentStream contentStream, double yPos, boolean pageStart) throws IOException {

        contentStream.moveTo(marginLeft, (float)yPos - 10);
        contentStream.lineTo((float) width - marginRight, (float)yPos - 10);
        contentStream.closePath();
        contentStream.stroke();

        yPos -= 20;
        return yPos;
    }
}
