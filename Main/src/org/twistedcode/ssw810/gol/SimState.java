package org.twistedcode.ssw810.gol;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: tracyde
 * Date: 8/6/12
 * Time: 11:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimState implements Serializable {
    private LifeBoard lifeBoard;
    private long generation;
    private final static String fileName = "GOL.dat";

    public long getGeneration() {
        return generation;
    }

    public void setGeneration(long generation) {
        this.generation = generation;
    }

    public SimState(LifeBoard lifeBoard, long generation) {
        this.lifeBoard = lifeBoard;
        this.generation = generation;

    }

    public LifeBoard getLifeBoard() {
        return this.lifeBoard;
    }
}
