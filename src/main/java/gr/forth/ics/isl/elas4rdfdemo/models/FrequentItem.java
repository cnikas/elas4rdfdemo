package gr.forth.ics.isl.elas4rdfdemo.models;

public class FrequentItem {
    private String name;
    private int count;
    private String uri;
    private String urisOfType;

    public String getUrisOfType() {
        return urisOfType;
    }

    public void setUrisOfType(String urisOfType) {
        this.urisOfType = urisOfType;
    }

    public FrequentItem(String name, int count, String uri) {
        this.name = name;
        this.count = count;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String buildRequest() {
        return "/entitiesForSchema?query=" + this.getUri() + "&urisOfType=" + this.getUrisOfType();
    }
}
