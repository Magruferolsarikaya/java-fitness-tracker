import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static UserProfile user;

    public static void main(String[] args) {
        System.out.println("=== FITNESS TRACKER INITIALIZING ===");
        user = DataManager.loadData();

        // Eğer dosya yoksa ve veri yüklenemediyse
        if (user == null) {
            createNewUser();
        }

        boolean isRunning = true;

        while (isRunning) {
            printMenu();
            System.out.print("Select an option (1-5): ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addNewWorkout();
                    break;
                case "2":
                    user.printFullHistory();
                    break;
                case "3":
                    DataManager.saveData(user);
                    break;
                case "4":
                    System.out.println("Saving data and exiting...");
                    DataManager.saveData(user);
                    isRunning = false;
                    break;
                case "5":
                    updateProfile();
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
        scanner.close();
    }

    private static int readIntFromUser(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String a = scanner.nextLine();
                return Integer.parseInt(a);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid whole number!");
            }
        }
    }

    private static double readDoubleFromUser(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String a = scanner.nextLine();
                return Double.parseDouble(a);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid decimal number!");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Add New Workout");
        System.out.println("2. View Workout History");
        System.out.println("3. Save Data Manually");
        System.out.println("4. Exit");
        System.out.println("5. Update Profile");
    }

    private static void createNewUser() {
        System.out.println("\nNo profile found. Let's set up a new one!");
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        // Güvenli metotlara geçirildi
        int height = readIntFromUser("Enter your height (cm): ");
        double weight = readDoubleFromUser("Enter your weight (kg): ");

        user = new UserProfile(name, height, weight);
        System.out.println("Profile created successfully!");
    }

    private static void updateProfile() {
        System.out.println("\n--- UPDATE PROFILE ---");
        System.out.println("Current Stats: " + user.heightCm + " cm, " + user.weightKg + " kg");

        int currentHeight = readIntFromUser("Enter new height (cm): ");
        double currentWeight = readDoubleFromUser("Enter new weight (kg): ");

        user.heightCm = currentHeight;
        user.weightKg = currentWeight;
        System.out.println("Profile successfully updated!");
    }

    private static void addNewWorkout() {
        System.out.println("\n--- ADD NEW WORKOUT ---");
        System.out.println("Select Workout Type: 1.PUSH 2.PULL 3.LEGS 4.FULL_BODY 5.CARDIO");
        System.out.print("Choice: ");
        String typeChoice = scanner.nextLine();

        WorkoutType type;
        switch (typeChoice) {
            case "1": type = WorkoutType.PUSH; break;
            case "2": type = WorkoutType.PULL; break;
            case "3": type = WorkoutType.LEGS; break;
            case "4": type = WorkoutType.FULL_BODY; break;
            case "5": type = WorkoutType.CARDIO; break;
            default:
                System.out.println("Invalid type. Defaulting to FULL_BODY.");
                type = WorkoutType.FULL_BODY;
        }

        WorkoutDay newWorkout = new WorkoutDay(LocalDate.now(), type);

        boolean addingExercises = true;
        while (addingExercises) {
            System.out.println("\nAdd Exercise: 1.Weight 2.Cardio 3.Done (Save Workout)");
            System.out.print("Choice: ");
            String exChoice = scanner.nextLine();

            if (exChoice.equals("1")) {
                System.out.print("Exercise Name (e.g., Bench Press): ");
                String name = scanner.nextLine();

                System.out.print("Target Muscle (e.g., Chest): ");
                String muscle = scanner.nextLine();

                // Güvenli metotlara geçirildi
                int sets = readIntFromUser("Sets: ");
                int reps = readIntFromUser("Reps: ");
                double weight = readDoubleFromUser("Weight (kg): ");

                newWorkout.addExercise(new WeightExercise(name, muscle, sets, reps, weight));
                System.out.println("Exercise added!");

            } else if (exChoice.equals("2")) {
                System.out.print("Cardio Name (e.g., Treadmill): ");
                String name = scanner.nextLine();

                System.out.print("Target (e.g., General): ");
                String muscle = scanner.nextLine();

                // Güvenli metotlara geçirildi
                int duration = readIntFromUser("Duration (minutes): ");
                double distance = readDoubleFromUser("Distance (km): ");

                newWorkout.addExercise(new CardioExercise(name, muscle, duration, distance));
                System.out.println("Cardio added!");

            } else if (exChoice.equals("3")) {
                addingExercises = false;
            } else {
                System.out.println("Invalid choice!");
            }
        }

        user.saveWorkout(newWorkout);
    }
}

abstract class Exercise {
    String exerciseName;
    String targetMuscleGroup;

    public Exercise(String exerciseName, String targetMuscleGroup) {
        this.exerciseName = exerciseName;
        this.targetMuscleGroup = targetMuscleGroup;
    }

    public abstract void printDetails();
}

class WeightExercise extends Exercise {
    int sets;
    int reps;
    double weightKg;

    public WeightExercise(String exerciseName, String targetMuscleGroup, int sets, int reps, double weightKg) {
        super(exerciseName, targetMuscleGroup);
        this.weightKg = weightKg;
        this.sets = sets;
        this.reps = reps;
    }

    @Override
    public void printDetails() {
        System.out.println("- " + exerciseName + " [" + targetMuscleGroup + "] -> "
                + sets + " Sets x " + reps + " Reps (" + weightKg + " kg)");
    }
}

class CardioExercise extends Exercise {
    int durationMinutes;
    double distanceKm;

    public CardioExercise(String exerciseName, String targetMuscleGroup, int durationMinutes, double distanceKm) {
        super(exerciseName, targetMuscleGroup);
        this.distanceKm = distanceKm;
        this.durationMinutes = durationMinutes;
    }

    @Override
    public void printDetails() {
        System.out.println("- " + exerciseName + " [" + targetMuscleGroup + "] -> "
                + durationMinutes + " mins, " + distanceKm + " km");
    }
}

enum WorkoutType {
    PUSH, PULL, LEGS, FULL_BODY, CARDIO
}

class WorkoutDay {
    LocalDate date;
    WorkoutType type;
    List<Exercise> exercisesPerformed;

    public WorkoutDay(LocalDate date, WorkoutType type) {
        this.date = date;
        this.type = type;
        this.exercisesPerformed = new ArrayList<>();
    }

    public void addExercise(Exercise exercise) {
        this.exercisesPerformed.add(exercise);
    }

    public void printWorkoutSummary() {
        System.out.println("\n=== " + this.date + " | " + this.type + " DAY ===");
        if (exercisesPerformed.isEmpty()) {
            System.out.println("No exercises added to this workout yet.");
            return;
        }
        for (Exercise e : exercisesPerformed) {
            e.printDetails();
        }
    }
}

class UserProfile {
    String name;
    int heightCm;
    double weightKg;
    List<WorkoutDay> workoutHistory;

    public UserProfile(String name, int heightCm, double weightKg) {
        this.heightCm = heightCm;
        this.name = name;
        this.weightKg = weightKg;
        this.workoutHistory = new ArrayList<>();
    }

    public void saveWorkout(WorkoutDay day) {
        workoutHistory.add(day);
        System.out.println("Workout on " + day.date + " has been saved to history.");
    }

    public void printFullHistory() {
        System.out.println("\n=== COMPLETE HISTORY FOR: " + name.toUpperCase() + " ===");
        System.out.println("Physical Stats: " + heightCm + " cm | " + weightKg + " kg");
        for (WorkoutDay day : workoutHistory) {
            day.printWorkoutSummary();
        }
    }
}

class DataManager {
    static final String FILE_NAME = "workout_data.txt";

    public static void saveData(UserProfile profile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write("PROFILE," + profile.name + "," + profile.heightCm + "," + profile.weightKg);
            writer.newLine();

            for (WorkoutDay day : profile.workoutHistory) {
                writer.write("WORKOUT," + day.date + "," + day.type);
                writer.newLine();

                for (Exercise e : day.exercisesPerformed) {
                    if (e instanceof WeightExercise) {
                        WeightExercise w = (WeightExercise) e;
                        writer.write("WEIGHT," + w.exerciseName + "," + w.targetMuscleGroup + ","
                                + w.sets + "," + w.reps + "," + w.weightKg);
                    } else if (e instanceof CardioExercise) {
                        CardioExercise c = (CardioExercise) e;
                        writer.write("CARDIO," + c.exerciseName + "," + c.targetMuscleGroup + ","
                                + c.durationMinutes + "," + c.distanceKm);
                    }
                    writer.newLine();
                }
            }
            System.out.println("Data successfully saved to " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to file: " + e.getMessage());
        }
    }

    public static UserProfile loadData() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("No saved data found. Creating a new profile...");
            return null;
        }

        UserProfile profile = null;
        WorkoutDay currentDay = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String tag = parts[0];

                switch (tag) {
                    case "PROFILE":
                        profile = new UserProfile(parts[1], Integer.parseInt(parts[2]), Double.parseDouble(parts[3]));
                        break;
                    case "WORKOUT":
                        if (profile != null) {
                            LocalDate date = LocalDate.parse(parts[1]);
                            WorkoutType type = WorkoutType.valueOf(parts[2]);
                            currentDay = new WorkoutDay(date, type);
                            profile.workoutHistory.add(currentDay);
                        }
                        break;
                    case "WEIGHT":
                        if (currentDay != null) {
                            Exercise weight = new WeightExercise(
                                    parts[1], parts[2],
                                    Integer.parseInt(parts[3]), Integer.parseInt(parts[4]),
                                    Double.parseDouble(parts[5])
                            );
                            currentDay.addExercise(weight);
                        }
                        break;
                    case "CARDIO":
                        if (currentDay != null) {
                            Exercise cardio = new CardioExercise(
                                    parts[1], parts[2],
                                    Integer.parseInt(parts[3]), Double.parseDouble(parts[4])
                            );
                            currentDay.addExercise(cardio);
                        }
                        break;
                }
            }
            System.out.println("Data successfully loaded from file.");
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }
        return profile;
    }
}