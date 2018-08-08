package de.unistuttgart.iste.rss.oo.hamstersimulator.territory;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import de.unistuttgart.iste.rss.oo.hamstersimulator.datatypes.Direction;
import de.unistuttgart.iste.rss.oo.hamstersimulator.datatypes.Location;

public class TerritoryLoader {

    private final TerritoryBuilder territoryBuilder;
    private Dimension loadedTerritoryDimensions;

    private TerritoryLoader(final TerritoryBuilder territoryBuilder) {
        super();
        this.territoryBuilder = territoryBuilder;
    }

    public static TerritoryLoader initializeFor(final Territory territory) {
        return new TerritoryLoader(territory.getTerritoryBuilder());
    }

    public void loadFromFile(final String territoryFile) {
        final List<String> list = readLinesFromTerritoryFile(territoryFile);
        final String[] lines = list.toArray(new String[]{});
        setSizeFromStrings(lines);
        final String[] territoryDefinition = Arrays.copyOfRange(lines,2,lines.length);
        buildTiles(territoryDefinition);

        this.territoryBuilder.build();
    }

    private void setSizeFromStrings(final String[] lines) {
        this.loadedTerritoryDimensions = new Dimension(Integer.parseInt(lines[0]), Integer.parseInt(lines[1]));
        this.territoryBuilder.initializeTerritory(this.loadedTerritoryDimensions);
    }

    private void buildTiles(final String[] lines) {
        final LinkedList<Location> grainLocations = new LinkedList<Location>();
        Optional<Location> defaultHamsterLocation = Optional.empty();
        Optional<Direction> defaultHamsterDirection = Optional.empty();
        for (int row = 0; row < this.loadedTerritoryDimensions.height; row++) {
            for (int column = 0; column < this.loadedTerritoryDimensions.width; column++) {
                final Location currentLocation = new Location(row,column);
                final char tileCode = lines[row].charAt(column);
                createTileAt(currentLocation, tileCode);
                switch (tileCode) {
                case ' ':
                case '#':
                    break;
                case '*':
                    grainLocations.add(currentLocation);
                    break;
                case '^':
                    grainLocations.add(currentLocation);
                    defaultHamsterLocation = Optional.of(currentLocation);
                    defaultHamsterDirection = Optional.of(Direction.NORTH);
                    break;
                case '>':
                    grainLocations.add(currentLocation);
                    defaultHamsterLocation = Optional.of(currentLocation);
                    defaultHamsterDirection = Optional.of(Direction.EAST);
                    break;
                case 'v':
                    grainLocations.add(currentLocation);
                    defaultHamsterLocation = Optional.of(currentLocation);
                    defaultHamsterDirection = Optional.of(Direction.SOUTH);
                    break;
                case '<':
                    grainLocations.add(currentLocation);
                    defaultHamsterLocation = Optional.of(currentLocation);
                    defaultHamsterDirection = Optional.of(Direction.WEST);
                    break;
                default:
                    throw new RuntimeException("Territory error.");
                }
            }
        }
        final int initialGrainCount = Integer.parseInt(lines[this.loadedTerritoryDimensions.height + grainLocations.size()]);
        territoryBuilder.defaultHamsterAt(Optional.of(defaultHamsterLocation.get()), defaultHamsterDirection.get(), initialGrainCount);
        placeGrain(lines, grainLocations);
    }

    private void createTileAt(final Location currentLocation, final char tileCode) {
        if (tileCode == '#') {
            this.territoryBuilder.wallAt(currentLocation);
        }
    }

    private List<String> readLinesFromTerritoryFile(final String territoryFileName) {
        final File file = new File(territoryFileName);
        final List<String> list = new ArrayList<String>();

        try ( Scanner input = new Scanner(file) )
        {
            while (input.hasNextLine()) {
                list.add(input.nextLine());
            }
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    private void placeGrain(final String[] lines, final LinkedList<Location> grainLocations) {
        for (int i = 0; i < grainLocations.size(); i++) {
            final Location location = grainLocations.get(i);
            final int count = Integer.parseInt(lines[this.loadedTerritoryDimensions.height + i]);
            territoryBuilder.grainAt(location, count);
        }
    }

}
