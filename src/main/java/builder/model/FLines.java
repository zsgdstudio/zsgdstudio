package builder.model;

import builder.helper.PathHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FLines extends ArrayDeque<String> {
    private Context context;
    private Path absNormPath;

    public FLines() {
        super();
    }

    public FLines(Context context, File file) {
        super();
        this.context = context;
        this.absNormPath = PathHelper.absNorm(file.toPath());
        try {
            if (!file.exists()) return;
            Scanner in = new Scanner(file);
            while (in.hasNextLine()) {
                this.add(in.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Context getContext() {
        return context;
    }

    public Path getAbsNormPath() {
        return absNormPath;
    }

    public List<FLines> split(String byLine) {
        List<FLines> res = new ArrayList<>();
        FLines cur = new FLines();
        for (String line : this) {
            if (line.equals(byLine)) {
                if (!cur.isEmpty()) {
                    res.add(cur);
                }
                cur = new FLines();
            } else {
                cur.add(line);
            }
        }
        if (!cur.isEmpty()) {
            res.add(cur);
        }
        return res;
    }

    public void write() {
        try {
            File f = absNormPath.toFile();
            if (!f.exists() && !f.createNewFile()) throw new IllegalStateException("couldn't create file");
            PrintWriter out = new PrintWriter(f);
            for (String line : this) out.println(line);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
