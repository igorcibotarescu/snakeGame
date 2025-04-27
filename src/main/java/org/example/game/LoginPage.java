package org.example.game;


import org.example.databaseH2.DataSource;
import org.example.databaseH2.DbHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.Arrays;

public class LoginPage extends JFrame implements ActionListener {

    JButton loginBtn = new JButton("Login");
    JButton registerBtn = new JButton("Register");
    JTextField userIdInput = new JTextField();
    JPasswordField passwordInput = new JPasswordField();
    JLabel userIdLAbel = new JLabel("UserID");
    JLabel passwordIdLabel = new JLabel("Password");
    JLabel messageLabel = new JLabel("");

    public LoginPage() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DataSource.close();
                System.exit(0);
            }
        });
        this.setTitle("Login");
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setSize(new Dimension(400, 400));
        this.setLayout(null);

        userIdLAbel.setBounds(25, 50, 100, 30);
        passwordIdLabel.setBounds(25,90, 100, 30);
        userIdInput.setBounds(125, 50, 225, 30);
        userIdInput.setFont(new Font(null, Font.ITALIC, 18));
        passwordInput.setBounds(125, 90, 225, 30);
        passwordInput.setFont(new Font(null, Font.ITALIC, 18));
        messageLabel.setBounds(25, 175, 375, 30);
        messageLabel.setFont(new Font(null, Font.ITALIC, 16));
        messageLabel.setHorizontalAlignment(SwingConstants.HORIZONTAL);
        loginBtn.setBounds(75, 250, 100, 50);
        loginBtn.addActionListener(this);
        loginBtn.setFocusable(false);
        registerBtn.setBounds(225, 250, 100, 50);
        registerBtn.addActionListener(this);
        loginBtn.setFocusable(false);

        this.add(userIdLAbel);
        this.add(passwordIdLabel);
        this.add(userIdInput);
        this.add(passwordInput);
        this.add(messageLabel);
        this.add(loginBtn);
        this.add(registerBtn);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(checkUser() || checkPassword()){
            return;
        }
        if(e.getSource() == registerBtn){
            try {
                User newUser = new User(Arrays.toString(passwordInput.getPassword()), 0, userIdInput.getText());
                switch (DbHelper.insertUser(newUser.id(), newUser.password())){
                    case 0 -> {
                        invalidMsg(messageLabel, "User already registered!");
                        resetFields();
                    }
                    case 1 -> {
                        this.dispose();
                        messageLabel.setText("Registered, redirecting...");
                        new GameFrame(newUser, true);
                    }
                }
            } catch (SQLException ex) {
                invalidMsg(messageLabel, "An error occurred while executing login operation!");
                resetFields();
            }
        }else if(e.getSource() == loginBtn){
            try {
                User user = DbHelper.selectUser(userIdInput.getText(), Arrays.toString(passwordInput.getPassword()));
                if(user == null){
                    invalidMsg(messageLabel,"User not found!");
                    resetFields();
                } else if(userIdInput.getText().equals(user.id()) && Arrays.toString(passwordInput.getPassword()).equals(user.password())){
                    this.dispose();
                    messageLabel.setText("Logged, redirecting...");
                    new GameFrame(user, false);
                }
            } catch (SQLException ex) {
                invalidMsg(messageLabel,"SQL Error!");
                resetFields();
            }
        }
    }

    private void resetFields() {
        userIdInput.setText("");
        passwordInput.setText("");
    }

    private boolean checkUser(){
        if(userIdInput.getText().isBlank()){
            invalidMsg(messageLabel, "User Id can't be empty");
            resetFields();
            return true;
        }
        return false;
    }

    private boolean checkPassword(){
        if(Arrays.toString(passwordInput.getPassword()).isBlank()){
            invalidMsg(messageLabel, "Password can't be empty!");
            resetFields();
            return true;
        }
        return false;
    }

    private void invalidMsg(JLabel label, String msg) {
        label.setText(msg);
        label.setForeground(Color.red);
    }
}
