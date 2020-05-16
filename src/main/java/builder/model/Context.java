package builder.model;

import java.io.File;

public class Context {
    private Context outer;

    private File srcF;
    private File trgF;

    private File css;
    private FLines headerFl;
    private FLines navigationFl;
    private Navigation navigation;
    private String dirName;
    private File index;
    private FLines footerFl;

    //constructor

    public Context() {}

    public Context outer() {
        if (outer == null) throw new IllegalStateException("no outer context");
        return outer;
    }

    public Context inner() {
        Context inner = new Context();
        inner.outer = this;
        return inner;
    }

    //get set


    public File getSrcF() {
        if (srcF == null && outer != null) return outer.getSrcF();
        return srcF;
    }

    public void setSrcF(File srcF) {
        this.srcF = srcF;
    }

    public File getTrgF() {
        if (trgF == null && outer != null) return outer.getTrgF();
        return trgF;
    }

    public void setTrgF(File trgF) {
        this.trgF = trgF;
    }

    public File getCss() {
        if (css == null && outer != null) return outer.getCss();
        return css;
    }

    public void setCss(File css) {
        this.css = css;
    }

    public FLines getHeaderFl() {
        if (headerFl == null && outer != null) return outer.getHeaderFl();
        return headerFl;
    }

    public void setHeaderFl(File file) {
        this.headerFl = new FLines(this, file);
    }

    public FLines getNavigationFl() {
        if (navigationFl == null && outer != null) return outer.getNavigationFl();
        return navigationFl;
    }

    public void setNavigationFl(File file) {
        this.navigationFl = new FLines(this, file);
    }

    public Navigation getNavigation() {
        if (navigation == null && outer != null) return outer.getNavigation();
        return navigation;
    }

    public void setNavigation(Navigation navigation) {
        this.navigation = navigation;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public File getIndex() {
        return index;
    }

    public void setIndex(File index) {
        this.index = index;
    }

    public FLines getFooterFl() {
        if (footerFl == null && outer != null) return outer.getFooterFl();
        return footerFl;
    }

    public void setFooterFl(File file) {
        this.footerFl = new FLines(this, file);
    }
}
