package mdToHtml.parser;

import mdToHtml.helper.PathHelper;
import mdToHtml.model.*;

import java.nio.file.Path;

public class BodyParser {
    private Scope scope;
    private StringBuilder curStr = new StringBuilder();
    private FileLines res;


    public FileLines parse(Scope scope, FileLines fl) {
        this.scope = scope;
        res = new FileLines(fl.getPath());
        curStr = new StringBuilder();

        for (Lines lines : fl.split("")) {
            block(lines);
        }

        return res;
    }

    public Navigation parseNavigation(Scope scope) {
        Navigation res = new Navigation();
        for (String line : scope.getNavigationFl()) {
            if (!line.contains("![") && line.contains("[")) {
                String mdLink = TriString.fromToInclusive(line, "[", ")").getContent();
                String link = TriString.fromToExclusive(mdLink, "(", ")").getContent();
                Path absLinkPath = PathHelper.resolveSiblingAbsNorm(scope.getNavigationFl().getPath(), Path.of(link));
                res.add(absLinkPath);
            }
        }
        return res;
    }

    /**
     * blocks
     * */

    private void block(Lines blockLines) {
        String line = blockLines.peek();
        if (line.startsWith(">")) {
            example(blockLines);
        } else if (line.contains("|")) {
            table(blockLines);
        } else {
            justBlock(blockLines);
        }
    }

    private void justBlock(Lines blockLines) {
        textLn("<div class=\"block\">");
        String line = "";
        while (!blockLines.isEmpty()) {
            line = blockLines.remove();
            if (line.startsWith("-")) {
                blockLines.addFirst(line);
                list(blockLines);
                break;
            } else {
                line(line);
                text(" ");
            }
        }
        textLn("</div>");
    }

    private void example(Lines blockLines) {
        Lines newBlockLines = new Lines();
        String line = "";
        while (!blockLines.isEmpty()) {
            line = blockLines.remove();
            if (line.startsWith(">")) newBlockLines.add(line.substring(1));
            else newBlockLines.add(line);
        }

        textLn("<div class=\"example\">");
        for (Lines lines : newBlockLines.split("")) {
            block(lines);
        }
        textLn("</div>");
    }

    private void list(Lines blockLines) {
        boolean inItem = false;
        String line = "";
        textLn("<ul>");
        while (!blockLines.isEmpty()) {
            line = blockLines.remove();
            if (line.startsWith("-")) {
                if (inItem) textLn("</li>");
                text("<li>");
                inItem = true;
                line = line.substring(1).trim();
            }
            line(line);
        }
        if (inItem) {
            text("</li>");
        }
        textLn("</ul>");
    }

    private void table(Lines blockLines) {
        textLn("<table>");
        tableRow(blockLines.remove());
        blockLines.remove();
        while (!blockLines.isEmpty()) {
            tableRow(blockLines.remove());
        }
        textLn("</table>");
    }

    private void tableRow(String line) {
        line = " " + line + " ";
        String[] cells = line.split("\\|");
        text("<tr>");
        for (String cell : cells) {
            text("<td>");
            line(cell.trim());
            text("</td>");
        }
        textLn("</tr>");
    }

    /**
     * line
     * */

    private void line(String line) {
        if (line.startsWith("#")) {
            header(line);
        } else {
            if (line.contains("![")) {
                img(line);
            } else if (line.contains("[")) {
                link(line);
            } else if (line.contains("__")) {
                bold(line);
            } else if (line.contains("~~")) {
                strike(line);
            } else if (line.contains("_")) {
                italic(line);
            } else if (line.contains("`")) {
                code(line);
            } else {
                text(line);
            }
        }
    }

    private void header(String line) {
        int headerInt = 0;
        for (; line.charAt(headerInt) == '#'; headerInt++) {}
        text("<h" + headerInt + ">");
        line(line.substring(headerInt).trim());
        text("</h" + headerInt + ">");
    }

    private void img(String line) {
        TriString ts = TriString.fromToInclusive(line, "![", ")");
        line(ts.getPre());

        String aText = TriString.fromToExclusive(ts.getContent(), "![", "]").getContent();
        String src = TriString.fromToExclusive(ts.getContent(), "(", ")").getContent();
        src = relateLink(src);
        text( "<div class=\"image\"><a href=\"" + src + "\" target=_blank> <img src=\"" + src + "\" alt=\"" + aText + "\"/></a></div>");

        line(ts.getPost());
    }

    private void link(String line) {
        TriString ts = TriString.fromToInclusive(line, "[", ")");
        line(ts.getPre());

        String aText = TriString.fromToExclusive(ts.getContent(), "[", "]").getContent();
        String href = TriString.fromToExclusive(ts.getContent(), "(", ")").getContent();
        href = relateLink(href);
        text("<a href=\"" + href + "\">" + aText + "</a>");

        line(ts.getPost());
    }

    private void bold(String line) {
        splitAndSurround(line, "__", "<b>", "</b>");
    }

    private void strike(String line) {
        splitAndSurround(line, "~~", "<s>", "</s>");
    }

    private void italic(String line) {
        splitAndSurround(line, "_", "<i>", "</i>");
    }

    private void code(String line) {
        splitAndSurround(line, "`", "<span class=\"code\">", "</span>");
    }

    private void splitAndSurround(String line, String splitBy, String surroundLeft, String surroundRight) {
        TriString ts = TriString.splitExclusive(line, splitBy);
        if (!ts.isOk()) text(line);
        else {
            line(ts.getPre());
            text(surroundLeft);
            line(ts.getContent());
            text(surroundRight);
            line(ts.getPost());
        }
    }

    /**
     * primitive
     * */

    private String relateLink(String link) {
        if (link.contains("http")) return link;
        Path relSrcPath = Path.of(link);
        Path absSrcPath = PathHelper.resolveSiblingAbsNorm(res.getPath(), relSrcPath);
        Path relTrgPath = PathHelper.reRelate(scope, absSrcPath);

        return relTrgPath.toString();
    }

    private void text(String line) {
        curStr.append(line);
    }

    private void textLn(String line) {
        curStr.append(line);
        res.add(curStr.toString());
        curStr = new StringBuilder();
    }

}
