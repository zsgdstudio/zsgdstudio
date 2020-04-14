package mdToHtml.parser;

import mdToHtml.helper.PathHelper;
import mdToHtml.model.FileLines;
import mdToHtml.model.Lines;
import mdToHtml.model.Scope;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileParser {
    private static BodyParser bodyParser = new BodyParser();

    public static void parse(Scope scope, File file) {
        String fileName = file.getName();
        Path trgPath = PathHelper.reRelateAbsNorm(scope, file);

        if (!fileName.endsWith(".md") && !fileName.endsWith(".html")) {
            copy(file.toPath(), trgPath);
            return;
        }
        parse0(scope, file, trgPath);
    }

    private static void copy(Path from, Path to) {
        try {
            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void parse0(Scope scope, File file, Path trgPath) {
        FileLines in = new FileLines(file);
        FileLines out = new FileLines(trgPath);

        out.add("<html>");

        out.add("<head>");
        out.add("<meta charset=\"utf-8\"/>");
        out.add("<meta name=\"viewport\" content=\"width=device-width\">");

        if (scope.getCss() != null) {
            out.add("<link rel=\"stylesheet\" href=\"" +
                    PathHelper.reRelate(scope, scope.getCss()) +
                    "\"/>");
        }
        out.add("</head>");

        out.add("<body>");

        if (scope.getHeaderFl() != null) {
            out.add("<div class=\"header\">");
            out.addAll(bodyParser.parse(scope, scope.getHeaderFl()));
            out.add("</div>");
        }

        out.add("<div class=\"content\">");
        if (scope.getNavigationFl() != null) {
            out.add("<div class=\"sider\">");
            out.addAll(bodyParser.parse(scope, scope.getNavigationFl()));
            out.add("</div>");
        }

        Lines horNav;
        if (scope.getNavigation() != null) {
            horNav = scope.getNavigation().getHorizontalNav(scope, file);
        } else {
            horNav = new Lines();
        }

        out.add("<div class=\"center\">");
        out.addAll(horNav);
        out.addAll(bodyParser.parse(scope, in));
        out.addAll(horNav);
        out.add("</div>");

        out.add("</div>");

        if (scope.getFooterFl() != null) {
            out.add("<div class=\"footer\">");
            out.addAll(bodyParser.parse(scope, scope.getFooterFl()));
            out.add("</div>");
        }

        out.add("</body>");

        out.write();
    }


}
