package de.unistuttgart.iste.rss.oo.hamstersimulator.external.model;

import de.unistuttgart.iste.rss.oo.hamstersimulator.commands.AbstractCompositeCommand;
import de.unistuttgart.iste.rss.oo.hamstersimulator.commands.UnidirectionalUpdatePropertyCommand;
import de.unistuttgart.iste.rss.oo.hamstersimulator.commands.specification.SpecifiedCommand;
import de.unistuttgart.iste.rss.oo.hamstersimulator.commands.specification.UnidirectionalUpdatePropertyCommandSpecification.ActionKind;
import de.unistuttgart.iste.rss.oo.hamstersimulator.datatypes.LogEntry;

public class WriteCommand extends AbstractCompositeCommand implements SpecifiedCommand<WriteCommandSpecification> {

    private final HamsterGame game;
    private final WriteCommandSpecification specification;

    public WriteCommand(final HamsterGame game, final WriteCommandSpecification spec) {
        super();
        this.game = game;
        this.specification = spec;
    }

    @Override
    protected void buildBeforeFirstExecution(final CompositeCommandBuilder builder) {
        builder.add(UnidirectionalUpdatePropertyCommand.createPropertyUpdateCommand(this.game.gameLog, new LogEntry(this.specification.getMessage(),this.specification.getSender()), ActionKind.ADD));
    }

    @Override
    public WriteCommandSpecification getSpecification() {
        return this.specification;
    }

}
