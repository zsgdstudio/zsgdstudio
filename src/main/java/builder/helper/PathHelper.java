package builder.helper;

import builder.model.Context;

import java.nio.file.Path;

public class PathHelper {

    public static Path absNorm(Path path) {
        return path.toAbsolutePath().normalize();
    }

    public static Path resolve(Context context, Path path) {
        return context.getSrcF().toPath().resolve(path);
    }

    public static Path reRelateAbsNorm(Context context, Path path) {
        Path anFrom = absNorm(context.getSrcF().toPath());
        Path anTo = absNorm(context.getTrgF().toPath());
        Path anFromPath = absNorm(path);

        String relS = anFrom.relativize(anFromPath).toString().replace(".md", ".html");
        return absNorm(anTo.resolve(Path.of(relS)));
    }

    public static Path reRelate(Context context, Path path) {
        return absNorm(context.getTrgF().toPath()).relativize(reRelateAbsNorm(context, path));
    }

}
