package de.unistuttgart.iste.rss.oo.hamstersimulator.internal.model;

import de.unistuttgart.iste.rss.oo.hamstersimulator.commands.UnidirectionalUpdatePropertyCommand;
import de.unistuttgart.iste.rss.oo.hamstersimulator.commands.specification.UnidirectionalUpdatePropertyCommandSpecification.ActionKind;
import de.unistuttgart.iste.rss.oo.hamstersimulator.internal.model.command.specification.AddWallToTileCommandSpecification;

public class AddWallToTileCommand extends AbstractTerritoryCompositeBaseCommand<AddWallToTileCommandSpecification> {

    public AddWallToTileCommand(final Territory territory, final AddWallToTileCommandSpecification spec) {
        super(territory, spec);
    }

    @Override
    protected void buildBeforeFirstExecution(final CompositeCommandBuilder builder) {
        final Tile tile = this.territory.getTileAt(this.specification.getLocation());
        final TileContent newWall = new Wall();
        this.compositeCommandBuilder.add(UnidirectionalUpdatePropertyCommand.createPropertyUpdateCommand(tile.content, newWall, ActionKind.ADD));
        this.compositeCommandBuilder.add(UnidirectionalUpdatePropertyCommand.createPropertyUpdateCommand(newWall.currentTile, tile, ActionKind.SET));
    }

}
