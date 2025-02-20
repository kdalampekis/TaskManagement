package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class StorageManager {
    private static final String DIRECTORY = "medialab/";
    private static final String TASKS_FILE = DIRECTORY + "tasks.json";
    private static final String CATEGORIES_FILE = DIRECTORY + "categories.json";
    private static final String PRIORITIES_FILE = DIRECTORY + "priorities.json";
    private static final String REMINDERS_FILE = DIRECTORY + "reminders.json";

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()); // ✅ Support for LocalDate

    static {
        ensureDirectoryExists();
    }

    private static void ensureDirectoryExists() {
        try {
            Files.createDirectories(Paths.get(DIRECTORY));
            createEmptyFileIfNotExists(TASKS_FILE);
            createEmptyFileIfNotExists(CATEGORIES_FILE);
            createEmptyFileIfNotExists(PRIORITIES_FILE);
            createEmptyFileIfNotExists(REMINDERS_FILE);
        } catch (IOException e) {
            System.err.println("Error creating medialab directory: " + e.getMessage());
        }
    }

    private static void createEmptyFileIfNotExists(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            objectMapper.writeValue(file, List.of()); // ✅ Create empty JSON array
        }
    }

    // ✅ Save and Load Tasks
    public static void saveTasks(List<Task> tasks) throws IOException {
        objectMapper.writeValue(new File(TASKS_FILE), tasks);
    }

    public static List<Task> loadTasks() throws IOException {
        File file = new File(TASKS_FILE);
        if (!file.exists() || file.length() == 0) return List.of();
        return objectMapper.readValue(file, new TypeReference<List<Task>>() {});
    }

    // ✅ Save and Load Categories
    public static void saveCategories(List<Category> categories) throws IOException {
        objectMapper.writeValue(new File(CATEGORIES_FILE), categories);
    }

    public static List<Category> loadCategories() throws IOException {
        File file = new File(CATEGORIES_FILE);
        if (!file.exists() || file.length() == 0) return List.of();
        return objectMapper.readValue(file, new TypeReference<List<Category>>() {});
    }

    // ✅ Save and Load Priorities
    public static void savePriorities(List<Priority> priorities) throws IOException {
        objectMapper.writeValue(new File(PRIORITIES_FILE), priorities);
    }

    public static List<Priority> loadPriorities() throws IOException {
        File file = new File(PRIORITIES_FILE);
        if (!file.exists() || file.length() == 0) return List.of();
        return objectMapper.readValue(file, new TypeReference<List<Priority>>() {});
    }

    // ✅ Save and Load Reminders
    public static void saveReminders(List<Reminder> reminders) throws IOException {
        objectMapper.writeValue(new File(REMINDERS_FILE), reminders);
    }

    public static List<Reminder> loadReminders() throws IOException {
        File file = new File(REMINDERS_FILE);
        if (!file.exists() || file.length() == 0) return List.of();
        return objectMapper.readValue(file, new TypeReference<List<Reminder>>() {});
    }
}
