package de.unistuttgart.iste.rss.oo.hamstersimulator.internal.model.hamster.command.specification;

import de.unistuttgart.iste.rss.oo.hamstersimulator.commands.CommandSpecification;
import de.unistuttgart.iste.rss.oo.hamstersimulator.datatypes.Direction;
import de.unistuttgart.iste.rss.oo.hamstersimulator.datatypes.Location;
import de.unistuttgart.iste.rss.oo.hamstersimulator.internal.model.territory.ReadOnlyTerritory;

public final class InitHamsterCommandSpecification implements CommandSpecification {

    private final ReadOnlyTerritory territory;
    private final Location location;
    private final Direction newDirection;
    private final int newGrainCount;

    public InitHamsterCommandSpecification(final ReadOnlyTerritory territory, final Location location, final Direction newDirection, final int newGrainCount) {
        super();
        this.territory = territory;
        this.location = location;
        this.newDirection = newDirection;
        this.newGrainCount = newGrainCount;
    }

    public Location getLocation() {
        return location;
    }

    public Direction getNewDirection() {
        return newDirection;
    }

    public int getNewGrainCount() {
        return newGrainCount;
    }

    public ReadOnlyTerritory getTerritory() {
        return territory;
    }
}