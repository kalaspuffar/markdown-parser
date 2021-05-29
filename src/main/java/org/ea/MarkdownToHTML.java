package org.ea;

public class MarkdownToHTML {
    public static void main(String[] args) {

    }

    public String convertMarkdown(String markdown) {
        StringBuilder sb = new StringBuilder();
        boolean codeMode = false;
        for (String s : markdown.split("\n")) {
            if (s.startsWith("```")) {
                sb.append(codeMode ? "</code>" : "<code>");
                codeMode = !codeMode;
            } else if (codeMode) {
                sb.append(s.replaceFirst("\\s+$", ""));
            } else if (s.startsWith("###")) {
                sb.append("<h3>");
                sb.append(s.substring(3).trim());
                sb.append("</h3>");
            } else if (s.startsWith("##")) {
                sb.append("<h2>");
                sb.append(s.substring(2).trim());
                sb.append("</h2>");
            } else if (s.startsWith("#")) {
                sb.append("<h1>");
                sb.append(s.substring(1).trim());
                sb.append("</h1>");
            } else {
                sb.append("<p>");
                sb.append(s.trim());
                sb.append("</p>");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
