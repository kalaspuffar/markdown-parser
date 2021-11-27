package org.ea.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberedList extends Paragraph {
    private static final Pattern numberStartPattern = Pattern.compile("^\\s*[\\d+]\\.");
    private final Pattern numberPattern = Pattern.compile("[\\d+]\\.");

    Map<Integer, Integer> depthMap = new HashMap<>();
    public List<String> listLines = new ArrayList<>();

    public NumberedList(PDDocument doc, String line, JSONObject config) {
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
            Matcher m = numberPattern.matcher(s);
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

    @Override
    public double render(PDPageContentStream contentStream, double yPos, boolean pageStart) throws IOException {
        contentStream.setFont(font, (float)fontSize);

        if (!pageStart) {
            yPos -= (fontSize / 2) * LINE_SPACING;
        }

        int currentNum = 1;
        int lastDepth = 0;

        boolean first = true;
        for (String s : listLines) {
            yPos -= first ? fontSize : fontSize * LINE_SPACING;
            first = false;

            Matcher m = numberPattern.matcher(s);

            if(m.find()) {
                int depthPos = m.start();
                int depth = depthMap.get(depthPos);

                contentStream.beginText();
                contentStream.newLineAtOffset(
                        (float) (marginLeft + depth),
                        (float) yPos
                );
                contentStream.showText(currentNum + ". " + s.substring(depthPos + 2).trim());
                contentStream.endText();
                lastDepth = depth;
                currentNum++;
            } else {
                float prefixWidth = font.getStringWidth(currentNum + ". ");
                contentStream.beginText();
                contentStream.newLineAtOffset(
                    (float) (marginLeft + lastDepth + prefixWidth),
                    (float) yPos
                );
                contentStream.showText(s.trim());
                contentStream.endText();
            }
        }

        return yPos;
    }

    public static boolean is(String line) {
        Matcher m = numberStartPattern.matcher(line);
        return m.find();
    }
}
