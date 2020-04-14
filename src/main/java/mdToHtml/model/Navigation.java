package mdToHtml.model;

import mdToHtml.helper.PathHelper;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

public class Navigation extends ArrayList<Path> {

    public Lines getHorizontalNav(Scope scope, File srcFile) {
        Path srcFilePath = PathHelper.absNorm(srcFile);
        if (!this.contains(srcFilePath)) return new Lines();

        int i = this.indexOf(srcFilePath);

        Lines res = new Lines();
        res.add("<div class=\"horizontal_nav\">");
        if (i > 0) {
            Path prevPath = PathHelper.reRelate(scope, get(i - 1));
            res.add("<a href=\"" + prevPath + "\" class=\"prev\">< Назад</a>");
        }
        if (i < size() - 1) {
            Path nextPath = PathHelper.reRelate(scope, get(i + 1));
            res.add("<a href=\"" + nextPath + "\" class=\"next\">Далее ></a>");
        }
        res.add("</div>");

        return res;
    }

}
