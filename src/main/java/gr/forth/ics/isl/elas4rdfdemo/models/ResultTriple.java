package gr.forth.ics.isl.elas4rdfdemo.models;

public class ResultTriple {
    public String subject;
    public String predicate;
    public String object;
    public String objExt;
    public String subExt;
    public String subHighlight;
    public String objHighlight;
    public String preHighlight;

    public String getPreHighlight() {
        return preHighlight;
    }

    public void setPreHighlight(String preHighlight) {
        this.preHighlight = preHighlight;
    }

    public String getSubHighlight() {
        return subHighlight;
    }

    public void setSubHighlight(String subHighlight) {
        this.subHighlight = subHighlight;
    }

    public String getObjHighlight() {
        return objHighlight;
    }

    public void setObjHighlight(String objHighlight) {
        this.objHighlight = objHighlight;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getObjExt() {
        return objExt;
    }

    public void setObjExt(String objExt) {
        this.objExt = objExt;
    }

    public String getSubExt() {
        return subExt;
    }

    public void setSubExt(String subExt) {
        this.subExt = subExt;
    }

    public String uriToString(String uri) {
        String clean = "";
        if (uri.startsWith("http")) {
            clean = uri.substring(uri.lastIndexOf("/") + 1);
        } else if (clean.contains("@")) {
            clean = clean.substring(0, clean.indexOf("@"));
        } else {
            clean = uri;
        }

        if (clean.length() > 1) {
            clean = Character.toUpperCase(clean.charAt(0)) + clean.substring(1);

            for (int i = 0; i < clean.length(); i++) {
                char c = clean.charAt(i);
                if (c == '_') {
                    clean = clean.substring(0, i) + "_" + Character.toUpperCase(clean.charAt(i + 1)) + clean.substring(i + 2);
                }
            }
        }
        return clean.trim();
    }

    public String optObjectUri() {
        if (object.startsWith("http"))
            return object;
        else
            return "";
    }

    public String shorten(String s) {
        String exc = s;

        if (exc.startsWith("[") && exc.endsWith("]"))
            exc = exc.substring(1, exc.length() - 1);

        if (exc.length() > 280 && !exc.startsWith("http")) {
            int lastStrong = exc.indexOf("</strong>", 271);
            if (lastStrong == -1) {
                exc = exc.substring(0, 280) + "...";
            } else {
                exc = exc.substring(0, lastStrong + 9) + "...";
            }
        }


        return exc;
    }

    public ResultTriple(String subject, String predicate, String object, String subExt, String objExt, String subHighlight, String preHighlight, String objHighlight) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.subExt = subExt;
        this.objExt = objExt;
        this.subHighlight = subHighlight;
        this.preHighlight = preHighlight;
        this.objHighlight = objHighlight;
    }

}
