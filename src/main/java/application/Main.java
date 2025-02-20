package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.StorageManager;
import service.CategoryManager;
import service.PriorityManager;
import service.ReminderManager;
import service.TaskManager;

import java.io.IOException;

public class Main extends Application {
    private static TaskManager taskManager = new TaskManager();
    private static CategoryManager categoryManager = new CategoryManager();
    private static PriorityManager priorityManager = new PriorityManager();
    private static ReminderManager reminderManager = new ReminderManager();

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/Main.fxml")), 600, 400));
            primaryStage.setTitle("Task Management System");

            // ✅ Load data from JSON on startup
            loadDataOnStartup();

            // ✅ Save data before closing
            primaryStage.setOnCloseRequest(event -> saveDataOnExit());

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Main.fxml: " + e.getMessage());
        }
    }

    private void loadDataOnStartup() {
        try {
            taskManager.getAllTasks();
            categoryManager.setCategories(StorageManager.loadCategories());
            priorityManager.setPriorities(StorageManager.loadPriorities());
            reminderManager.setReminders(StorageManager.loadReminders());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load data from JSON files: " + e.getMessage());
        }
    }

    private void saveDataOnExit() {
        try {
            StorageManager.saveTasks(taskManager.getAllTasks());
            StorageManager.saveCategories(categoryManager.getCategories());
            StorageManager.savePriorities(priorityManager.getPriorities());
            StorageManager.saveReminders(reminderManager.getAllReminders());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to save data to JSON files: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
