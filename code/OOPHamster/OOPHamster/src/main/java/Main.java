import java.io.IOException;

import de.unistuttgart.iste.rss.oo.hamstersimulator.commands.Command;
import de.unistuttgart.iste.rss.oo.hamstersimulator.commands.EditCommandStack;
import de.unistuttgart.iste.rss.oo.hamstersimulator.commands.GameCommandStack;
import de.unistuttgart.iste.rss.oo.hamstersimulator.datatypes.Direction;
import de.unistuttgart.iste.rss.oo.hamstersimulator.datatypes.Location;
import de.unistuttgart.iste.rss.oo.hamstersimulator.external.model.Hamster;
import de.unistuttgart.iste.rss.oo.hamstersimulator.external.model.Territory;
import de.unistuttgart.iste.rss.oo.hamstersimulator.ui.javafx.JavaFXUI;

public class Main {

    public static void main(final String[] args) throws IOException, CloneNotSupportedException {
        JavaFXUI.start();

        final String territoryFile = "territories/example01.ter";
        final EditCommandStack<Command> editStack = new EditCommandStack<>();
        final GameCommandStack<Command> gameStack = new GameCommandStack<>();

        final Territory editTerritory = new Territory(editStack);
        final Territory gameTerritory = new Territory(gameStack);

        gameTerritory.showUI();
        editTerritory.loadFromFile(territoryFile);
        editTerritory.cloneInto(gameTerritory);
        gameTerritory.runGame(territory -> Main.exampleRun(territory));
        gameTerritory.reset();
    }

    private static void exampleRun(final Territory territory) {
        final Hamster paule = territory.getDefaultHamster();
        final Hamster willi = new Hamster(territory, Location.from(1, 3), Direction.WEST, 0);
        final Hamster marry = new Hamster(territory, Location.from(1, 2), Direction.EAST, 0);

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
