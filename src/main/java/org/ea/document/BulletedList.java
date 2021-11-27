package org.ea.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BulletedList extends Paragraph {
    private static final Pattern bulletStartPattern = Pattern.compile("^\\s*[*\\-+] ");
    private final Pattern bulletPattern = Pattern.compile("[*\\-+] ");

    Map<Integer, Integer> depthMap = new HashMap<>();
    public List<String> listLines = new ArrayList<>();

    public BulletedList(PDDocument doc, String line, JSONObject config) {
        super(doc, line, config);
        listLines.add(line);
    }

    @Override
    public void addContent(String line) {
        listLines.add(line);
    }

    @Override
    protected void renderContent() throws IOException {
        findDepths();
        renderedContentLines = listLines;
    }

    private void findDepths() {
        for (String s : listLines) {
            Matcher m = bulletPattern.matcher(s);
            if (m.find()) {
                depthMap.put(m.start(), 0);
            }
        }

        List<Integer> availableVal = new ArrayList<>(depthMap.keySet());

        Collections.sort(availableVal);

        int depthPoints = 0;
        for (Integer i : availableVal) {
            depthMap.put(i, depthPoints);
            depthPoints += fontSize;
        }
    }

    private void drawCircle(PDPageContentStream contentStream, float cx, float cy, float r) throws IOException {
        final float k = 0.552284749831f;
        contentStream.setNonStrokingColor(Color.BLACK);
        contentStream.moveTo(cx - r, cy);
        contentStream.curveTo(cx - r, cy + k * r, cx - k * r, cy + r, cx, cy + r);
        contentStream.curveTo(cx + k * r, cy + r, cx + r, cy + k * r, cx + r, cy);
        contentStream.curveTo(cx + r, cy - k * r, cx + k * r, cy - r, cx, cy - r);
        contentStream.curveTo(cx - k * r, cy - r, cx - r, cy - k * r, cx - r, cy);
        contentStream.fill();
    }

    @Override
    public double render(PDPageContentStream contentStream, double yPos, boolean pageStart) throws IOException {
        contentStream.setFont(font, (float)fontSize);

        if (!pageStart) {
            yPos -= (fontSize / 2) * LINE_SPACING;
        }

        int lastDepth = 0;

        boolean first = true;
        for (String s : listLines) {
            yPos -= first ? fontSize : fontSize * LINE_SPACING;
            first = false;

            Matcher m = bulletPattern.matcher(s);

            if(m.find()) {
                int depthPos = m.start();
                int depth = depthMap.get(depthPos);

                drawCircle(
                    contentStream,
                    (float) (marginLeft + depth + fontSize),
                    (float) (yPos + fontSize / 4),
                    (float) (fontSize / 5)
                );

                contentStream.beginText();
                contentStream.newLineAtOffset(
                        (float) (marginLeft + depth + (fontSize * 1.6f)),
                        (float) yPos
                );
                contentStream.showText(s.substring(depthPos + 1).trim());
                contentStream.endText();
                lastDepth = depth;
            } else {
                contentStream.beginText();
                contentStream.newLineAtOffset(
                    (float) (marginLeft + lastDepth + (fontSize * 1.6f)),
                    (float) yPos
                );
                contentStream.showText(s.trim());
                contentStream.endText();
            }
        }

        return yPos;
    }

    public static boolean is(String line) {
        Matcher m = bulletStartPattern.matcher(line);
        return m.find();
    }
}
