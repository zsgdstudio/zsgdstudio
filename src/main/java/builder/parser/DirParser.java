package builder.parser;

import builder.helper.PathHelper;
import builder.model.Context;
import builder.model.FLines;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DirParser {
    private FileParser fileParser = new FileParser();

    public void parse(Context context) {
        List<File> srcDirFiles = Arrays.stream(
                Objects.requireNonNullElse(context.getSrcF().listFiles(), new File[] {})
        ).collect(Collectors.toList());

        onFileLike(srcDirFiles, "style.css", context::setCss);
        onFileLike(srcDirFiles, "header", f -> {
            srcDirFiles.remove(f);
            context.setHeaderFl(f);
        });
        onFileLike(srcDirFiles, "navigation", f -> {
            srcDirFiles.remove(f);
            context.setNavigationFl(f);
            context.setNavigation(NavigationParser.parseNavigation(context.getNavigationFl()));
        });
        onFileLike(srcDirFiles, "dir.md", f -> {
            srcDirFiles.remove(f);
            context.setDirName(NavigationParser.parseDirName(new FLines(context, f)));
        });
        onFileLike(srcDirFiles, "index", context::setIndex);
        onFileLike(srcDirFiles, "footer", f -> {
            srcDirFiles.remove(f);
            context.setFooterFl(f);
        });

        if (!context.getTrgF().mkdir()) throw new IllegalStateException("couldn't create dir");

        List<File> files = srcDirFiles.stream().filter(File::isFile).collect(Collectors.toList());
        for (File file : files) {
            fileParser.parse(context, file);
        }


        List<File> dirs = srcDirFiles.stream().filter(File::isDirectory).collect(Collectors.toList());
        for (File subDir : dirs) {
            Path trgPath = PathHelper.reRelateAbsNorm(context, subDir.toPath());
            Context context1 = context.inner();
            context1.setSrcF(subDir);
            context1.setTrgF(trgPath.toFile());
            parse(context1);
        }
    }



    private static void onFileLike(List<File> files, String name, Consumer<File> actions) {
        onFileLike(files, name, actions, null);
    }

    private static void onFileLike(List<File> files, String name, Consumer<File> actions, Runnable orElse) {
        List<File> res = files.stream().filter(f -> f.getName().contains(name)).collect(Collectors.toList());
        if (res.size() > 1) {
            throw new IllegalStateException("more than 1 file like " + name);
        } else if (res.size() == 1) {
            actions.accept(res.get(0));
        } else {
            if (orElse != null) orElse.run();
        }
    }

}
