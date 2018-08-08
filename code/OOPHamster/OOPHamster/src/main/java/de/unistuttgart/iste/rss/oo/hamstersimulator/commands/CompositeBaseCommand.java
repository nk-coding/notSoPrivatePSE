package de.unistuttgart.iste.rss.oo.hamstersimulator.commands;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.unistuttgart.iste.rss.oo.hamstersimulator.commands.PropertyCommandSpecification.ActionKind;

public abstract class CompositeBaseCommand implements CommandInterface {

    private List<CommandInterface> commandsToExecute = Lists.newLinkedList();
    protected CompositeCommandBuilder compositeCommandBuilder = new CompositeCommandBuilder();

    @Override
    public void execute() {
        buildBeforeFirstExecution(compositeCommandBuilder);
        commandsToExecute = ImmutableList.copyOf(compositeCommandBuilder.commandsToExecute);
        commandsToExecute.forEach(command -> command.execute());
    }

    @Override
    public void undo() {
        commandsToExecute.forEach(command -> command.undo());
    }

    protected void buildBeforeFirstExecution(final CompositeCommandBuilder builder) {};

    protected class CompositeCommandBuilder {
        private final List<CommandInterface> commandsToExecute = Lists.newLinkedList();

        public CompositeCommandBuilder add(final List<CommandInterface> commands) {
            commandsToExecute.addAll(commands);
            return this;
        }

        public CompositeCommandBuilder add(final CommandInterface ... commands ) {
            for (final CommandInterface command : commands) {
                commandsToExecute.add(command);
            }
            return this;
        }

        public <G> CompositeCommandBuilder addPropertyUpdateCommand(final PropertyMap<G> entity,
                final String propertyName,
                final Object value,
                final ActionKind action) {
            commandsToExecute.add(UnidirectionalUpdatePropertyCommand.createPropertyUpdateCommand(entity, propertyName, value, action));
            return this;
        }
    }

}
