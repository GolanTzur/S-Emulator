package engine;

import engine.basictypes.Variable;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

public class Statistics implements Serializable {
    private static String sourceFile="src\\statistics\\statistics.bin";
    private static int runCounter = 0;
    private int degree;
    private Collection<Variable> variables;
    private int result;
    private int cycles;
    private int id;
    public Statistics(int degree,Collection<Variable> variables,int cycles,int result) {
        this.degree = degree;
        this.variables = variables;
        this.result = result;
        this.cycles = cycles;
        this.id=runCounter++;
    }
    public void writeStatistics() throws IOException {
        Path path = Paths.get(sourceFile);
        if (!path.toFile().exists()) {
            path.toFile().getParentFile().mkdirs();
            path.toFile().createNewFile();
        }
        else
        {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()));
            oos.writeObject(this);
            oos.close();
        }

    }

    public static ArrayList<Statistics> loadStatisticsIndividually() throws IOException, ClassNotFoundException {
        ArrayList<Statistics> stats = new ArrayList<>();
        Path path = Paths.get(sourceFile);
        if (!path.toFile().exists()) {
            return stats;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            while (true) {
                try {
                    Statistics stat = (Statistics) ois.readObject();
                    stats.add(stat);
                } catch (EOFException e) {
                    break;
                }
            }
        }
        return stats;
    }
    public static int getRunCounter() {
        return runCounter;
    }
    public static void reset()
    {
        runCounter=0;
        Path path = Paths.get(sourceFile);
        if (path.toFile().exists()) {
            path.toFile().delete();
        }
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Run #").append(id).append("\n");
        sb.append("Degree: ").append(degree).append("\n");
        sb.append("Variables: ");
        for (Variable var : variables) {
            sb.append(var.toString()).append("=");
            sb.append(var.getValue()).append(" ");
        }
        sb.append("\n");
        sb.append("Result: ").append(result).append("\n");
        sb.append("Cycles: ").append(cycles).append("\n");
        return sb.toString();
    }

}
