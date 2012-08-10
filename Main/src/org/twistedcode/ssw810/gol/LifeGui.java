package org.twistedcode.ssw810.gol;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: tracyde
 * Date: 8/3/12
 * Time: 2:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class LifeGui extends JFrame implements ActionListener {
    public static final int FPS_MIN = 0, FPS_MAX = 30, FPS_INIT = 10;

    private long generationNumber;
    private boolean isRunning;
    private boolean isPaused;

    private int width = 800;
    private int height = 600;
    private int hgap = 0;
    private int vgap = 0;

    private int boardHeight = 40;
    private int boardWidth = 40;

    Dimension d = new Dimension(width, height);

    private final Timer timer;

    private LifeBoard board;
    private final JLabel lBWidth;
    private final JLabel lBHeight;
    private final JLabel lGen;
    private final JLabel lAlive;
    private final JLabel lDead;
    private final JLabel lBorn;
    private final JLabel lKilled;

    private final JButton bStart;
    private final JButton bPause;
    private final JButton bReset;
    private final JButton bSave;
    private final JButton bLoad;

//    private final JButton bRandom;

    public LifeGui(String title) {
        super(title);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(d);
        this.setPreferredSize(d);
        this.setResizable(false);
        this.setLayout(new BorderLayout(hgap,vgap));

        // Set up timer to drive animation events.
        timer = new Timer(fpsToMs(FPS_INIT), this);

        // Initialize board and add it to frame
        this.board = new LifeBoard(20, 20, boardHeight, boardWidth);
        JScrollPane scrollPane = new JScrollPane(board);
        Dimension dSP = new Dimension(600,500);
        scrollPane.setPreferredSize(dSP);

        GridBagConstraints boardPanelConstraints = new GridBagConstraints();
//        0,0,500,50,0,0,GridBagConstraints.LINE_START,GridBagConstraints.NONE,
        boardPanelConstraints.gridx = GridBagConstraints.RELATIVE;
        boardPanelConstraints.gridy = GridBagConstraints.RELATIVE;
        boardPanelConstraints.gridwidth = 1;
        boardPanelConstraints.gridheight = 1;
        boardPanelConstraints.weightx = 1;
        boardPanelConstraints.weighty = 0;
        boardPanelConstraints.anchor = GridBagConstraints.LINE_START;
        boardPanelConstraints.fill = GridBagConstraints.NONE;
        boardPanelConstraints.ipadx = 0;
        boardPanelConstraints.ipady = 0;
//        boardPanelConstraints.insets = GridBagConstraints.
        final JPanel boardPanel = new JPanel(new GridBagLayout());
        boardPanel.add(scrollPane, boardPanelConstraints);

        final JPanel scoreBoard = new JPanel();
        scoreBoard.setLayout(new GridLayout(7,1,0,0));
        lBWidth = new JLabel("Board Width: ");
        lBHeight = new JLabel("Board Height: ");
        lGen = new JLabel("Generation: ");
        lAlive = new JLabel("Alive: ");
        lDead = new JLabel("Dead: ");
        lBorn = new JLabel("Born: ");
        lKilled = new JLabel("Killed: ");

        scoreBoard.add(lBWidth);
        scoreBoard.add(lBHeight);
        scoreBoard.add(lGen);
        scoreBoard.add(lAlive);
        scoreBoard.add(lDead);
        scoreBoard.add(lBorn);
        scoreBoard.add(lKilled);

        boardPanelConstraints.gridwidth = GridBagConstraints.REMAINDER;
        boardPanel.add(scoreBoard, boardPanelConstraints);

        this.add(boardPanel, BorderLayout.NORTH);

        // Create and add a separator
//        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
//        separator.setPreferredSize(new Dimension(100,10));
//        separator.setBorder(n);
        this.add(new JSeparator(), BorderLayout.CENTER);

        // Create the panel to hold controls
        final JPanel controlPanel = new JPanel();
        GridBagConstraints buttonPanelConstraints = new GridBagConstraints();
//        0,0,500,50,0,0,GridBagConstraints.LINE_START,GridBagConstraints.NONE,
        buttonPanelConstraints.gridx = GridBagConstraints.RELATIVE;
        buttonPanelConstraints.gridy = GridBagConstraints.RELATIVE;
        buttonPanelConstraints.gridwidth = 0;
        buttonPanelConstraints.gridheight = 0;
        buttonPanelConstraints.weightx = 1;
        buttonPanelConstraints.weighty = 1;
        buttonPanelConstraints.anchor = GridBagConstraints.LINE_START;
        buttonPanelConstraints.fill = GridBagConstraints.BOTH;
        buttonPanelConstraints.ipadx = 0;
        buttonPanelConstraints.ipady = 0;
        controlPanel.setLayout(new GridBagLayout());

        // Create the panel to hold buttons
        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // Create and add the start button and logic
        bStart = new JButton("Start");
        bStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Implement the start button
                System.out.println("Start Button Clicked!");
                if (board.isAnyCellAlive()) {
                    startSim();
                }
            }
        });
        buttonPanel.add(bStart);

        // Create and add the pause button and logic
        bPause = new JButton("Pause");
        bPause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Implement the start button
                System.out.println("Pause Button Clicked!");
                if (isRunning) {
                    pauseSim();
                }
            }
        });
        buttonPanel.add(bPause);

        // Create and add the reset button and logic
        bReset = new JButton("Reset");
        bReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Implement the reset button
                System.out.println("Reset Button Clicked!");
                resetSim();
            }
        });
        buttonPanel.add(bReset);

        // Create and add the save button and logic
        bSave = new JButton("Save");
        bSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Implement the save button
                System.out.println("Save Button Clicked!");
                SimState simState = new SimState(board, generationNumber);
                saveState(simState);
            }
        });
        buttonPanel.add(bSave);

        // Create and add the load button and logic
        bLoad = new JButton("Load");
        bLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Implement the load button
                System.out.println("Load Button Clicked!");
                loadState();

                lBWidth.setText("Board Width: " + board.getNumCols());
                lBHeight.setText("Board Height: " + board.getNumRows());
                lGen.setText("Generation: " + generationNumber);
                lAlive.setText("Alive: " + board.getAlive());
                lDead.setText("Dead: " + board.getDead());
                lBorn.setText("Born: " + board.getBorn());
                lKilled.setText("Killed: " + board.getKilled());
            }
        });
        buttonPanel.add(bLoad);

        controlPanel.add(buttonPanel, buttonPanelConstraints);

        //controlPanel.add(scoreBoard);
        this.add(controlPanel, BorderLayout.SOUTH);
    }

    private int fpsToMs(final int fps) {
        if (fps == 0) {
            return Integer.MAX_VALUE;
        }
        return 1000 / fps;
    }

    public void start() {
        timer.restart();
    }

    public void stop() {
        timer.stop();
    }

    public void actionPerformed(final ActionEvent e) {
        board.makeNextGeneration();
        lBWidth.setText("Board Width: " + board.getNumCols());
        lBHeight.setText("Board Height: " + board.getNumRows());
        lGen.setText("Generation: " + ++generationNumber);
        lAlive.setText("Alive: " + board.getAlive());
        lDead.setText("Dead: " + board.getDead());
        lBorn.setText("Born: " + board.getBorn());
        lKilled.setText("Killed: " + board.getKilled());

        if (!board.isAnyCellAlive() || board.isStalled()) {
//            final String message =
//                    generationNumber == 2 ?
//                            "Your configuration killed everyone off in 1 generation." :
//                            "Your configuration killed everyone off in " +
//                                    (generationNumber - 1)  + " generations.";
//            new MessageBox(this, "Too Bad", message, MessageBox.FROWN);
//            resetSim();
            pauseSim();
        }
    }

    private void setSpeed(final int fps) {
        if (fps == 0) {
            stop();
        } else {
            if (isRunning && !isPaused && !timer.isRunning()) {
                start();
            }
            timer.setDelay(fpsToMs(fps));
        }
    }

    private void startSim() {
        if (!isPaused) {
            generationNumber = 1;
        }
        isPaused = false;
        isRunning = true;
//        startResetButton.setText("Reset");
//        startResetButton.setMnemonic('R');
//        statusLabel.setText("Game in session...");
//        boardTypeComboBox.setEnabled(false);
//        presetComboBox.setEnabled(false);
        checkButtons();
        board.setEditable(false);
        lBWidth.setText("Board Width: " + board.getNumCols());
        lBHeight.setText("Board Height: " + board.getNumRows());
        lGen.setText("Generation: " + generationNumber);
        lAlive.setText("Alive: " + board.getAlive());
        lDead.setText("Dead: " + board.getDead());
        lBorn.setText("Born: " + board.getBorn());
        lKilled.setText("Killed: " + board.getKilled());
//        setSpeed(slider.getValue());
        setSpeed(FPS_INIT);
    }

    private void checkButtons() {
        if (isRunning) {
            bStart.setEnabled(false);
            bPause.setEnabled(true);
            bReset.setEnabled(true);
            bSave.setEnabled(false);
            bLoad.setEnabled(false);
        } else if (isPaused) {
            bStart.setEnabled(true);
            bPause.setEnabled(false);
            bReset.setEnabled(true);
            bSave.setEnabled(true);
            bLoad.setEnabled(true);
        }
    }

    private void pauseSim() {
        stop();
        isPaused = true;
        isRunning = false;
//        startResetButton.setText("Reset");
//        startResetButton.setMnemonic('R');
//        statusLabel.setText("Game in session...");
//        boardTypeComboBox.setEnabled(false);
//        presetComboBox.setEnabled(false);
        checkButtons();
        board.setEditable(true);
        lBWidth.setText("Board Width: " + board.getNumCols());
        lBHeight.setText("Board Height: " + board.getNumRows());
        lGen.setText("Generation: " + generationNumber);
        lAlive.setText("Alive: " + board.getAlive());
        lDead.setText("Dead: " + board.getDead());
        lBorn.setText("Born: " + board.getBorn());
        lKilled.setText("Killed: " + board.getKilled());
    }

    private void resetSim() {
        stop();
        isRunning = false;
//        startResetButton.setText("Start");
//        startResetButton.setMnemonic('S');
//        boardTypeComboBox.setEnabled(true);
//        presetComboBox.setEnabled(true);
//        presetComboBox.setSelectedIndex(0);
        checkButtons();
        lBWidth.setText("Board Width: ");
        lBHeight.setText("Board Height: ");
        lGen.setText("Generation: ");
        lAlive.setText("Alive: ");
        lDead.setText("Dead: ");
        lBorn.setText("Born: ");
        lKilled.setText("Killed: ");
//        statusLabel.setText(IDLE_MESSAGE);
        board.clear();
        generationNumber = 0;
        board.setEditable(true);
    }

    public void saveState(SimState simState) {
        String fileName = "GOL.dat";
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(fileName);
            out = new ObjectOutputStream(fos);
            out.writeObject(simState);
            out.close();
            System.out.println("Object Persisted");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void loadState() {
        String fileName = "GOL.dat";
        SimState simState = null;
        FileInputStream fis = null;
        ObjectInputStream in = null;
        try {
            fis = new FileInputStream(fileName);
            in = new ObjectInputStream(fis);
            simState = (SimState) in.readObject();
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        board.setGrid(simState.getLifeBoard().getGrid(), simState.getLifeBoard().getNumRows(), simState.getLifeBoard().getNumCols());
        board.setAlive(simState.getLifeBoard().getAlive());
        board.setDead(simState.getLifeBoard().getDead());
        board.setBorn(simState.getLifeBoard().getBorn());
        board.setKilled(simState.getLifeBoard().getKilled());
        this.generationNumber = simState.getGeneration();
    }
}
