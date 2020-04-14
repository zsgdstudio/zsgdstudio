package mdToHtml.helper;

import mdToHtml.model.Scope;

import java.io.File;
import java.nio.file.Path;

public class PathHelper {

    public static Path absNorm(Path path) {
        return path.toAbsolutePath().normalize();
    }

    public static Path absNorm(File file) {
        return absNorm(file.toPath());
    }

    public static Path reRelateAbsNorm(Scope scope, Path object) {
        Path from = absNorm(scope.getSrc().toPath());
        Path to = absNorm(scope.getTrg().toPath());
        object = absNorm(object);

        String relS = from.relativize(object).toString().replace(".md", ".html");
        Path rel = Path.of(relS);
        return absNorm(to.resolve(rel));
    }

    public static Path reRelateAbsNorm(Scope scope, File object) {
        return reRelateAbsNorm(scope, object.toPath());
    }

    public static Path reRelate(Scope scope, Path object) {
        return scope.getTrg().toPath().relativize(reRelateAbsNorm(scope, object));
    }

    public static Path reRelate(Scope scope, File object) {
        return reRelate(scope, object.toPath());
    }

    public static Path resolveSiblingAbsNorm(Path relativeTo, Path related) {
        return absNorm( relativeTo.getParent().resolve(related) );
    }
}
