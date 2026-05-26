package com.com253.payrollsystem.presentation.gui.navigation;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.stage.Window;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SceneManager {

    private static final Logger LOGGER = Logger.getLogger(SceneManager.class.getName());
    private static SceneManager instance;
    
    private final Map<Routes.Screen, Scene> sceneCache;
    private final Map<Routes.Screen, Object> controllerCache;
    private Stage primaryStage;
    private Routes.Screen currentScreen;

    /**
     * Private constructor for singleton pattern.
     */
    private SceneManager() {
        this.sceneCache = new HashMap<>();
        this.controllerCache = new HashMap<>();
    }

    /**
     * Get singleton instance.
     */
    public static synchronized SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    /**
     * Initialize SceneManager with primary stage (call at app startup).
     */
    public void initialize(Stage primaryStage) {
        this.primaryStage = primaryStage;
        LOGGER.info("SceneManager initialized with primary stage");
    }

    /**
     * Switch the primary stage to a new scene.
     * 
     * @param screen the screen route to load
     * @throws RuntimeException if FXML loading fails
     */
    public void switchScene(Routes.Screen screen) {
        if (primaryStage == null) {
            LOGGER.severe("Primary stage not initialized. Call initialize() first.");
            throw new RuntimeException("SceneManager not properly initialized");
        }
        
        try {
            Scene scene = getOrLoadScene(screen);
            primaryStage.setScene(scene);
            primaryStage.setTitle("NU Payroll System - " + screen.getTitle());
            this.currentScreen = screen;
            
            LOGGER.info("Switched to scene: " + screen.getTitle());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to switch to scene: " + screen.getTitle(), e);
            showErrorDialog("Navigation Error", "Failed to load " + screen.getTitle(), e.getMessage());
        }
    }

    /**
     * Switch scene on a specific stage (for multi-window support).
     */
    public void switchScene(Stage stage, Routes.Screen screen) {
        try {
            Scene scene = getOrLoadScene(screen);
            stage.setScene(scene);
            stage.setTitle("NU Payroll System - " + screen.getTitle());
            this.currentScreen = screen;
            
            LOGGER.info("Switched stage to scene: " + screen.getTitle());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to switch stage to scene: " + screen.getTitle(), e);
            showErrorDialog("Navigation Error", "Failed to load " + screen.getTitle(), e.getMessage());
        }
    }

    /**
     * Show a modal dialog window.
     * 
     * @param screen the screen to show as modal
     * @param title the window title
     * @param owner the owner stage (null = unowned)
     * @return the new Stage
     */
    public Stage showModal(Routes.Screen screen, String title, Window owner) {
        try {
            Stage modalStage = new Stage();
            modalStage.setTitle("NU Payroll System - " + (title != null ? title : screen.getTitle()));
            
            if (owner != null) {
                modalStage.initModality(Modality.WINDOW_MODAL);
                modalStage.initOwner(owner);
            } else {
                modalStage.initModality(Modality.APPLICATION_MODAL);
            }
            
            Scene scene = getOrLoadScene(screen);
            modalStage.setScene(scene);
            modalStage.show();
            
            LOGGER.info("Showed modal: " + screen.getTitle());
            return modalStage;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to show modal: " + screen.getTitle(), e);
            showErrorDialog("Modal Error", "Failed to load " + screen.getTitle(), e.getMessage());
            return null;
        }
    }

    /**
     * Show a modal dialog (using primary stage as owner).
     */
    public Stage showModal(Routes.Screen screen, String title) {
        return showModal(screen, title, primaryStage);
    }

    /**
     * Get or load a scene. Caches scenes for better performance.
     * 
     * @param screen the screen to load
     * @return the Scene
     * @throws IOException if FXML loading fails
     */
    private Scene getOrLoadScene(Routes.Screen screen) throws IOException {
        if (sceneCache.containsKey(screen)) {
            return sceneCache.get(screen);
        }

        FXMLLoader loader = createFxmlLoader(screen);
        Parent root = loader.load();
        Object controller = loader.getController();
        
        Scene scene = new Scene(root);
        
        // Apply stylesheets
        for (String stylesheet : Routes.getStylesheets(screen)) {
            String cssResource = SceneManager.class.getResource(stylesheet).toExternalForm();
            scene.getStylesheets().add(cssResource);
        }
        
        // Cache the scene and controller
        sceneCache.put(screen, scene);
        if (controller != null) {
            controllerCache.put(screen, controller);
        }
        
        LOGGER.info("Loaded scene: " + screen.getTitle());
        return scene;
    }

    /**
     * Create an FXMLLoader for a screen route.
     */
    private FXMLLoader createFxmlLoader(Routes.Screen screen) throws IOException {
        String fxmlPath = screen.getFxmlPath();
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
        
        if (loader.getLocation() == null) {
            throw new IOException("FXML resource not found: " + fxmlPath);
        }
        
        return loader;
    }

    /**
     * Get a cached controller for a screen.
     * @return the controller, or null if not found/cached
     */
    public <T> T getController(Routes.Screen screen, Class<T> controllerClass) {
        Object controller = controllerCache.get(screen);
        if (controller != null && controllerClass.isInstance(controller)) {
            return controllerClass.cast(controller);
        }
        return null;
    }

    /**
     * Clear scene cache (useful when reloading UI or resetting state).
     */
    public void clearCache() {
        sceneCache.clear();
        controllerCache.clear();
        LOGGER.info("Scene cache cleared");
    }

    /**
     * Clear cache for a specific screen.
     */
    public void clearCache(Routes.Screen screen) {
        sceneCache.remove(screen);
        controllerCache.remove(screen);
        LOGGER.info("Cache cleared for screen: " + screen.getTitle());
    }

    /**
     * Get the currently displayed screen.
     */
    public Routes.Screen getCurrentScreen() {
        return currentScreen;
    }

    /**
     * Get the primary stage.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Show a simple error dialog.
     * TODO: Replace with proper Alert/Dialog once UI framework is finalized
     */
    private void showErrorDialog(String title, String header, String content) {
        Platform.runLater(() -> {
            LOGGER.log(Level.WARNING, title + " | " + header + " | " + content);
            // TODO: Implement proper error dialog
            // For now, just log the error
        });
    }
}
