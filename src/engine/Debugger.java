package engine;

public class Debugger {

    private final Runner runner;
    private boolean running = false;
    private int currentStep;

    public Debugger(Runner runner) {
        this.runner = runner;
        this.currentStep = 0;
    }
    public boolean isRunning() {
        return running;
    }
    public void reset() {
        runner.reset();
        this.currentStep = 0;
        this.running = false;
    }
    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getCurrentStep() {
        return currentStep;
    }
    public int getCycleCount() {
        return runner.getCycleCountdebug();
    }

    public void step() {
        if (!runner.isFinisheddebug()) {
            runner.step();
            if(runner.isFinisheddebug())
                this.running = false;
            this.currentStep=runner.getCurrIndexdebug();
        }
        else
            this.running = false;
    }



    public void resume() {
        while (!runner.isFinisheddebug()) {
            runner.step();
        }
        this.running = false;
        this.currentStep=runner.getCurrIndexdebug();
    }

}