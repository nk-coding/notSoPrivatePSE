package de.unistuttgart.iste.rss.oo.hamstersimulator.simulator.commands;

import de.unistuttgart.iste.rss.oo.hamstersimulator.commands.PropertyCommandSpecification;
import de.unistuttgart.iste.rss.oo.hamstersimulator.commands.PropertyCommandSpecification.ActionKind;
import de.unistuttgart.iste.rss.oo.hamstersimulator.commands.PropertyMap;
import de.unistuttgart.iste.rss.oo.hamstersimulator.commands.UnidirectionalUpdatePropertyCommand;
import de.unistuttgart.iste.rss.oo.hamstersimulator.hamster.Hamster;
import de.unistuttgart.iste.rss.oo.hamstersimulator.hamster.commands.DropGrainCommand;
import de.unistuttgart.iste.rss.oo.hamstersimulator.territory.tile.Grain;
import de.unistuttgart.iste.rss.oo.hamstersimulator.territory.tile.Tile;

public class PutGrainCommand extends HamsterCompositeBaseCommand {

    private Grain grainDropped;
    private Tile currentTile;

    public PutGrainCommand(final PropertyMap<Hamster> hamsterState) {
        super(hamsterState);
    }

    @Override
    protected void buildBeforeFirstExecution(final CompositeCommandBuilder builder) {
        assert this.hamster.getCurrentTile().isPresent();
        this.currentTile = this.hamster.getCurrentTerritory().getTileAt(this.hamster.getCurrentTile().get().getLocation());
        this.grainDropped = this.hamster.getGrainInMouth().get(0);
        builder.add(
                new DropGrainCommand(hamsterState, this.grainDropped),
                new UnidirectionalUpdatePropertyCommand<Tile>(this.currentTile.getState(), new PropertyCommandSpecification("content", this.grainDropped, ActionKind.ADD))
                );
    }
}
