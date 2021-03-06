package de.unistuttgart.iste.rss.oo.hamstersimulator.internal.model.territory.command.specification;

import de.unistuttgart.iste.rss.oo.hamstersimulator.commands.CommandSpecification;
import de.unistuttgart.iste.rss.oo.hamstersimulator.datatypes.Location;

public final class ClearTileCommandSpecification extends AbstractTerritoryTileCommandSpecification implements CommandSpecification {

    public ClearTileCommandSpecification(final Location location) {
        super(location);
    }
}
