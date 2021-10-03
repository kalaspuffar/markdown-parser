package org.ea;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontFactory;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.ea.document.Paragraph;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ParseMarkdown {
    public static void main(String[] args) throws IOException {
        JSONObject config = (JSONObject) JSONValue.parse(new FileReader("config.json"));

        PDDocument doc = new PDDocument();

        List<Paragraph> paragraphList = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader("test.md"));
        String line;

        Paragraph p = null;
        while ((line = br.readLine()) != null) {
            if (p == null) {
                p = Paragraph.createParagraph(doc, line, config);
            } else if (line.isBlank()) {
                p.calculateHeight();
                paragraphList.add(p);
                p = null;
            } else {
                p.addContent(line);
            }
        }

        p.calculateHeight();
        paragraphList.add(p);

        for (Paragraph p2 : paragraphList) {
            System.out.println(p2);
        }
    }
}
