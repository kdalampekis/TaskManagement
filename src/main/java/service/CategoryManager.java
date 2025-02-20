package service;

import model.Category;
import java.util.ArrayList;
import java.util.List;

public class CategoryManager {
    private List<Category> categories;

    public CategoryManager() {
        this.categories = new ArrayList<>();
        loadDefaultCategories();
    }
    public void setCategories(List<Category> categories) {
        this.categories = new ArrayList<>(categories);
    }

    private void loadDefaultCategories() {
        if (categories.isEmpty()) {
            categories.add(new Category("General"));
            categories.add(new Category("Work"));
            categories.add(new Category("Personal"));
        }
    }

    public void addCategory(String name) {
        categories.add(new Category(name));
    }

    public void renameCategory(String oldName, String newName) {
        for (Category category : categories) {
            if (category.getName().equals(oldName)) {
                category.setName(newName);
                return;
            }
        }
    }

    public void deleteCategory(String name, TaskManager taskManager) {
        categories.removeIf(category -> category.getName().equals(name));
        taskManager.deleteTasksByCategory(name);
    }

    public List<Category> getCategories() {
        return categories;
    }
}
