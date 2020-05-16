package builder.parser;

import builder.helper.PathHelper;
import builder.model.Context;
import builder.model.FLines;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileParser {
    private BodyParser bodyParser = new BodyParser();

    public void parse(Context context, File file) {
        String fileName = file.getName();
        Path trgPath = PathHelper.reRelateAbsNorm(context, file.toPath());

        if (!fileName.endsWith(".md") && !fileName.endsWith(".html")) {
            copy(file.toPath(), trgPath);
            return;
        }
        parse0(context, file, trgPath.toFile());
    }

    private void copy(Path from, Path to) {
        try {
            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parse0(Context context, File src, File trg) {
        FLines in = new FLines(context, src);
        FLines out = new FLines(context, trg);

        out.add("<html>");

        out.add("<head>");
        out.add("<meta charset=\"utf-8\"/>");
        out.add("<meta name=\"viewport\" content=\"width=device-width\">");
        if (context.getCss() != null) {
            out.add("<link rel=\"stylesheet\" href=\"" +
                    PathHelper.reRelate(context, context.getCss().toPath()) +
                    "\"/>");
        }
        out.add("</head>");

        out.add("<body>");

        if (context.getHeaderFl() != null) {
            out.add("<div class=\"header\">");
            out.addAll(bodyParser.parse(context, context.getHeaderFl()));
            out.add("</div>");
        }

        out.add("<div class=\"content\">");

        if (context.getNavigationFl() != null) {
            out.add("<div class=\"sider\">");
            out.addAll(bodyParser.parse(context, context.getNavigationFl()));
            out.add("</div>");
        }

        out.add("<div class=\"center\">");
        FLines horNav = NavigationParser.getHorizontalNavFl(context, in);
        out.addAll(horNav);
        out.addAll(NavigationParser.getDirNavFl(context, in));
        out.addAll(bodyParser.parse(context, in));
        out.addAll(horNav);
        out.add("</div>");

        out.add("</div>");

        if (context.getFooterFl() != null) {
            out.add("<div class=\"footer\">");
            out.addAll(bodyParser.parse(context, context.getFooterFl()));
            out.add("</div>");
        }

        out.add("</body>");

        out.write();
    }

}
