package engine;

import engine.basictypes.Variable;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

public class Statistics implements Serializable {
    private static String sourceFile="data\\statistics\\statistics.bin";
    private static int runCounter = 1;
    private final int degree;

    private final Collection<Variable> variables;
    private final ProgramVars results;

    private final int cycles;
    private final int id;

    public Statistics(int degree,Collection<Variable> variables,int cycles,ProgramVars results) {
        this.degree = degree;
        this.variables = variables;
        this.results = results;
        this.cycles = cycles;
        this.id=runCounter++;
    }
    // Java
    public void appendStatistics() {
        Path path = Paths.get(sourceFile);
        Collection<Statistics> stats=null;
        if (path.toFile().exists()) {
            try  {
                stats = loadStatisticsIndividually();
            } catch (IOException e) {
                System.out.println(e);
                return;
            }
        }
        else {
            path.toFile().getParentFile().mkdirs();
            stats = new ArrayList<>();
        }
        stats.add(this);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(stats);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Collection<Statistics> loadStatisticsIndividually() throws IOException {
        Collection<Statistics> stats = new ArrayList<>();
        Path path = Paths.get(sourceFile);
        if (!path.toFile().exists()) {
            return stats;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            stats=(ArrayList<Statistics>) ois.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
        }
        return stats;
    }

    public static void reset()
    {
        runCounter=1;
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
        sb.append("Result: ").append(results.getY().getValue()).append("\n");
        sb.append("Cycles: ").append(cycles).append("\n");
        return sb.toString();
    }
    public int getId() {
        return id;
    }
    public final Collection<Variable> getVariables() {
        return variables;
    }
    public final ProgramVars getResults() {
        return results;
    }
    public int getCycles() {
            return cycles;
    }
    public int getDegree() {
        return degree;
    }

}
