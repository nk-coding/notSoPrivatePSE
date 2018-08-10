import java.io.IOException;

import de.unistuttgart.iste.rss.oo.hamstersimulator.commands.Command;
import de.unistuttgart.iste.rss.oo.hamstersimulator.commands.EditCommandStack;
import de.unistuttgart.iste.rss.oo.hamstersimulator.commands.GameCommandStack;
import de.unistuttgart.iste.rss.oo.hamstersimulator.datatypes.Direction;
import de.unistuttgart.iste.rss.oo.hamstersimulator.datatypes.Location;
import de.unistuttgart.iste.rss.oo.hamstersimulator.external.model.GameHamster;
import de.unistuttgart.iste.rss.oo.hamstersimulator.internal.model.Territory;
import de.unistuttgart.iste.rss.oo.hamstersimulator.internal.model.TerritoryLoader;
import de.unistuttgart.iste.rss.oo.hamstersimulator.ui.javafx.JavaFXUI;

public class Main {

    public static void main(final String[] args) throws IOException, CloneNotSupportedException {
        JavaFXUI.start();

        final String territoryFile = "territories/example01.ter";
        final EditCommandStack<Command> editStack = new EditCommandStack<>();
        final GameCommandStack<Command> gameStack = new GameCommandStack<>();
        final Territory territory = new Territory(editStack);
        final Territory t2 = new Territory(gameStack);

        JavaFXUI.getSingleton().init(t2);

        TerritoryLoader.initializeFor(territory).loadFromFile(territoryFile);

        t2.getCommandStack().execute(editStack.cloneCommandsInto(t2));


        delay(1000);
        gameStack.startGame();
        exampleRun(t2);
        gameStack.stopGame();

        delay(1000);
        t2.getCommandStack().undoAll();
    }

    protected static void delay(final int delay) {
        try {
            Thread.sleep(delay);
        } catch (final InterruptedException e) { }
    }

    private static void exampleRun(final Territory territory) {
        final GameHamster paule = GameHamster.fromInternalHamster(territory.getDefaultHamster());
        final GameHamster willi = new GameHamster(territory, Location.from(1, 3), Direction.WEST, 0);
        final GameHamster marry = new GameHamster(territory, Location.from(1, 2), Direction.EAST, 0);

        while (!paule.grainAvailable() && paule.frontIsClear()) {
            paule.move();
        }
        while (paule.grainAvailable()) {
            paule.pickGrain();
        }
        paule.turnLeft();
        paule.turnLeft();
        while (paule.frontIsClear()) {
            paule.move();
        }
        while (!paule.mouthEmpty()) {
            paule.putGrain();
        }
        willi.move();
        marry.move();
    }
}
