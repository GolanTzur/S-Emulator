package console;

import engine.*;
import engine.basictypes.Variable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Scanner;

public class ConsoleUI {
    public ConsoleUI() {
        Program program=null;
        Program programCopy = null;
        XMLHandler xmlHandler = XMLHandler.getInstance();
        LinkedList<Statistics> currentStats=null;
        System.out.println("Welcome to S-Emulator IDE");
        boolean exit = false;

        while (!exit) {
            Menu();
            int choice;
            try {
                choice = new Scanner(System.in).nextInt();
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    try {
                        System.out.print("Enter XML file path: ");
                        String filePath = new BufferedReader(new InputStreamReader(System.in)).readLine();
                        program = xmlHandler.loadProgram(filePath);
                        program.checkValidity();
                        programCopy = program.clone();
                        System.out.println("Program loaded successfully.");
                        //Statistics.reset();
                        currentStats= new LinkedList<>();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 2:
                    if(program==null){
                        System.out.println("No program loaded. Please load a program first.");
                        break;
                    }
                    System.out.print(program);
                    break;
                case 3:
                    if(program==null){
                        System.out.println("No program loaded. Please load a program first.");
                        break;
                    }
                    int expansionsAvailable = program.getProgramDegree();
                    int expansionsRequested=-1;
                    do {
                        try {
                            System.out.println("Enter expansion degree, Max is: " + expansionsAvailable);
                            expansionsRequested = new Scanner(System.in).nextInt();
                        } catch (Exception e) {
                            System.out.println("Invalid input. Please enter a number.");
                            expansionsRequested=-1;
                        }
                    } while(expansionsRequested <0 || expansionsRequested > expansionsAvailable);
                    int diffDegrees=program.getProgramDegree()-programCopy.getProgramDegree();

                    if(expansionsRequested != diffDegrees){ // If the degree has changed, re-deploy the program
                        programCopy= program.clone();
                        programCopy.deployToDegree(expansionsRequested);
                    }
                    System.out.println("After Expansion: ");
                    System.out.println(programCopy);
                    break;
                case 4:
                    if(program==null){
                        System.out.println("No program loaded. Please load a program first.");
                        break;
                    }
                    int degree = program.getProgramDegree();
                    System.out.println("Available Degrees: " + degree);
                    int selectedDegree=-1;
                    do {
                        try {
                            System.out.println("Select Expansion Degree: ");
                            selectedDegree = new Scanner(System.in).nextInt();
                        }catch (Exception e) {
                            System.out.println("Invalid input. Please enter a number.");
                        }
                    } while(selectedDegree < 0  || selectedDegree > degree);

                    diffDegrees=program.getProgramDegree()-programCopy.getProgramDegree();
                    if(selectedDegree != diffDegrees){ // If the degree has changed, re-deploy the program
                        programCopy= program.clone();
                        programCopy.deployToDegree(selectedDegree);
                    }
                    Collection<Variable>initVars= programCopy.setUserInput();
                    programCopy.execute();
                    ProgramVars results= programCopy.getVars().clone();
                    int cycles= programCopy.getCycleCount();

                    try {
                        currentStats.add(new Statistics(selectedDegree,initVars,cycles,results));
                    }catch (Exception e) {
                        System.out.println("Error writing statistics: " + e.getMessage());
                    }
                    finally {
                        System.out.println("After Execution: ");
                        System.out.println(programCopy);
                        System.out.println(programCopy.getVars());
                        System.out.println("Total Cycles: " + cycles);
                    }
                    break;
                case 5:
                    try {
                        //Collection<Statistics> programStats = Statistics.loadStatisticsIndividually();
                        for(Statistics stats: currentStats){
                            System.out.println(stats);
                        }
                    } catch (Exception e) {
                        System.out.println("No Statistics Loaded " + e.getMessage());
                    }
                    break;
                case 6:
                    exit = true;
                    System.out.println("Exiting S-Emulator IDE. Goodbye!");
                    break;
                case 7:
                    ProgramState state=new ProgramState(program,programCopy);
                    try {
                        state.saveProgramState();
                        System.out.println("Program state saved successfully.");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 8:
                    try {
                        ProgramState toLoad=ProgramState.loadProgramState();
                        program=toLoad.getOrigin();
                        programCopy=toLoad.getCopy();
                        //Statistics.reset();
                        System.out.println("Program state loaded successfully");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public static void main(String[] args) {
        new ConsoleUI();
    }

    public static void Menu() {
        System.out.println("1. Load Program from XML");
        System.out.println("2. Display Program");
        System.out.println("3. Expand Program");
        System.out.println("4. Run Program");
        System.out.println("5. Show History and Statistics");
        System.out.println("6. Exit");
        System.out.println("7. Save Current Program State");
        System.out.println("8. Load Program State");
    }
}
