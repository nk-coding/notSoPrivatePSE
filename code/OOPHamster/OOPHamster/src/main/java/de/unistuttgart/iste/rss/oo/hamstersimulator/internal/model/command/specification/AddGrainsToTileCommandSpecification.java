package de.unistuttgart.iste.rss.oo.hamstersimulator.internal.model.command.specification;

import de.unistuttgart.iste.rss.oo.hamstersimulator.commands.specification.CommandSpecification;
import de.unistuttgart.iste.rss.oo.hamstersimulator.datatypes.Location;

public final class AddGrainsToTileCommandSpecification extends AbstractTerritoryTileCommandSpecification implements CommandSpecification {

    private final int amount;

    public AddGrainsToTileCommandSpecification(final Location location, final int amount) {
        super(location);
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void visit(final SpecificationVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}