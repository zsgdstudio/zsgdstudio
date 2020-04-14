package mdToHtml.model;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Lines extends ArrayDeque<String> {

    public Lines() {
        super();
    }

    @Deprecated
    public Lines(File file) throws Exception {
        super();
        Scanner in = new Scanner(file);
        while (in.hasNextLine()) {
            this.add(in.nextLine());
        }
    }

    public List<Lines> split(String byLine) {
        List<Lines> res = new ArrayList<>();
        Lines cur = new Lines();
        for (String line : this) {
            if (line.equals(byLine)) {
                if (!cur.isEmpty()) {
                    res.add(cur);
                }
                cur = new Lines();
            } else {
                cur.add(line);
            }
        }
        if (!cur.isEmpty()) {
            res.add(cur);
        }
        return res;
    }

    @Deprecated
    public void write(File file) throws Exception {
        PrintWriter out = new PrintWriter(file);
        for (String line : this) out.println(line);
        out.flush();
    }

}
