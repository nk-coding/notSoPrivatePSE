package de.unistuttgart.iste.rss.oo.hamstersimulator.internal.model;

public class Wall extends TileContent {

    @Override
    public String toString() {
        return "Wall";
    }

    @Override
    protected boolean blocksEntrance() {
        return true;
    }

}