package builder;

import builder.model.Context;
import builder.parser.DirParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class Main {

    public static void main(String[] args) {
        String src, trg;
        if (args.length != 2) {
            throw new IllegalArgumentException("2 agrs required: SRC and TRG directories");
        } else {
            src = args[0];
            trg = args[1];
        }
        File srcF = new File(src);
        File trgF = new File(trg);

        Context context = new Context();
        context.setSrcF(srcF);
        context.setTrgF(trgF);

        if (!removeDir(trgF)) {
            System.out.println("fail during target directory removal");
            return;
        }

        new DirParser().parse(context);
    }

    private static boolean removeDir(File file) {
        try {
            if (file.exists()) {
                boolean failed = Files.walk(file.toPath())
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .anyMatch(f -> !f.delete());
                return !failed;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
