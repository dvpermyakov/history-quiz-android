package dvpermyakov.historyquiz.adapter_models;

/**
 * Created by dvpermyakov on 02.06.2016.
 */
public class CardViewInformationModel {
    private Integer color;
    private Integer drawable;
    private String title;
    private String value;

    public CardViewInformationModel(String title, String value, Integer drawable, Integer color) {
        this.title = title;
        this.value = value;
        this.drawable = drawable;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }
    public String getValue() {
        return value;
    }
    public Integer getDrawable() {
        return drawable;
    }
    public Integer getColor() {
        return color;
    }
}
