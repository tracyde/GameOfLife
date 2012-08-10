package org.twistedcode.ssw810.gol;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: tracyde
 * Date: 8/3/12
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class LifeBoard extends JPanel implements MouseListener, MouseMotionListener, Serializable {

    public static final int FINITE = 0, TORUS = 1, INFINITE = 2;
    public static final String[] BOARDTYPES = {"Finite", "Torus", "Infinite"};

    private final Color backgroundColor = new Color(210, 210, 210); //Color(22, 107, 29);
    private final Color mouseCellColor = new Color(160, 160, 160);
    private final Color aliveColor = new Color(45, 45, 45);

    private int numRows, numCols, imageWidth, imageHeight, panelWidth, panelHeight;

    // In order to keep a loose hold on memory setting upper ceiling for number of rows and cols
    private int maxNumRows = 1024;
    private int maxNumCols = 1024;

    private boolean[][] grid, tempGrid;
    private boolean editable = true;
    private boolean stalled = false;
    private int boardType, mouseX, mouseY;

    private long alive;
    private long dead;
    private long born;
    private long killed;

    public void setAlive(long alive) {
        this.alive = alive;
    }

    public void setDead(long dead) {
        this.dead = dead;
    }

    public void setBorn(long born) {
        this.born = born;
    }

    public void setKilled(long killed) {
        this.killed = killed;
    }

    public long getAlive() {
        return alive;
    }

    public long getDead() {
        return dead;
    }

    public long getBorn() {
        return born;
    }

    public long getKilled() {
        return killed;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public LifeBoard(final int imageHeight, final int imageWidth, final int numRows, final int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
        this.boardType = INFINITE;

        grid = new boolean[numRows][numCols];
        tempGrid = new boolean[numRows][numCols];
        setSize(getPreferredSize());
        for (int row = numRows - 1; row >= 0; --row) {
            for (int col = numCols - 1; col >= 0; --col) {
                grid[row][col] = tempGrid[row][col] = false;
            }
        }
        addMouseListener(this);
        addMouseMotionListener(this);
        panelWidth = imageWidth * numCols;
        panelHeight = imageHeight * numRows;
//        panelWidth = this.getSize().width;
//        panelHeight = this.getSize().height;
        mouseX = mouseY = -1;
        setDoubleBuffered(true);
    }

    private int neighborCount(final int row, final int col) {
        int count = 0;
        for (int i = row - 1; i <= row + 1; ++i) {
            for (int j = col - 1; j <= col + 1; ++j) {
                if (boardType == FINITE) {
                    if (i >= 0 && i < numRows && j >= 0 && j < numCols) {
                        count += grid[i][j] ? 1 : 0;
                    }
                } else if (boardType == TORUS) {
                    int tempI = i, tempJ = j;
                    if (i == -1) {
                        tempI = numRows - 1;
                    } else if (i == numRows) {
                        tempI = 0;
                    }
                    if (j == -1) {
                        tempJ = numCols - 1;
                    } else if (j == numCols) {
                        tempJ = 0;
                    }
                    count += grid[tempI][tempJ] ? 1 : 0;
                } else if (boardType == INFINITE) {
                    if (i >= 0 && i < numRows && j >= 0 && j < numCols) {
                        count += grid[i][j] ? 1 : 0;
                    }
                } else {
                    // Placeholder
                }
            }
        }
        count -= grid[row][col] ? 1 : 0;
        return count;
    }

    public boolean isAnyCellAlive() {
        for (int row = numRows - 1; row >= 0; --row) {
            for (int col = numCols - 1; col >= 0; --col) {
                if (grid[row][col]) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isStalled() {
        return stalled;
    }

    public void makeNextGeneration() {
        boolean borderCell = false;
        for (int row = 0; row < numRows; row++) {
            if (grid[row][0] || grid[row][numCols-1]) {
                borderCell = true;
            }
        }
        for (int col = 0; col < numCols; col++) {
            if (grid[0][col] || grid[numRows-1][col]) {
                borderCell = true;
            }
        }
        if (boardType == INFINITE && borderCell) {
            int growBy = 1;
            increaseGrid(grid, growBy*2);
        }

        for (int row = numRows - 1; row >= 0; --row) {
            for (int col = numCols - 1; col >= 0; --col) {
                final int numOfNeighbors = neighborCount(row, col);
                switch (numOfNeighbors) {
                    case 2:
                        tempGrid[row][col] = grid[row][col];
                        break;
                    case 3:
                        tempGrid[row][col] = true;
                        break;
                    default:
                        tempGrid[row][col] = false;
                }
            }
        }

        if (gridsEqual(grid, tempGrid, numRows, numCols)) {
            stalled = true;
        } else {
            stalled = false;
        }

        // Reset alive and dead counters
        alive = 0;
        dead = 0;

        for (int row = numRows - 1; row >= 0; --row) {
            for (int col = numCols - 1; col >= 0; --col) {
                // Look at the change between grid and tempGrid
                // a new cell is born and removed cell is killed
                if (grid[row][col]) {
                    if (!tempGrid[row][col]) {
                        killed++;
                    }
                } else {
                    if (tempGrid[row][col]) {
                        born++;
                    }
                }

                // Determine number of alive and dead
                if (tempGrid[row][col]) {
                    alive++;
                } else {
                    dead++;
                }

                grid[row][col] = tempGrid[row][col];
            }
        }
        repaint();
    }

    public Dimension getPreferredSize() {
        return new Dimension(numCols * imageWidth + 1, numRows * imageHeight + 1);
    }

    public void setEditable(final boolean editable) {
        this.editable = editable;
    }

    public void clear() {
        alive = 0;
        dead = 0;
        born = 0;
        killed = 0;
        for (int row = numRows - 1; row >= 0; --row) {
            for (int col = numCols - 1; col >= 0; --col) {
                grid[row][col] = tempGrid[row][col] = false;
            }
        }
        repaint();
    }

    public void setRandomConfig() {
        for (int row = numRows - 1; row >= 0; --row) {
            for (int col = numCols - 1; col >= 0; --col) {
                final int number = (int)(Math.random() * 100);
                grid[row][col] = number < 20 ? true : false;
            }
        }
        repaint();
    }

    public void paint(final Graphics g) {
        g.setColor(backgroundColor);
        g.fillRect(0, 0, panelWidth, panelHeight);

        if (mouseX != -1 && mouseY != -1) {
            g.setColor(mouseCellColor);
            g.fillRect(mouseX * imageWidth, mouseY * imageHeight, imageWidth, imageHeight);
        }
        g.setColor(SystemColor.activeCaptionBorder);
        for (int row = numRows; row >= 0; --row) {
            g.drawLine(0, row * imageHeight, numCols * imageWidth, row * imageHeight);
        }
        for (int col = numCols; col >= 0; --col) {
            g.drawLine(col * imageWidth, 0, col * imageWidth, numRows * imageWidth);
        }

        for (int row = numRows - 1; row >= 0; --row) {
            for (int col = numCols - 1; col >= 0; --col) {
                if (grid[row][col]) {
                    g.setColor(aliveColor);
                    g.fillRect(col * imageWidth + 1, row * imageHeight + 1, imageWidth, imageHeight);
                }
            }
        }
    }

    public boolean[][] getGrid() {
        return grid;
    }

    public void setGrid(boolean[][] grid, int numRows, int numCols) {
        // Recreate numRows, numCols, grid, and tempGrid based off of the new size
        this.numRows = numRows;
        this.numCols = numCols;
        this.panelWidth = this.imageWidth * this.numCols;
        this.panelHeight = this.imageHeight * this.numRows;
        this.grid = new boolean[numRows][numCols];
        this.tempGrid = new boolean[numRows][numCols];
        // Initialize grid and tempGrid to false
        for (int row = numRows - 1; row >= 0; --row) {
            for (int col = numCols - 1; col >= 0; --col) {
                this.grid[row][col] = tempGrid[row][col] = false;
            }
        }
        // Set grid values from newGrid values
        for (int row = numRows - 1; row >= 0; --row) {
            for (int col = numCols - 1; col >= 0; --col) {
                if (grid[row][col]) {
                    this.grid[row][col] = true;
                }
            }
        }
        repaint();
    }

    public boolean gridsEqual(boolean[][] g1, boolean[][] g2, int numRows, int numCols) {
        boolean result = true;
        for (int row = numRows - 1; row >= 0; --row) {
            for (int col = numCols - 1; col >= 0; --col) {
                if (g1[row][col] != g2[row][col]) {
                    result = false;
                }
            }
        }
        return result;
    }

    public void increaseGrid(boolean[][] grid, int increaseBy) {
        if (numRows + increaseBy <= maxNumRows && numCols + increaseBy <= maxNumCols) {
            // Ensure increaseBy is > 0 and even
            if (!(increaseBy >= 2)) {
                increaseBy = 2; // give a sane default; remember we are increasing 2 axis (x and y)
            }
            if (!(increaseBy%2 == 0)) {
                increaseBy += 1;
            }
            // Create var that holds how many places we need to move the grid by
            int moveBy = increaseBy/2;

            // Initialize new grid to hold values
            boolean[][] newGrid = new boolean[numRows+increaseBy][numCols+increaseBy];
            for (int row = numRows+increaseBy - 1; row >= 0; --row) {
                for (int col = numCols+increaseBy - 1; col >= 0; --col) {
                    newGrid[row][col] = false;
                }
            }

            // Set newGrid values based off of current grid
            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numCols; col++) {
                    if (grid[row][col]) {
                        newGrid[row+moveBy][col+moveBy] = true;
                    }
                }
            }

            // Recreate numRows, numCols, grid, and tempGrid based off of the new size
            this.numRows = numRows + increaseBy;
            this.numCols = numCols + increaseBy;
            this.panelWidth = this.imageWidth * this.numCols;
            this.panelHeight = this.imageHeight * this.numRows;
            this.grid = new boolean[numRows][numCols];
            this.tempGrid = new boolean[numRows][numCols];
            // Initialize grid and tempGrid to false
            for (int row = numRows - 1; row >= 0; --row) {
                for (int col = numCols - 1; col >= 0; --col) {
//                    System.out.println("NumCols: " + numCols + "; NumRows: " + numRows + "; Col: " + col + "; Row: " + row + ";");
                    this.grid[row][col] = tempGrid[row][col] = false;
                }
            }
            // Set grid values from newGrid values
            for (int row = numRows - 1; row >= 0; --row) {
                for (int col = numCols - 1; col >= 0; --col) {
                    if (newGrid[row][col]) {
//                        System.out.println("NumCols: " + numCols + "; NumRows: " + numRows + "; Col: " + col + "; Row: " + row + ";");
                        this.grid[row][col] = true;
                    }
                }
            }
        } else {
//            System.out.println("Unable to grow grid any further: Upper bounds exceeded!");
        }
        repaint();
    }

    public void mousePressed(final MouseEvent me) {
        if (editable) {
            final int row = me.getY() / imageHeight,
                    col = me.getX() / imageWidth;
            if (row >= 0 && row < numRows &&
                    col >= 0 && col < numCols) {

                grid[row][col] = !grid[row][col];
                repaint();
            }
        }
    }

    public void mouseDragged(final MouseEvent me) {
        if (editable) {
            final int row = me.getY() / imageHeight,
                    col = me.getX() / imageWidth;
            if (row >= 0 && row < numRows &&
                    col >= 0 && col < numCols) {

                mouseY = row;
                mouseX = col;
                grid[row][col] = true;
                repaint();
            }
        }
    }

    public void mouseEntered(final MouseEvent me) { }

    public void mouseExited(final MouseEvent me) {
        mouseX = mouseY = -1;
        repaint();
    }

    public void mouseClicked(final MouseEvent me) { }

    public void mouseReleased(final MouseEvent me) { }

    public void mouseMoved(final MouseEvent me) {
        if (editable) {
            final int row = me.getY() / imageHeight,
                    col = me.getX() / imageWidth;
            if (row >= 0 && row < numRows &&
                    col >= 0 && col < numCols) {

                mouseY = row;
                mouseX = col;
                repaint();
            }
        }
    }
}
