package mdToHtml;

import mdToHtml.model.Scope;
import mdToHtml.parser.DirParser;

import java.io.File;

public class Main {
    private static final String SRC = "ru/corebook/";
    private static final String TRG = "site/";

    public static void main(String[] args) {
        Scope scope = new Scope();
        scope.setSrc(new File(SRC));
        scope.setTrg(new File(TRG));
        DirParser.parse(scope, new File(TRG));
    }

}
