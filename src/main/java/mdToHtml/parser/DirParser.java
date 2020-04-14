package mdToHtml.parser;

import mdToHtml.helper.PathHelper;
import mdToHtml.model.FileLines;
import mdToHtml.model.Scope;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class DirParser {
    private static BodyParser bodyParser = new BodyParser();

    public static void parse(Scope scope, File dir) {
        Path trgPath = PathHelper.reRelateAbsNorm(scope, dir);
        scope = scope.inner();
        scope.setSrc(dir);
        scope.setTrg(new File(trgPath.toUri()));

        List<File> srcDirFiles = new ArrayList<>(Arrays.asList(dir.listFiles()));

        File fl = null;
        fl = findSingleFile(srcDirFiles, "style.css");
        if (fl != null) scope.setCss(fl);
        fl = findSingleFile(srcDirFiles, "header");
        if (fl != null) {
            srcDirFiles.remove(fl);
            scope.setHeaderFl(new FileLines(fl));
        }
        fl = findSingleFile(srcDirFiles, "navigation");
        if (fl != null) {
            srcDirFiles.remove(fl);
            scope.setNavigationFl(new FileLines(fl));
            scope.setNavigation(bodyParser.parseNavigation(scope));
        }
        fl = findSingleFile(srcDirFiles, "footer");
        if (fl != null) {
            srcDirFiles.remove(fl);
            scope.setFooterFl(new FileLines(fl));
        }

        scope.getTrg().mkdir();

        List<File> files = srcDirFiles.stream().filter(File::isFile).collect(Collectors.toList());
        for (File file : files) {
            FileParser.parse(scope, file);
        }

        List<File> dirs = srcDirFiles.stream().filter(File::isDirectory).collect(Collectors.toList());
        for (File subDir : dirs) {
            DirParser.parse(scope, subDir);
        }
    }

    private static File findSingleFile(List<File> files, String name) {
        List<File> res = files.stream().filter(f -> f.getName().contains(name)).collect(Collectors.toList());
        if (res.size() > 1) {
            throw new IllegalStateException("more than 1 file like " + name);
        } else if (res.size() == 1) {
            return res.get(0);
        } else {
            return null;
        }
    }

}
