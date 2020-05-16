package builder.parser;

import builder.helper.PathHelper;
import builder.model.Context;
import builder.model.FLines;
import builder.model.Navigation;
import builder.model.TString;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class NavigationParser {

    public static Navigation parseNavigation(FLines navigationFl) {
        Navigation res = new Navigation();
        for (String line : navigationFl) {
            if (!line.contains("![") && line.contains("[")) {
                String mdLink = TString.splitInc(line, "[", ")").getContent();
                String link = TString.splitExc(mdLink, "(", ")").getContent();
                Path absLinkPath = PathHelper.absNorm( navigationFl.getContext().getSrcF().toPath().resolve(Path.of(link)) );
                res.add(absLinkPath);
            }
        }
        return res;
    }

    public static String parseDirName(FLines dirNameFl) {
        return dirNameFl.getFirst();
    }

    public static FLines getHorizontalNavFl(Context context, FLines srcFl) {
        Navigation navigation = context.getNavigation();
        if (navigation == null) return new FLines();

        Path srcPath = srcFl.getAbsNormPath();
        if (!navigation.contains(srcPath)) return new FLines();

        int i = navigation.indexOf(srcPath);

        FLines res = new FLines();
        res.add("<div class=\"horizontal_nav\">");
        if (i > 0) {
            Path prevPath = PathHelper.reRelate(context, navigation.get(i - 1));
            res.add("<a href=\"" + prevPath + "\" class=\"prev\">< Назад</a>");
        }
        if (i < navigation.size() - 1) {
            Path nextPath = PathHelper.reRelate(context, navigation.get(i + 1));
            res.add("<a href=\"" + nextPath + "\" class=\"next\">Далее ></a>");
        }
        res.add("</div>");

        return res;
    }

    public static FLines getDirNavFl(Context context, FLines fLines) {
        List<String> names = new ArrayList<>();
        List<File> indexes = new ArrayList<>();

        Context cur = context;
        while (cur.getDirName() != null) {
            names.add(cur.getDirName());
            if (cur.getIndex() != null) indexes.add(cur.getIndex());
            else indexes.add(null);
            if (cur.outer() == null) break;
            cur = cur.outer();
        }

        if (names.isEmpty()) return new FLines();

        String fileName = fLines.getAbsNormPath().toFile().getName();
        if (fileName.equals("index.md") || fileName.equals("index.html")) {
            names.remove(0);
            indexes.remove(0);
        }

        if (names.isEmpty()) return new FLines();

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < names.size(); i++) {
            int j = names.size() - 1 - i;
            File ind = indexes.get(j);
            String link;
            if (ind != null) {
                link = "<a href=\"" +
                        PathHelper.reRelate(context, PathHelper.absNorm(ind.toPath())).toString()
                        + "\">" + names.get(j) + "</a>";
            } else {
                link = names.get(j);
            }
            s.append(link).append(" > ");
        }

        FLines res = new FLines();
        res.add("<div class=\"block\">");
        res.add(s.toString());
        res.add("</div>");

        return res;
    }

}
