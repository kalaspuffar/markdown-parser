package org.ea;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.ea.document.Code;
import org.ea.document.Page;
import org.ea.document.Paragraph;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ParseMarkdown {
    public static void main(String[] args) throws Exception {
        JSONObject config = (JSONObject) JSONValue.parse(new FileReader("config.json"));

        PDDocument doc = new PDDocument();

        BufferedReader br = new BufferedReader(new FileReader("test.md"));
        String line;

        List<Page> pages = new ArrayList<>();

        Page page = new Page(config);
        doc.addPage(page);
        pages.add(page);

        int codeMode = Code.CODE_NOT_SET;

        Paragraph p = null;
        while ((line = br.readLine()) != null) {
            if (p == null && line.isBlank()) continue;

            if (p == null) {
                if (
                    codeMode == Code.CODE_NOT_SET &&
                    (
                        line.startsWith("\t") ||
                        line.startsWith("    ") ||
                        line.equals("```")
                    )
                ) {
                    if (line.startsWith("\t")) codeMode = Code.CODE_TAB;
                    if (line.startsWith("    ")) codeMode = Code.CODE_SPACE;
                    if (line.equals("```")) codeMode = Code.CODE_PREFIX;
                    p = new Code(doc, line, config);
                } else {
                    p = Paragraph.createParagraph(doc, line, config);
                }
            } else if (line.isBlank()) {
                if (codeMode == Code.CODE_NOT_SET) {
                    if (!page.add(p)) {
                        page = new Page(config);
                        doc.addPage(page);
                        pages.add(page);
                        page.add(p);
                    }
                    p = null;
                } else {
                    p.addContent(line);
                }
            } else {
                if (codeMode != Code.CODE_NOT_SET) {
                    if (codeMode == Code.CODE_TAB && !line.startsWith("\t")) {
                        codeMode = Code.CODE_NOT_SET;
                        if (!page.add(p)) {
                            page = new Page(config);
                            doc.addPage(page);
                            pages.add(page);
                            page.add(p);
                        }
                        p = Paragraph.createParagraph(doc, line, config);
                    } else if (codeMode == Code.CODE_SPACE && !line.startsWith("    ")) {
                        codeMode = Code.CODE_NOT_SET;
                        if (!page.add(p)) {
                            page = new Page(config);
                            doc.addPage(page);
                            pages.add(page);
                            page.add(p);
                        }
                        p = Paragraph.createParagraph(doc, line, config);
                    } else if (codeMode == Code.CODE_PREFIX && line.equals("```")) {
                        codeMode = Code.CODE_NOT_SET;
                        if (!page.add(p)) {
                            page = new Page(config);
                            doc.addPage(page);
                            pages.add(page);
                            page.add(p);
                        }
                        p.addContent("");
                        p = null;
                    }

                    if (p != null) {
                        p.addContent(line);
                    }
                } else {
                    p.addContent(line);
                }
            }
        }

        if (!page.add(p)) {
            page = new Page(config);
            doc.addPage(page);
            pages.add(page);
            page.add(p);
        }

        for (Page renderPage : pages) {
            renderPage.render(doc);
        }

        doc.save(new File("test.pdf"));
    }
}
