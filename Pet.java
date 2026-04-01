package com.mycompany.petspahotel;

public class Pet {
    private int id;
    private String name;
    private String breed;
    private int age;
    private String healthCondition;
    private String specialNotes;
    
    public Pet(String name, String breed, int age, String healthCondition) {
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.healthCondition = healthCondition;
        this.specialNotes = "";
    }
    
    public Pet(int id, String name, String breed, int age, String healthCondition) {
        this.id = id;
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.healthCondition = healthCondition;
        this.specialNotes = "";
    }
    
    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getBreed() { return breed; }
    public int getAge() { return age; }
    public String getHealthCondition() { return healthCondition; }
    public String getSpecialNotes() { return specialNotes; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setBreed(String breed) { this.breed = breed; }
    public void setAge(int age) { this.age = age; }
    public void setHealthCondition(String healthCondition) { this.healthCondition = healthCondition; }
    public void setSpecialNotes(String specialNotes) { this.specialNotes = specialNotes; }
    
    public void displayPetInfo() {
        System.out.println("\n=== PET INFORMATION ===");
        System.out.println("Name: " + name);
        System.out.println("Breed: " + breed);
        System.out.println("Age: " + age + " years");
        System.out.println("Health Condition: " + healthCondition);
        if (!specialNotes.isEmpty()) {
            System.out.println("Special Notes: " + specialNotes);
        }
    }
    
    public boolean hasSpecialNeeds() {
        return healthCondition != null && !healthCondition.equalsIgnoreCase("Healthy");
    }
}