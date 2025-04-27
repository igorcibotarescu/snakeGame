package org.example.game;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class SpeedFrame extends JFrame implements ChangeListener {

    private final JSlider jSlider;
    private final JLabel jLabel = new JLabel();

    public SpeedFrame(Component component, int currentSpeed) {

        super("Pick-up the Speed");

        jSlider =  new JSlider(1,10,adjustSpeed((200 - currentSpeed) / 20)); // 20 is the unit
        jSlider.setPaintTicks(true);
        jSlider.setMinorTickSpacing(1);
        jSlider.setPaintTrack(true);
        jSlider.setMajorTickSpacing(4);
        jSlider.setPaintLabels(true);
        jSlider.setFont(new Font("MV Boli", Font.PLAIN,15));
        jSlider.addChangeListener(this);

        jLabel.setText("Speed is: " + jSlider.getValue());
        jLabel.setFont(new Font("MV Boli", Font.PLAIN,15));

        JPanel jPanel = new JPanel();
        jPanel.add(jSlider);
        jPanel.add(jLabel);
        this.add(jPanel);

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setSize(new Dimension(400,300));
        setLocationRelativeTo(component);
        this.setVisible(true);
    }

    private int adjustSpeed(int speed) {
        if(speed == 0) {
            speed = 10;
        }
        return speed;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        int newSpeed = jSlider.getValue();
        jLabel.setText("Speed is: " + newSpeed);
        GamePanel.CURRENT_SPEED = adjustSpeed(200 - newSpeed * 20);
    }
}
