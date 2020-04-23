package mdToHtml;

import mdToHtml.model.Scope;
import mdToHtml.parser.DirParser;

import java.io.File;

public class Main {
    private static final String SRC = "src/main/site/";
    private static final String TRG = "out/";

    public static void main(String[] args) {
        Scope scope = new Scope();
        scope.setSrc(new File(SRC));
        scope.setTrg(new File(TRG));
        File dir = new File(SRC);
        if (dir.exists()) {
            dir.delete();
        }
        DirParser.parse(scope, dir);
    }

}
