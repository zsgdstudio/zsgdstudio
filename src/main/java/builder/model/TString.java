package builder.model;

public class TString {
    private final String pre;
    private final String content;
    private final String post;
    private final boolean ok;

    private TString() {
        pre = "";
        content = "";
        post = "";
        ok = false;
    }

    private TString(String pre, String content, String post) {
        this.pre = pre;
        this.content = content;
        this.post = post;
        this.ok = true;
    }

    public String getPre() {
        if (!isOk()) throw new IllegalStateException("can't get pre-string from non-ok tri string");
        return pre;
    }

    public String getContent() {
        if (!isOk()) throw new IllegalStateException("can't get content from non-ok tri string");
        return content;
    }

    public String getPost() {
        if (!isOk()) throw new IllegalStateException("can't get post-string from non-ok tri string");
        return post;
    }

    public boolean isOk() {
        return ok;
    }

    public TString ifOk(TriConsumer<String> actions) {
        if (isOk()) actions.accept(pre, content, post);
        return this;
    }

    public TString ifNotOk(Runnable actions) {
        if (!isOk()) actions.run();
        return this;
    }

    public TString then(TriConsumer<String> actions) {
        if (!isOk()) throw new IllegalStateException("can't perform actions with non-ok tri string");
        actions.accept(pre, content, post);
        return this;
    }

    // fabric methods

    public static TString splitInc(String source, String left, String right) {
        if (!source.contains(left) || !source.contains(right)) {
            return new TString();
        }
        int rightI = source.indexOf(right) + right.length();
        return new TString(
                source.substring(0, source.indexOf(left)),
                source.substring(source.indexOf(left), rightI),
                source.substring(rightI)
        );
    }

    public static TString splitExc(String source, String left, String right) {
        if (!source.contains(left) || !source.contains(right)) {
            return new TString();
        }
        return new TString(
                source.substring(0, source.indexOf(left)),
                source.substring(source.indexOf(left) + left.length(), source.indexOf(right)),
                source.substring(source.indexOf(right) + right.length())
        );
    }

    public static TString splitExc(String source, String splitBy) {
        if (!source.contains(splitBy)) return new TString();
        String contPost = source.substring(source.indexOf(splitBy) + splitBy.length());
        if (!contPost.contains(splitBy)) return new TString();
        return new TString(
                source.substring(0, source.indexOf(splitBy)),
                contPost.substring(0, contPost.indexOf(splitBy)),
                contPost.substring(contPost.indexOf(splitBy) + splitBy.length())
        );
    }

    // functional interface

    public interface TriConsumer<T> {
        void accept(T t, T u, T v);
    }

}
