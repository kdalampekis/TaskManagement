package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.*;
import service.CategoryManager;
import service.TaskManager;
import service.ReminderManager;
import service.PriorityManager;
import service.StorageManager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class MainController {
    private TaskManager taskManager = new TaskManager();
    private CategoryManager categoryManager = new CategoryManager();
    private ReminderManager reminderManager = new ReminderManager();

    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> titleColumn;
    @FXML private TableColumn<Task, String> categoryColumn;
    @FXML private TableColumn<Task, String> priorityColumn;
    @FXML private TableColumn<Task, String> statusColumn;

    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> priorityComboBox;
    @FXML private Button addTaskButton;

    @FXML private ComboBox<String> reminderTypeComboBox;
    @FXML private DatePicker reminderDatePicker;
    @FXML private Button addReminderButton;


    // ✅ Summary Stats UI Elements
    @FXML private Label totalTasksLabel;
    @FXML private Label completedTasksLabel;
    @FXML private Label delayedTasksLabel;
    @FXML private Label dueSoonTasksLabel;

    // ✅ Search & Filtering UI Elements
    @FXML private TextField titleSearchField;
    @FXML private ComboBox<String> categoryFilterComboBox;
    @FXML private ComboBox<String> priorityFilterComboBox;
    @FXML private Button updateTaskButton; // ✅ New
    @FXML private Button deleteTaskButton; // ✅ New

    @FXML private ComboBox<String> categoryListComboBox;
    @FXML private TextField newCategoryField;
    @FXML private Button addCategoryButton;
    @FXML private Button renameCategoryButton;
    @FXML private Button deleteCategoryButton;

    @FXML private ComboBox<String> priorityListComboBox;
    @FXML private TextField newPriorityField;
    @FXML private Button addPriorityButton;
    @FXML private Button renamePriorityButton;
    @FXML private Button deletePriorityButton;


    @FXML private TableView<Reminder> reminderTable;
    @FXML private TableColumn<Reminder, String> reminderTaskColumn;
    @FXML private TableColumn<Reminder, String> reminderTypeColumn;
    @FXML private Button editReminderButton;
    @FXML private Button deleteReminderButton;


    private ObservableList<Task> taskList = FXCollections.observableArrayList();
    private PriorityManager priorityManager = new PriorityManager();


    public void initialize() {
        titleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));
        categoryColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCategory().getName()));
        priorityColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPriority().getName()));
        statusColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().toString()));

        try {
            // ✅ Load data from JSON files
            taskList.addAll(StorageManager.loadTasks());
            taskTable.setItems(taskList);

            categoryManager.setCategories(StorageManager.loadCategories());
            priorityManager.setPriorities(StorageManager.loadPriorities());
            reminderManager.setReminders(StorageManager.loadReminders());

            // ✅ Debug: Print loaded categories and priorities
            System.out.println("Loaded Categories: " + categoryManager.getCategories());
            System.out.println("Loaded Priorities: " + priorityManager.getPriorities());

            loadCategories();
            loadPriorities();
            loadReminders();

            updateTaskSummary(); // ✅ Update summary stats
            checkForDelayedTasks(); // ✅ Show alert for delayed tasks on startup

        } catch (IOException e) {
            showAlert("Error", "Failed to load data from files.", Alert.AlertType.ERROR);
        }

        // ✅ Load categories into ComboBox
        System.out.println("Loading categories into ComboBox: " + categoryManager.getCategories());
        List<String> categoryNames = categoryManager.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toList());

        categoryComboBox.setItems(FXCollections.observableArrayList(categoryNames));
        if (!categoryNames.isEmpty()) {
            categoryComboBox.setValue(categoryNames.get(0)); // ✅ Set default value
        } else {
            System.out.println("No categories found!");
        }

        // ✅ Load priorities into ComboBox
        List<String> priorityNames = priorityManager.getPriorities().stream()
                .map(Priority::getName)
                .collect(Collectors.toList());

        priorityComboBox.setItems(FXCollections.observableArrayList(priorityNames));
        if (!priorityNames.isEmpty()) {
            priorityComboBox.setValue(priorityNames.get(0)); // ✅ Set default value
        } else {
            System.out.println("No priorities found!");
        }

        // ✅ Load reminder types
        reminderTypeComboBox.setItems(FXCollections.observableArrayList(
                "One Day Before", "One Week Before", "One Month Before", "Specific Date"
        ));

        // ✅ Load filters
        categoryFilterComboBox.setItems(FXCollections.observableArrayList("All"));
        categoryFilterComboBox.getItems().addAll(categoryNames);
        categoryFilterComboBox.setValue("All");

        priorityFilterComboBox.setItems(FXCollections.observableArrayList("All"));
        priorityFilterComboBox.getItems().addAll(priorityNames);
        priorityFilterComboBox.setValue("All");

        // ✅ Add listeners to trigger filtering
        titleSearchField.textProperty().addListener((observable, oldValue, newValue) -> filterTasks());
        categoryFilterComboBox.setOnAction(event -> filterTasks());
        priorityFilterComboBox.setOnAction(event -> filterTasks());
    }

    private void loadPriorities() {
        List<String> priorityNames = priorityManager.getPriorities().stream()
                .map(Priority::getName)
                .collect(Collectors.toList());

        priorityListComboBox.setItems(FXCollections.observableArrayList(priorityNames));
        if (!priorityNames.isEmpty()) {
            priorityListComboBox.setValue(priorityNames.get(0));
        }
    }


    private void loadReminders() {
        ObservableList<Reminder> reminderList = FXCollections.observableArrayList(reminderManager.getAllReminders());
        reminderTable.setItems(reminderList);
    }

    @FXML
    private void editReminder() {
        Reminder selectedReminder = reminderTable.getSelectionModel().getSelectedItem();
        if (selectedReminder == null) {
            showAlert("Error", "Please select a reminder to edit.", Alert.AlertType.ERROR);
            return;
        }

        // Example: Change the reminder type
        ReminderType newType = ReminderType.ONE_WEEK_BEFORE; // Example modification
        LocalDate newDate = LocalDate.now().plusWeeks(1); // Example modification

        Reminder newReminder = new Reminder(selectedReminder.getTask(), newType, newDate);
        reminderManager.editReminder(selectedReminder, newReminder);

        loadReminders();
        showAlert("Success", "Reminder updated successfully!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void deleteReminder() {
        Reminder selectedReminder = reminderTable.getSelectionModel().getSelectedItem();
        if (selectedReminder == null) {
            showAlert("Error", "Please select a reminder to delete.", Alert.AlertType.ERROR);
            return;
        }

        reminderManager.deleteReminder(selectedReminder);
        loadReminders();
        showAlert("Success", "Reminder deleted successfully!", Alert.AlertType.INFORMATION);
    }



    @FXML
    private void addPriority() {
        String newPriorityName = newPriorityField.getText().trim();
        if (newPriorityName.isEmpty()) {
            showAlert("Error", "Priority name cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        priorityManager.addPriority(newPriorityName);
        loadPriorities(); // Refresh UI
        newPriorityField.clear();
        showAlert("Success", "Priority added successfully!", Alert.AlertType.INFORMATION);
    }


    @FXML
    private void renamePriority() {
        String selectedPriority = priorityListComboBox.getValue();
        String newPriorityName = newPriorityField.getText().trim();

        if (selectedPriority == null || newPriorityName.isEmpty()) {
            showAlert("Error", "Please select a priority and enter a new name.", Alert.AlertType.ERROR);
            return;
        }

        priorityManager.renamePriority(selectedPriority, newPriorityName);
        loadPriorities(); // Refresh UI
        newPriorityField.clear();
        showAlert("Success", "Priority renamed successfully!", Alert.AlertType.INFORMATION);
    }


    @FXML
    private void deletePriority() {
        String selectedPriority = priorityListComboBox.getValue();
        if (selectedPriority == null) {
            showAlert("Error", "Please select a priority to delete.", Alert.AlertType.ERROR);
            return;
        }

        priorityManager.deletePriority(selectedPriority, taskManager);
        loadPriorities(); // Refresh UI
        showAlert("Success", "Priority deleted successfully!", Alert.AlertType.INFORMATION);
    }




    @FXML
    private void addTask() {
        String title = titleField.getText();
        if (title.isEmpty()) return;

        String selectedCategory = categoryComboBox.getValue();
        String selectedPriority = priorityComboBox.getValue();

        Task newTask = new Task(
                "" + (taskList.size() + 1), title, descriptionField.getText(),
                new Category(selectedCategory), new Priority(selectedPriority), LocalDate.now().plusDays(7)
        );

        taskManager.addTask(newTask);
        taskList.add(newTask);

        titleField.clear();
        descriptionField.clear();
        filterTasks(); // ✅ Refresh filtered view
    }

    private void loadCategories() {
        List<String> categoryNames = categoryManager.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toList());

        categoryListComboBox.setItems(FXCollections.observableArrayList(categoryNames));
        if (!categoryNames.isEmpty()) {
            categoryListComboBox.setValue(categoryNames.get(0));
        }
    }

    @FXML
    private void addCategory() {
        String newCategoryName = newCategoryField.getText().trim();
        if (newCategoryName.isEmpty()) {
            showAlert("Error", "Category name cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        categoryManager.addCategory(newCategoryName);
        loadCategories(); // Refresh UI
        newCategoryField.clear();
        showAlert("Success", "Category added successfully!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void renameCategory() {
        String selectedCategory = categoryListComboBox.getValue();
        String newCategoryName = newCategoryField.getText().trim();

        if (selectedCategory == null || newCategoryName.isEmpty()) {
            showAlert("Error", "Please select a category and enter a new name.", Alert.AlertType.ERROR);
            return;
        }

        categoryManager.renameCategory(selectedCategory, newCategoryName);
        loadCategories(); // Refresh UI
        newCategoryField.clear();
        showAlert("Success", "Category renamed successfully!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void deleteCategory() {
        String selectedCategory = categoryListComboBox.getValue();
        if (selectedCategory == null) {
            showAlert("Error", "Please select a category to delete.", Alert.AlertType.ERROR);
            return;
        }

        categoryManager.deleteCategory(selectedCategory, taskManager);
        loadCategories(); // Refresh UI
        showAlert("Success", "Category deleted successfully!", Alert.AlertType.INFORMATION);
    }


    @FXML
    private void addReminder() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            System.out.println("Please select a task.");
            return;
        }

        String selectedReminderType = reminderTypeComboBox.getValue();
        ReminderType type = null;
        LocalDate reminderDate = null;

        switch (selectedReminderType) {
            case "One Day Before":
                type = ReminderType.ONE_DAY_BEFORE;
                reminderDate = selectedTask.getDueDate().minusDays(1);
                break;
            case "One Week Before":
                type = ReminderType.ONE_WEEK_BEFORE;
                reminderDate = selectedTask.getDueDate().minusWeeks(1);
                break;
            case "One Month Before":
                type = ReminderType.ONE_MONTH_BEFORE;
                reminderDate = selectedTask.getDueDate().minusMonths(1);
                break;
            case "Specific Date":
                type = ReminderType.SPECIFIC_DATE;
                reminderDate = reminderDatePicker.getValue();
                break;
        }

        if (reminderDate == null || reminderDate.isAfter(selectedTask.getDueDate())) {
            System.out.println("Invalid reminder date.");
            return;
        }

        reminderManager.addReminder(selectedTask, type, reminderDate);
        System.out.println("Reminder added: " + type + " for task " + selectedTask.getTitle());
    }

    private void filterTasks() {
        String titleFilter = titleSearchField.getText().toLowerCase();
        String categoryFilter = categoryFilterComboBox.getValue();
        String priorityFilter = priorityFilterComboBox.getValue();

        List<Task> filteredTasks = taskManager.getAllTasks().stream()
                .filter(task -> titleFilter.isEmpty() || task.getTitle().toLowerCase().contains(titleFilter))
                .filter(task -> categoryFilter.equals("All") || task.getCategory().getName().equalsIgnoreCase(categoryFilter))
                .filter(task -> priorityFilter.equals("All") || task.getPriority().getName().equalsIgnoreCase(priorityFilter))
                .collect(Collectors.toList());

        taskList.setAll(filteredTasks);
    }

    private void checkForDelayedTasks() {
        long delayedCount = taskManager.getAllTasks().stream()
                .filter(task -> task.getStatus() == TaskStatus.DELAYED)
                .count();

        if (delayedCount > 0) {
            showAlert("Warning", "You have " + delayedCount + " delayed tasks!", Alert.AlertType.WARNING);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateTaskSummary() {
        long totalTasks = taskManager.getAllTasks().size();
        long completedTasks = taskManager.getAllTasks().stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
                .count();
        long delayedTasks = taskManager.getAllTasks().stream()
                .filter(task -> task.getStatus() == TaskStatus.DELAYED)
                .count();
        long dueSoonTasks = taskManager.getAllTasks().stream()
                .filter(task -> !task.getStatus().equals(TaskStatus.COMPLETED) &&
                        task.getDueDate().isBefore(LocalDate.now().plusDays(7)))
                .count();

        totalTasksLabel.setText(String.valueOf(totalTasks));
        completedTasksLabel.setText(String.valueOf(completedTasks));
        delayedTasksLabel.setText(String.valueOf(delayedTasks));
        dueSoonTasksLabel.setText(String.valueOf(dueSoonTasks));
    }

    @FXML
    private void updateTask() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("Error", "Please select a task to update.", Alert.AlertType.ERROR);
            return;
        }

        String newTitle = titleField.getText();
        String newDescription = descriptionField.getText();
        String newCategory = categoryComboBox.getValue();
        String newPriority = priorityComboBox.getValue();

        if (newTitle.isEmpty()) {
            showAlert("Error", "Title cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        // Update task fields
        selectedTask.setTitle(newTitle);
        selectedTask.setDescription(newDescription);
        selectedTask.setCategory(new Category(newCategory));
        selectedTask.setPriority(new Priority(newPriority));

        taskManager.updateTask(selectedTask);
        taskTable.refresh(); // ✅ Refresh UI
        updateTaskSummary(); // ✅ Refresh stats
        filterTasks(); // ✅ Refresh filtered view

        showAlert("Success", "Task updated successfully!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void deleteTask() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("Error", "Please select a task to delete.", Alert.AlertType.ERROR);
            return;
        }

        taskManager.deleteTask(selectedTask.getId());
        taskList.remove(selectedTask); // ✅ Remove from UI
        updateTaskSummary(); // ✅ Refresh stats
        filterTasks(); // ✅ Refresh filtered view

        showAlert("Success", "Task deleted successfully!", Alert.AlertType.INFORMATION);
    }

    public void saveDataOnExit() {
        try {
            StorageManager.saveTasks(taskList);
            StorageManager.saveCategories(categoryManager.getCategories());
            StorageManager.savePriorities(priorityManager.getPriorities());
            StorageManager.saveReminders(reminderManager.getAllReminders());
        } catch (IOException e) {
            e.printStackTrace(); // ✅ Debugging: Print error
            showAlert("Error", "Failed to save data to files: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

}
