package de.unistuttgart.iste.rss.oo.hamstersimulator.hamster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import de.unistuttgart.iste.rss.oo.hamstersimulator.HamsterSimulator;
import de.unistuttgart.iste.rss.oo.hamstersimulator.datatypes.Direction;
import de.unistuttgart.iste.rss.oo.hamstersimulator.datatypes.Location;
import de.unistuttgart.iste.rss.oo.hamstersimulator.datatypes.LocationVector;
import de.unistuttgart.iste.rss.oo.hamstersimulator.hamster.commands.InitDefaultHamsterCommand;
import de.unistuttgart.iste.rss.oo.hamstersimulator.hamster.commands.MoveCommand;
import de.unistuttgart.iste.rss.oo.hamstersimulator.hamster.commands.PickGrainCommand;
import de.unistuttgart.iste.rss.oo.hamstersimulator.hamster.commands.PutGrainCommand;
import de.unistuttgart.iste.rss.oo.hamstersimulator.hamster.commands.TurnLeftCommand;
import de.unistuttgart.iste.rss.oo.hamstersimulator.hamster.events.HamsterChangedDirectionEvent;
import de.unistuttgart.iste.rss.oo.hamstersimulator.hamster.events.HamsterCreatedEvent;
import de.unistuttgart.iste.rss.oo.hamstersimulator.hamster.events.HamsterCreatedListener;
import de.unistuttgart.iste.rss.oo.hamstersimulator.hamster.events.HamsterGrainAddedEvent;
import de.unistuttgart.iste.rss.oo.hamstersimulator.hamster.events.HamsterGrainDeletedEvent;
import de.unistuttgart.iste.rss.oo.hamstersimulator.hamster.events.HamsterMovedEvent;
import de.unistuttgart.iste.rss.oo.hamstersimulator.hamster.events.HamsterStateChangedEvent;
import de.unistuttgart.iste.rss.oo.hamstersimulator.hamster.events.HamsterStateListener;
import de.unistuttgart.iste.rss.oo.hamstersimulator.territory.Grain;
import de.unistuttgart.iste.rss.oo.hamstersimulator.territory.Territory;
import de.unistuttgart.iste.rss.oo.hamstersimulator.territory.Tile;
import de.unistuttgart.iste.rss.oo.hamstersimulator.territory.TileContent;

public class Hamster extends TileContent {

    private Optional<Tile> currentTile = Optional.empty();
    private Direction direction;
    private final List<Grain> grainInMouth;
    private final List<HamsterStateListener> stateListener = new LinkedList<>();
    private static final List<HamsterCreatedListener> creationListener = new LinkedList<>();
    private final HamsterStateChanger hamsterStateAccess = new HamsterStateChanger();
    private final HamsterSimulator simulator;

    /*
     * Constructors
     */
    // TODO: Fixme, make private by using a creational pattern
    public Hamster(final HamsterSimulator simulator, final Optional<Tile> initialTile, final Direction direction, final int grainInMouth) {
        super();

        assert initialTile != null;

        this.simulator = simulator;
        this.grainInMouth = new LinkedList<>();
        notifyHamsterCreated(this);
        init(initialTile, direction, grainInMouth);
    }

    public Hamster(final HamsterSimulator simulator) {
        this(simulator,Optional.empty(),Direction.NORTH,0);
    }

    public void init(final Optional<Tile> initialTile, final Direction direction, final int grainInMouth) {
        this.simulator.getCommandStack().execute(new InitDefaultHamsterCommand(hamsterStateAccess, initialTile, direction, grainInMouth));
    }

    Hamster(final HamsterSimulator simulator, final Optional<Tile> initialTile, final Direction direction) {
        this(simulator, initialTile, direction, 0);
    }

    Hamster(final HamsterSimulator simulator, final Optional<Tile> initialTile) {
        this(simulator, initialTile, Direction.NORTH, 0);
    }

    /*
     * State changers
     */
    public class HamsterStateChanger {

        public Hamster getHamster() {
            return Hamster.this;
        }

        public void setDirection(final Direction direction) {
            final Direction oldDirection = Hamster.this.direction;
            Hamster.this.direction = direction;
            fireStateChangedEvent(new HamsterChangedDirectionEvent(Hamster.this, oldDirection, Hamster.this.direction));
        }

        public void addGrainToMouth(final Grain newGrain) {
            assert !Hamster.this.getGrainInMouth().contains(newGrain);

            Hamster.this.grainInMouth.add(newGrain);
            fireStateChangedEvent(new HamsterGrainAddedEvent(Hamster.this, newGrain));
        }

        public void removeGrainFromMouth(final Grain grainToRemove) {
            assert Hamster.this.grainInMouth.contains(grainToRemove);

            Hamster.this.grainInMouth.remove(grainToRemove);
            fireStateChangedEvent(new HamsterGrainDeletedEvent(Hamster.this, grainToRemove));
        }

        public Grain getAnyGrain() {
            assert Hamster.this.grainInMouth.size() > 0;
            return Hamster.this.grainInMouth.get(0);
        }

        public void setCurrentTile(final Optional<Tile> newTile) {
            assert newTile != null;
            assert !Hamster.this.currentTile.isPresent() || Hamster.this.currentTile.get().hasObjectInContent(Hamster.this);
            assert !newTile.isPresent() || newTile.get().canEnter();

            final Optional<Tile> oldTile = Hamster.this.currentTile;
            Hamster.this.currentTile = newTile;

            fireStateChangedEvent(new HamsterMovedEvent(Hamster.this, oldTile, Hamster.this.currentTile));
        }
    }
    /*
     * Commands
     */
    public void move() {
        this.simulator.getCommandStack().execute(new MoveCommand(hamsterStateAccess));
    }

    public void turnLeft() {
        this.simulator.getCommandStack().execute(new TurnLeftCommand(hamsterStateAccess));
    }

    public void pickGrain() {
        this.simulator.getCommandStack().execute(new PickGrainCommand(hamsterStateAccess));
    }

    public void putGrain() {
        this.simulator.getCommandStack().execute(new PutGrainCommand(hamsterStateAccess));
    }

    public void readNumber() {
        // TODO - implement Hamster.readNumber
        throw new UnsupportedOperationException();
    }

    public void readString() {
        // TODO - implement Hamster.readString
        throw new UnsupportedOperationException();
    }

    public void write() {
        // TODO - implement Hamster.write
        throw new UnsupportedOperationException();
    }

    /*
     * Queries
     */
    public boolean frontIsClear() {
        final LocationVector movementVector = this.getDirection().getMovementVector();
        final Location potentialNewLocation = this.getCurrentTile().orElseThrow(IllegalStateException::new).getLocation().translate(movementVector);
        return this.currentTile.get().getTerritory().getTileAt(potentialNewLocation).canEnter();
    }

    public boolean grainAvailable() {
        return this.getCurrentTile().orElseThrow(IllegalStateException::new).countObjectsOfType(Grain.class) > 0;
    }

    public boolean mouthEmpty() {
        return this.getGrainInMouth().isEmpty();
    }

    public Optional<Tile> getCurrentTile() {
        return currentTile;
    }

    public Territory getCurrentTerritory() {
        return currentTile.orElseThrow(IllegalStateException::new).getTerritory();
    }

    public Direction getDirection() {
        return direction;
    }

    public List<Grain> getGrainInMouth() {
        return Collections.unmodifiableList(grainInMouth);
    }

    /*
     * OO-Design Methods
     */
    public void addHamsterStateListener(final HamsterStateListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("No listener object specified");
        }
        this.stateListener.add(listener);
    }

    public void removeHamsterStateListener(final HamsterStateListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("No listener object specified");
        }
        this.stateListener.remove(listener);
    }

    @Override
    public String toString() {
        return "Hamster";
    }

    @Override
    protected boolean blocksEntrance() {
        return false;
    }

    private void fireStateChangedEvent(final HamsterStateChangedEvent event) {
        for (final HamsterStateListener listener : new ArrayList<>(stateListener)) {
            listener.onStateChanged(event);
        }
    }

    public static void addHamsterCreatedListener(final HamsterCreatedListener listener) {
        creationListener.add(listener);
    }

    public static void removeHamsterCreatedListener(final HamsterCreatedListener listener) {
        creationListener.remove(listener);
    }

    private static void notifyHamsterCreated(final Hamster hamster) {
        for (final HamsterCreatedListener listener : creationListener) {
            listener.hamsterCreated(new HamsterCreatedEvent(hamster));
        }
    }

}