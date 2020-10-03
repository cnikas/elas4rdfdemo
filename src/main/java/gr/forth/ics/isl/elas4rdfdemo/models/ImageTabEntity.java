package gr.forth.ics.isl.elas4rdfdemo.models;

import java.util.List;

public class ImageTabEntity {
    private String uri;
    private String imageUrl;
    private List<String> adjacencies;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getAdjacencies() {
        return adjacencies;
    }

    public void setAdjacencies(List<String> adjacencies) {
        this.adjacencies = adjacencies;
    }
}
