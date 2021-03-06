package com.harmony.engine;

import com.harmony.engine.data.GlobalData;
import com.harmony.engine.data.ProjectData;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.File;

public class Harmony extends Application {

    private static Thread coreThread;

    public static File directory;
    public static Stage staticStage;

    private static Scene staticScene;

    private boolean controlDown = false;
    private boolean sDown = false;
    public static boolean saving = false;

    public static void main(String[] args) throws Exception { open(new File("/Users/nick227889/Dev/Game")); }

    public static void open(File directory) throws Exception {
        Harmony.directory = directory;
        ProjectData.load(directory);

        if(Launcher.staticStage != null) {
            new Harmony().start(Launcher.staticStage);
        } else {
            Launcher.configureSystemProperties();
            launch();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Harmony.class.getResource("/engine.fxml"));

        Scene scene = new Scene(root, 1280, 720);
        Harmony.staticScene = scene;

        GlobalData.load();

        // Handle Theme
        scene.getStylesheets().add(Harmony.class.getResource(GlobalData.getThemeCSSLocation()).toExternalForm());

        stage.setTitle("Harmony Engine v1.0");
        stage.setMinHeight(400);
        stage.setMinWidth(600);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();

        stage.setOnCloseRequest(event -> closeApplication());

        scene.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.CONTROL || keyEvent.getCode() == KeyCode.COMMAND) {
                controlDown = true;
            } else if(keyEvent.getCode() == KeyCode.S) {
                sDown = true;
            }

            if(controlDown && sDown && !saving) {
                saving = true;
                ProjectData.save(Harmony.directory);
                saving = false;
            }
        });

        scene.setOnKeyReleased(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.CONTROL || keyEvent.getCode() == KeyCode.COMMAND) {
                controlDown = false;
            } else if(keyEvent.getCode() == KeyCode.S) {
                sDown = false;
            }
        });
    }

    private void closeApplication() {
        ProjectData.save(Harmony.directory);
        GlobalData.save();

        Platform.exit();
        System.exit(0);
    }

    public static void changeTheme() {
        staticScene.getStylesheets().remove(staticScene.getStylesheets().size() - 1);

        // Handle Theme
        staticScene.getStylesheets().add(Harmony.class.getResource(GlobalData.getThemeCSSLocation()).toExternalForm());
    }

    public static String getResourceString(String path) {
        return path.trim().replaceAll(Harmony.directory.getPath(), "").trim();
    }
}
