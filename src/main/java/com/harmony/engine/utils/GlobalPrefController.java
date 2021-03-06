package com.harmony.engine.utils;

import com.harmony.engine.Harmony;
import com.harmony.engine.Launcher;
import com.harmony.engine.data.GlobalData;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

public class GlobalPrefController {

    public ComboBox<String> theme;
    public CheckBox autoSave;

    public Button cancelButton;
    public Button applyButton;

    private boolean isThemeChange = false;

    @FXML
    public void initialize() {
        setFields();

        cancelButton.setOnMouseClicked(event -> {
            GlobalData.staticStage.close();
        });

        applyButton.setOnMouseClicked(event -> {
            // Set Values
            for(int i = 0; i < GlobalData.Theme.values().length; i++) {
                if(GlobalData.Theme.values()[i].toString().equals(theme.getSelectionModel().getSelectedItem()))
                    GlobalData.setTheme(GlobalData.Theme.values()[i]);
            }

            GlobalData.setAutoSave(autoSave.isSelected());

            if(isThemeChange) {
                if(Harmony.staticStage != null)
                    Harmony.changeTheme();
                else if(Launcher.staticStage != null)
                    Launcher.changeTheme();
            }

            GlobalData.staticStage.close();
        });

        handleChanges();
    }

    private void setFields() {
        int selectedTheme = 0;

        for(int i = 0; i < GlobalData.Theme.values().length; i++) {
            theme.getItems().add(GlobalData.Theme.values()[i].toString());
            if(GlobalData.Theme.values()[i] == GlobalData.getTheme()) selectedTheme = i;
        }

        theme.getSelectionModel().select(selectedTheme);

        autoSave.setSelected(GlobalData.getAutoSave());
    }

    private void handleChanges() {
        theme.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> isThemeChange = true);
    }

}
