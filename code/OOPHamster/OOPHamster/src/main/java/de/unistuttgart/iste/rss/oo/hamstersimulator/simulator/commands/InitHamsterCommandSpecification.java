package de.unistuttgart.iste.rss.oo.hamstersimulator.simulator.commands;

import java.util.Optional;

import de.unistuttgart.iste.rss.oo.hamstersimulator.commands.specification.CompositeCommandSpecification;
import de.unistuttgart.iste.rss.oo.hamstersimulator.datatypes.Direction;
import de.unistuttgart.iste.rss.oo.hamstersimulator.datatypes.Location;

public class InitHamsterCommandSpecification extends CompositeCommandSpecification {

    private final Optional<Location> location;
    private final Direction newDirection;
    private final int newGrainCount;

    public InitHamsterCommandSpecification(final Optional<Location> location, final Direction newDirection, final int newGrainCount) {
        this.location = location;
        this.newDirection = newDirection;
        this.newGrainCount = newGrainCount;
    }

    public Optional<Location> getLocation() {
        return location;
    }

    public Direction getNewDirection() {
        return newDirection;
    }

    public int getNewGrainCount() {
        return newGrainCount;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}