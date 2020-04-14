package mdToHtml.model;

public class TriString {
    private final String pre;
    private final String content;
    private final String post;
    private final boolean ok;

    public TriString() {
        pre = "";
        content = "";
        post = "";
        ok = false;
    }

    public TriString(String pre, String content, String post) {
        this.pre = pre;
        this.content = content;
        this.post = post;
        this.ok = true;
    }

    public String getPre() {
        return pre;
    }

    public String getContent() {
        return content;
    }

    public String getPost() {
        return post;
    }

    public boolean isOk() {
        return ok;
    }



    public static TriString fromToInclusive(String source, String left, String right) {
        if (!source.contains(left) || !source.contains(right)) {
            return new TriString();
        }
        int rightI = source.indexOf(right) + right.length();
        return new TriString(
                source.substring(0, source.indexOf(left)),
                source.substring(source.indexOf(left), rightI),
                source.substring(rightI)
        );
    }

    public static TriString fromToExclusive(String source, String left, String right) {
        if (!source.contains(left) || !source.contains(right)) {
            return new TriString();
        }
        return new TriString(
                source.substring(0, source.indexOf(left)),
                source.substring(source.indexOf(left) + left.length(), source.indexOf(right)),
                source.substring(source.indexOf(right) + right.length())
        );
    }

    public static TriString splitExclusive(String source, String splitBy) {
        if (!source.contains(splitBy)) return new TriString();
        String contPost = source.substring(source.indexOf(splitBy) + splitBy.length());
        if (!contPost.contains(splitBy)) return new TriString();
        return new TriString(
                source.substring(0, source.indexOf(splitBy)),
                contPost.substring(0, contPost.indexOf(splitBy)),
                contPost.substring(contPost.indexOf(splitBy) + splitBy.length())
        );
    }
}
