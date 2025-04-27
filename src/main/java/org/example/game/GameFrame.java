package org.example.game;


import org.example.databaseH2.DataSource;

import javax.swing.*;
import java.awt.event.*;
import java.net.URL;

public class GameFrame extends JFrame implements ActionListener {
    private final JMenuItem speed;
    private final JMenuItem color;
    private final JMenuItem loadLastGame;
    private final GamePanel gamePanel;

    public GameFrame(User currentUser, boolean isNewUser) {

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DataSource.close();
                System.exit(0);
            }
        });

        GamePanel.CURRENT_STATE = States.FIRST_ENTRY;

        this.setTitle("Snake Game");
        this.setResizable(false);

        JMenuBar jMenuBar = new JMenuBar();
        JMenu settingsMenu = new JMenu("Settings");

        settingsMenu.setMnemonic(KeyEvent.VK_S);
        jMenuBar.add(settingsMenu);

        speed = new JMenuItem("Speed");
        color = new JMenuItem("Color");
        loadLastGame = new JMenuItem("Load Last Game");

        speed.addActionListener(this);
        ImageIcon speedIcon = new ImageIcon(GameFrame.class.getResource("resources/speed.png"));
        speed.setIcon(speedIcon);

        color.addActionListener(this);
        ImageIcon colorIcon = new ImageIcon(GameFrame.class.getResource("resources/color.png"));
        color.setIcon(colorIcon);

        loadLastGame.addActionListener(this);
        ImageIcon loadIcon = new ImageIcon(GameFrame.class.getResource("resources/loading.png"));
        loadLastGame.setIcon(loadIcon);

        settingsMenu.add(speed);
        settingsMenu.add(color);
        settingsMenu.add(loadLastGame);

        this.setJMenuBar(jMenuBar);

        gamePanel = new GamePanel(currentUser, isNewUser);
        gamePanel.requestFocus();
        this.add(gamePanel);

        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(color)){
            GamePanel.SNAKE_COLOR = JColorChooser.showDialog(this,"Select a color",null);
        } else if (e.getSource().equals(speed)) {
            new SpeedFrame(this, GamePanel.CURRENT_SPEED);
        } else if (e.getSource().equals(loadLastGame)) {
            GamePanel.CURRENT_STATE = States.IS_LOADED;
            if(GamePanel.TIMER != null && GamePanel.TIMER.isRunning()){
                GamePanel.TIMER.stop();
            }
            gamePanel.repaint();
        }
    }
}
