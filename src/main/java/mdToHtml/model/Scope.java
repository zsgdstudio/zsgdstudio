package mdToHtml.model;

import java.io.File;

public class Scope {
    private Scope outer;

    private File src;
    private File trg;

    private File css;
    private FileLines headerFl;
    private FileLines navigationFl;
    private Navigation navigation;
    private FileLines footerFl;

    //scope

    public Scope() {
    }

    private Scope(Scope outer) {
        this.outer = outer;
    }

    public Scope outer() {
        if (outer == null) throw new IllegalStateException("no outer scope");
        return outer;
    }

    public Scope inner() {
        return new Scope(this);
    }

    //get set

    public File getSrc() {
        if (src == null && outer != null) return outer.getSrc();
        return src;
    }

    public void setSrc(File src) {
        this.src = src;
    }

    public File getTrg() {
        if (trg == null && outer != null) return outer.getTrg();
        return trg;
    }

    public void setTrg(File trg) {
        this.trg = trg;
    }

    public File getCss() {
        if (css == null && outer != null) return outer.getCss();
        return css;
    }

    public void setCss(File css) {
        this.css = css;
    }

    public FileLines getHeaderFl() {
        if (headerFl == null && outer != null) return outer.getHeaderFl();
        return headerFl;
    }

    public void setHeaderFl(FileLines headerFl) {
        this.headerFl = headerFl;
    }

    public FileLines getNavigationFl() {
        if (navigationFl == null && outer != null) return outer.getNavigationFl();
        return navigationFl;
    }

    public void setNavigationFl(FileLines navigationFl) {
        this.navigationFl = navigationFl;
    }

    public Navigation getNavigation() {
        if (navigation == null && outer != null) return outer.getNavigation();
        return navigation;
    }

    public void setNavigation(Navigation navigation) {
        this.navigation = navigation;
    }

    public FileLines getFooterFl() {
        if (footerFl == null && outer != null) return outer.getFooterFl();
        return footerFl;
    }

    public void setFooterFl(FileLines footerFl) {
        this.footerFl = footerFl;
    }
}
