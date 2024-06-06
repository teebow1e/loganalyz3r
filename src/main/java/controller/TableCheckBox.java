package controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class TableCheckBox {
    private BooleanProperty selected;
    private String[] data;

    public TableCheckBox(String[] data) {
        this.data = data;
        this.selected = new SimpleBooleanProperty(false);
    }
    public boolean isSelected() {
        return selected.get();
    }
    public BooleanProperty selectedProperty() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
    public String[] getData() {
        return data;
    }
    public void setData(String[] data) {
        this.data = data;
    }
}
