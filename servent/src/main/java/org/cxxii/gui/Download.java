package org.cxxii.gui;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class Download {

    private final SimpleStringProperty fileName;
    private final SimpleStringProperty status;
    private final SimpleDoubleProperty progress;
    private final SimpleStringProperty speed;

    public Download(String fileName, String status, double progress , String speed) {
        this.fileName = new SimpleStringProperty(fileName);
        this.status = new SimpleStringProperty(status);
        this.progress = new SimpleDoubleProperty(progress);
        this.speed = new SimpleStringProperty(speed);
    }

    public SimpleStringProperty fileNameProperty() {
        return fileName;
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public SimpleDoubleProperty progressProperty() {
        return progress;
    }

    public SimpleStringProperty speedProperty() {
        return speed;
    }
}
