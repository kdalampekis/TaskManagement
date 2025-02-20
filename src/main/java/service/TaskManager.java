package service;

import model.Task;
import model.TaskStatus;
import model.Priority;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class TaskManager {
    private List<Task> tasks;

    public TaskManager() {
        try {
            this.tasks = StorageManager.loadTasks();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        checkDelayedTasks();  // Ensure delayed tasks are updated at startup
    }

    public void addTask(Task task) {
        tasks.add(task);
        try {
            StorageManager.saveTasks(tasks);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateTask(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(task.getId())) {
                tasks.set(i, task);
                try {
                    StorageManager.saveTasks(tasks);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
        }
    }

    public void deleteTask(String taskId) {
        tasks.removeIf(task -> task.getId().equals(taskId));
        try {
            StorageManager.saveTasks(tasks);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkDelayedTasks() {
        LocalDate today = LocalDate.now();
        for (Task task : tasks) {
            if (task.getDueDate().isBefore(today) && task.getStatus() != TaskStatus.COMPLETED) {
                task.setStatus(TaskStatus.DELAYED);
            }
        }
        try {
            StorageManager.saveTasks(tasks);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Task> getAllTasks() { return tasks; }

    public void deleteTasksByCategory(String categoryName) {
        tasks.removeIf(task -> task.getCategory().getName().equals(categoryName));
        try {
            StorageManager.saveTasks(tasks);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reassignTasksToDefaultPriority(String deletedPriority) {
        for (Task task : tasks) {
            if (task.getPriority().getName().equals(deletedPriority)) {
                task.setPriority(new Priority("Default"));
            }
        }
        try {
            StorageManager.saveTasks(tasks);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


