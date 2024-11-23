package ui;

public class UseCaseRelationship {
    private final String type;
    private final String startTitle;
    private final String endTitle;

    public UseCaseRelationship(String startTitle, String endTitle, String type) {
        this.startTitle = startTitle;
        this.endTitle = endTitle;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getStartTitle() {
        return startTitle;
    }

    public String getEndTitle() {
        return endTitle;
    }

    @Override
    public String toString() {
        return "Relationship{" +
                "type='" + type + '\'' +
                ", start='" + startTitle + '\'' +
                ", end='" + endTitle + '\'' +
                '}';
    }
}
