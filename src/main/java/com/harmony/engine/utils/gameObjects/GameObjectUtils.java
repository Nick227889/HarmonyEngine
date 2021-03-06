package com.harmony.engine.utils.gameObjects;

import com.harmony.engine.Harmony;
import com.harmony.engine.data.GlobalData;
import com.harmony.engine.utils.Status;
import com.harmony.engine.utils.textures.TextureUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class GameObjectUtils {

    public static Stage staticStage;

    public static void createGameObject() {
        try {
            Status.setCurrentStatus(Status.Type.STAND_BY);
            FXMLLoader loader = new FXMLLoader(TextureUtils.class.getResource("/utils/gameObject.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            // Handle Theme
            scene.getStylesheets().add(Harmony.class.getResource(GlobalData.getThemeCSSLocation()).toExternalForm());

            Stage stage = new Stage();
            GameObjectUtils.staticStage = stage;
            stage.setTitle("Create Game Object");
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setAlwaysOnTop(true);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
