package service;

import model.Priority;
import java.util.ArrayList;
import java.util.List;

public class PriorityManager {
    private List<Priority> priorities;

    public PriorityManager() {
        this.priorities = new ArrayList<>();
        loadDefaultPriorities();
    }

    public void setPriorities(List<Priority> priorities) {
        this.priorities = new ArrayList<>(priorities);
    }

    private void loadDefaultPriorities() {
        if (priorities.isEmpty()) {
            priorities.add(new Priority("Default"));
            priorities.add(new Priority("High"));
            priorities.add(new Priority("Medium"));
            priorities.add(new Priority("Low"));
        }
    }

    public void addPriority(String name) {
        priorities.add(new Priority(name));
    }

    public void renamePriority(String oldName, String newName) {
        for (Priority priority : priorities) {
            if (priority.getName().equals(oldName)) {
                priority.setName(newName);
                return;
            }
        }
    }

    public void deletePriority(String name, TaskManager taskManager) {
        if (name.equals("Default")) return; // Prevent deleting Default
        priorities.removeIf(priority -> priority.getName().equals(name));

        // Assign affected tasks to "Default"
        taskManager.reassignTasksToDefaultPriority(name);
    }

    public List<Priority> getPriorities() {
        return priorities;
    }
}
