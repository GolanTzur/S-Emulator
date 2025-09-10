package engine;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ProgramState implements Serializable {
    private static final String sourceFile = "data\\programstate\\programstate.bin";
    private final Program origin;
    private final Program copy;

    public ProgramState(Program origin, Program copy) {
        this.origin = origin;
        this.copy = copy;
    }

    public void saveProgramState() throws Exception {
        if (origin == null || copy == null) {
            throw new Exception("No Program loaded");
        }
        Path path = Paths.get(sourceFile);
        if (!path.toFile().exists()) {
            path.toFile().getParentFile().mkdirs();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static ProgramState loadProgramState() throws Exception {
        Path path = Paths.get(sourceFile);
        if (!path.toFile().exists()) {
            throw new Exception("No saved Program State found");
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return (ProgramState) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new Exception("Error loading program state", e);
        }

    }
    public Program getOrigin() {
        return origin;
    }
    public Program getCopy() {
        return copy;
    }
}

