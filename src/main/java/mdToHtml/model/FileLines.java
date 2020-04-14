package mdToHtml.model;

import mdToHtml.helper.PathHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Scanner;

public class FileLines extends Lines {
    private Path path;

    public FileLines(Path path) {
        super();
        this.path = path;
    }

    public FileLines(File file) {
        super();
        path = PathHelper.absNorm(file.toPath());
        try {
            Scanner in = new Scanner(file);
            while (in.hasNextLine()) {
                this.add(in.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void write() {
        try {
            File f = new File(path.toUri());
            if (!f.exists()) f.createNewFile();
            PrintWriter out = new PrintWriter(f);
            for (String line : this) out.println(line);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
