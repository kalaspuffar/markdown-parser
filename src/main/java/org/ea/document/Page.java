package org.ea.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Page extends PDPage {
    private JSONObject config;
    private double heightLeft;
    private long marginTop;
    private List<Paragraph> paragraphs = new ArrayList<>();

    public Page(JSONObject config) {
        super();
        this.config = config;
        heightLeft = this.getMediaBox().getHeight();
        JSONObject margins = (JSONObject) config.get("margin");
        marginTop = (Long)margins.get("top");
        heightLeft -= (marginTop + (Long)margins.get("bottom"));

        JSONObject pageSize = (JSONObject) config.get("page_size");

        PDRectangle rect = new PDRectangle(
            ((Double)pageSize.get("width")).floatValue(),
            ((Double)pageSize.get("height")).floatValue()
        );

        this.setMediaBox(rect);
        this.setCropBox(rect);
        this.setTrimBox(rect);
    }

    public boolean add(Paragraph p) throws Exception {
        double height = p.calculateHeight();

        if (heightLeft < height) {
            return false;
        } else {
            paragraphs.add(p);
            heightLeft -= height;
        }
        return true;
    }

    public void render(PDDocument doc) throws Exception {
        double yPos = this.getMediaBox().getHeight();
        yPos -= marginTop;

        PDPageContentStream contentStream = new PDPageContentStream(
            doc,
            this,
            PDPageContentStream.AppendMode.APPEND,
            true,
            true
        );

        contentStream.saveGraphicsState();
        contentStream.setStrokingColor(Color.BLACK);

        boolean pageStart = true;
        for (Paragraph p : paragraphs) {
            yPos = p.render(contentStream, yPos, pageStart);
            pageStart = false;
        }

        contentStream.restoreGraphicsState();

        contentStream.close();
    }
}
