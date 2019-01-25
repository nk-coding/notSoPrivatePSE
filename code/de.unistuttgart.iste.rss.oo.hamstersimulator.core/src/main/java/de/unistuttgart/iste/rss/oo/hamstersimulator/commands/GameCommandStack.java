package de.unistuttgart.iste.rss.oo.hamstersimulator.commands;

import static de.unistuttgart.iste.rss.utils.Preconditions.checkState;

import java.util.concurrent.Semaphore;

import de.unistuttgart.iste.rss.oo.hamstersimulator.exceptions.GameAbortedException;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;

public class GameCommandStack extends CommandStack {

    public enum Mode {
        RUNNING, INITIALIZING, STOPPED, PAUSED
    }

    private final ReadOnlyObjectWrapper<Mode> state = new ReadOnlyObjectWrapper<Mode>(this, "state", Mode.INITIALIZING);
    protected final SimpleDoubleProperty speed = new SimpleDoubleProperty(this, "speed", 4.0);

    private final Semaphore pauseLock = new Semaphore(1, true);

    private Thread executingThread;

    public void startGame(final boolean startPaused) {
        this.executedCommands.clear();
        this.canUndo.set(false);
        this.canRedo.set(false);
        if (startPaused) {
            pause();
        } else {
            state.set(Mode.RUNNING);
        }
    }

    public void delay() {
        try {
            Thread.sleep((int)((11.0d-this.speed.get())/5.0d * 400.0d));
        } catch (final InterruptedException e) {
        }
    }

    public void setSpeed(final double speed) {
        assert speed > 0 && speed <= 10;
        this.speed.set(speed);
    }

    @Override
    public void execute(final Command command) {
        if (this.state.get() != Mode.RUNNING && this.state.get() != Mode.PAUSED) {
            throw new GameAbortedException("The game needs to be running to execute hamster commands");
        }
        try {
            this.executingThread = Thread.currentThread();
            checkState(!(state.get() == Mode.STOPPED));
            pauseLock.acquire();
            super.execute(command);
            pauseLock.release();
            delay();
        } catch (final InterruptedException e) {
        }
    }

    public void reset() {
        state.set(Mode.INITIALIZING);
        interruptWaitingThreads();
    }

    private void interruptWaitingThreads() {
        if (this.pauseLock.hasQueuedThreads()) {
            this.executingThread.interrupt();
        }
    }

    public void stopGame() {
        state.set(Mode.STOPPED);
        if (pauseLock.availablePermits() == 0) {
            pauseLock.release();
        }
        interruptWaitingThreads();
    }

    public void pause() {
        assert state.get() != Mode.PAUSED;

        state.set(Mode.PAUSED);
        new Thread(() -> {
            try {
                this.pauseLock.acquire();
            } catch (final InterruptedException e) {
            }
        }).start();
    }

    public void resume() {
        assert this.pauseLock.availablePermits() == 0;

        state.set(Mode.RUNNING);
        this.pauseLock.release();
    }

    public ReadOnlyObjectProperty<Mode> stateProperty() {
        return this.state.getReadOnlyProperty();
    }

    public DoubleProperty speedProperty() {
        return this.speed;
    }

}
