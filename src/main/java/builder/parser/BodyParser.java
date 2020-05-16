package builder.parser;

import builder.helper.PathHelper;
import builder.model.Context;
import builder.model.FLines;
import builder.model.TString;

import java.nio.file.Path;

public class BodyParser {
    private Context context;
    private FLines src;
    private StringBuilder curStr = new StringBuilder();
    private FLines res;

    public FLines parse(Context context, FLines fl) {
        this.context = context;
        this.src = fl;
        res = new FLines();
        curStr = new StringBuilder();
        for (FLines lines : fl.split("")) {
            block(lines);
        }
        return res;
    }

    /**
     * blocks
     * */

    private void block(FLines blockLines) {
        if (blockLines.isEmpty()) return;
        String line = blockLines.peek();
        if (line.startsWith(">")) {
            example(blockLines);
        } else if (line.contains("|")) {
            table(blockLines);
        } else {
            justBlock(blockLines);
        }
    }

    private void justBlock(FLines blockLines) {
        textLn("<div class=\"block\">");
        String line;
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

    private void example(FLines blockLines) {
        FLines newBlockLines = new FLines();
        String line;
        while (!blockLines.isEmpty()) {
            line = blockLines.remove();
            if (line.startsWith(">")) newBlockLines.add(line.substring(1));
            else newBlockLines.add(line);
        }

        textLn("<div class=\"example\">");
        for (FLines lines : newBlockLines.split("")) {
            block(lines);
        }
        textLn("</div>");
    }

    private void list(FLines blockLines) {
        boolean inItem = false;
        String line;
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

    private void table(FLines blockLines) {
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
        while (line.charAt(headerInt) == '#') {
            headerInt++;
        }
        text("<h" + headerInt + ">");
        line(line.substring(headerInt).trim());
        text("</h" + headerInt + ">");
    }

    private void img(String line) {
        TString.splitInc(line, "![", ")").then((pre, content, post) -> {
            line(pre);

            String aText = TString.splitExc(content, "![", "]").getContent();
            String src = TString.splitExc(content, "(", ")").getContent();
            src = relateLink(src);
            text( "<div class=\"image\"><a href=\"" + src + "\" target=_blank> <img src=\"" + src + "\" alt=\"" + aText + "\"/></a></div>");

            line(post);
        });
    }

    private void link(String line) {
        TString.splitInc(line, "[", ")").then((pre, content, post) -> {
            line(pre);

            String aText = TString.splitExc(content, "[", "]").getContent();
            String href = TString.splitExc(content, "(", ")").getContent();
            href = relateLink(href);
            text("<a href=\"" + href + "\">" + aText + "</a>");

            line(post);
        });
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
        TString.splitExc(line, splitBy).ifOk((pre, content, post) -> {
            line(pre);
            text(surroundLeft);
            line(content);
            text(surroundRight);
            line(post);
        }).ifNotOk(() -> text(line));
    }

    /**
     * primitive
     * */

    private String relateLink(String link) {
        if (link.contains("http")) return link;
        Path relSrcPath;
        try {
            relSrcPath = Path.of(link);
        } catch (Exception e) {
            return link;
        }
        return PathHelper.reRelate(context, PathHelper.resolve(src.getContext(), relSrcPath)).toString();
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
