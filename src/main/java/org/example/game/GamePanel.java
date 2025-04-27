package org.example.game;


import org.example.databaseH2.DbHelper;
import org.example.databaseH2.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    public static final int SCREEN_WIDTH = 600;
    public static final int SCREEN_HEIGHT = 600;
    public static final int PIXEL_SIZE = 25;
    public static int CURRENT_SPEED = 100; // delay in ms
    public static Direction MOVING_DIRECTION = Direction.RIGHT; // starting direction for movement
    public static States CURRENT_STATE;
    public static Timer TIMER;
    public static Random RANDOM = new Random();
    public static Tile SNAKE_HEAD = new Tile();
    public static Tile APPLE_COORDINATES = new Tile();
    public static ArrayList<Tile> SNAKE_BODY = new ArrayList<>();
    public static User CURRENT_USER;
    public static boolean IS_NEW_USER;
    public static Color SNAKE_COLOR = Color.BLUE;
    public static Settings CURRENT_SETTINGS;

    public GamePanel(User currentUser, boolean isNewUser) {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new CustomKeyAdapter());
        CURRENT_USER = currentUser;
        IS_NEW_USER = isNewUser;
    }

    private void startGame() {
        initAppleCoordinates();
        //refactorAppleCoordinates();
        CURRENT_STATE = States.IS_RUNNING;
        TIMER = new Timer(CURRENT_SPEED, this);
        TIMER.start();
    }

    private void loadFromSettings() {
        if(CURRENT_SETTINGS != null) {
            MOVING_DIRECTION = CURRENT_SETTINGS.direction();
            CURRENT_SPEED = CURRENT_SETTINGS.speed();
            SNAKE_COLOR = CURRENT_SETTINGS.color();
            SNAKE_BODY = CURRENT_SETTINGS.bodyTiles();
            SNAKE_HEAD = CURRENT_SETTINGS.headTiles();
            APPLE_COORDINATES = CURRENT_SETTINGS.appleTile();
        }else {
            SNAKE_BODY = new ArrayList<>();
            SNAKE_HEAD = new Tile();
            MOVING_DIRECTION = Direction.RIGHT;
            initAppleCoordinates();
            //refactorAppleCoordinates();
        }
        CURRENT_STATE = States.IS_RUNNING;
        if(TIMER == null){
            TIMER = new Timer(CURRENT_SPEED, this);
        }
        TIMER.start();
    }

    private void initAppleCoordinates() {
        APPLE_COORDINATES.setX(RANDOM.nextInt(SCREEN_WIDTH / PIXEL_SIZE));
        APPLE_COORDINATES.setY(RANDOM.nextInt(SCREEN_HEIGHT / PIXEL_SIZE));
    }

    /**
     * make sure that apple coordinates are not the same as head or body coordinates
     */
    private void refactorAppleCoordinates() {
        ArrayList<Tile> snakeTiles = new ArrayList<>(SNAKE_BODY);
        snakeTiles.add(SNAKE_HEAD);
        boolean appleCoordinatesOk = true;

        while (true){
            for (Tile snakeTile : snakeTiles) {
                if (snakeTile.equals(APPLE_COORDINATES)) {
                    appleCoordinatesOk = false;
                    break;
                }
            }
            if(appleCoordinatesOk){
                break;
            }
            initAppleCoordinates();
        }
    }

    private void displayMessage(Message message,Graphics graphics){
        String s1,s2;
        switch (message) {
            case START -> {
                if(IS_NEW_USER){
                    s1 = "Welcome aboard '" + CURRENT_USER.id() + "'" + ". Hope you'll enjoy!";
                }else {
                    s1 = "Welcome back '" + CURRENT_USER.id() + "'" + ". Your highest score is " + CURRENT_USER.score();
                }
                s2 = "Press 'Enter' to start the game";
                drawText(graphics, s1, s2);
            }
            case PAUSED -> {
                s1 = "You paused the game";
                s2 = "Press 'Enter' to resume play";
                drawText(graphics, s1, s2);
            }
            case SAVED -> {
                s1 = "You saved the game";
                s2 = "Press 'Enter' to resume play";
                drawText(graphics, s1, s2);
            }
            case END -> {
                s1 = "Game is over: " + SNAKE_BODY.size();
                s2 = "Press 'Enter' to start again";
                drawText(graphics,s1, s2);
            }
            case LOADED -> {
                try {
                    CURRENT_SETTINGS = DbHelper.getSettings(CURRENT_USER.id());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                if(CURRENT_SETTINGS != null) {
                    s1 = "You've successfully loaded the game";
                    s2 = "Press 'Enter' to start it";
                } else {
                    s1 = "You've got no save game";
                    s2 = "Press 'Enter' to start a new one";
                }
                drawText(graphics,s1, s2);
            }
        }
    }

    private void drawText(Graphics graphics, String m1, String m2) {

        graphics.setFont(new Font("Arial", Font.PLAIN,30));
        graphics.setColor(Color.red);
        FontMetrics fontMetrics = getFontMetrics(graphics.getFont());

        graphics.drawString(m1, (SCREEN_WIDTH - fontMetrics.stringWidth(m1)) / 2,SCREEN_HEIGHT / 2);
        graphics.drawString(m2, (SCREEN_WIDTH - fontMetrics.stringWidth(m2)) / 2,SCREEN_HEIGHT / 2 + PIXEL_SIZE * 2);
    }

    private void drawScore(Graphics graphics) {
        graphics.setFont(new Font("Arial", Font.BOLD,16));
        graphics.setColor(Color.white);
        FontMetrics fontMetrics = getFontMetrics(graphics.getFont());
        String currentScore = "Current Score: " + SNAKE_BODY.size();
        graphics.drawString(currentScore, (SCREEN_WIDTH - fontMetrics.stringWidth(currentScore)) / 2,PIXEL_SIZE);
    }

    private void drawSnakeHead(Graphics graphics) {
        graphics.setColor(SNAKE_COLOR);
        graphics.fill3DRect(SNAKE_HEAD.getX() * PIXEL_SIZE, SNAKE_HEAD.getY() * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE, true);
    }

    private void drawSnakeBody(Graphics graphics) {
        graphics.setColor(SNAKE_COLOR);
        SNAKE_BODY.forEach(tile -> graphics.fill3DRect(tile.getX() * PIXEL_SIZE, tile.getY() * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE, true));
    }

    private void drawApple(Graphics graphics) {
        graphics.setColor(Color.red);
        graphics.fillOval(APPLE_COORDINATES.getX() * PIXEL_SIZE, APPLE_COORDINATES.getY() * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
    }

    private void drawGrid(Graphics graphics) {
        // x axis
        for(int i = 1; i < SCREEN_HEIGHT / PIXEL_SIZE; i++){
            graphics.drawLine(0, i * PIXEL_SIZE, SCREEN_WIDTH, i * PIXEL_SIZE);
        }
        // y axis
        for(int i = 1; i < SCREEN_WIDTH / PIXEL_SIZE; i++){
            graphics.drawLine(i * PIXEL_SIZE, 0, i * PIXEL_SIZE, SCREEN_HEIGHT);
        }
    }

    private void draw(Graphics graphics){

        switch (CURRENT_STATE) {
            case IS_LOADED -> displayMessage(Message.LOADED, graphics);
            case IS_PAUSED -> displayMessage(Message.PAUSED, graphics);
            case IS_SAVED -> {
                displayMessage(Message.SAVED, graphics);
                try {
                    DbHelper.saveGame(CURRENT_USER.id(), new Settings(CURRENT_USER.id(), SNAKE_COLOR, CURRENT_SPEED, MOVING_DIRECTION, SNAKE_BODY,SNAKE_HEAD, APPLE_COORDINATES));
                    int bestScore = DbHelper.selectUser(CURRENT_USER.id()).score();
                    if (SNAKE_BODY.size() > bestScore) {
                        DbHelper.updateScore(CURRENT_USER.id(), SNAKE_BODY.size());
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            case FIRST_ENTRY -> displayMessage(Message.START, graphics);
            case IS_OVER -> {
                displayMessage(Message.END, graphics);
                try {
                    int bestScore = DbHelper.selectUser(CURRENT_USER.id()).score();
                    if (SNAKE_BODY.size() > bestScore) {
                        DbHelper.updateScore(CURRENT_USER.id(), SNAKE_BODY.size());
                    }
                } catch (SQLException e) {
                    System.out.println("Error while saving score...");
                    System.out.println(e.getMessage());
                }
            }
            case IS_RUNNING -> drawAllComponents(graphics);
        }
    }

    private void drawAllComponents(Graphics graphics) {
        //drawGrid(graphics);
        drawApple(graphics);
        drawSnakeHead(graphics);
        drawSnakeBody(graphics);
        drawScore(graphics);
    }

    private boolean checkCollisions() {
        // border collisions
        if(SNAKE_HEAD.getX() * PIXEL_SIZE < 0 || PIXEL_SIZE * (SNAKE_HEAD.getX() + 1) > SCREEN_WIDTH || SNAKE_HEAD.getY() * PIXEL_SIZE < 0 || PIXEL_SIZE * (SNAKE_HEAD.getY() + 1) > SCREEN_HEIGHT) {
            return true;
        }
        // body collisions
        if(SNAKE_BODY.size() > 2) {
            for (Tile tile : SNAKE_BODY) {
                if (tileEncountered(SNAKE_HEAD, tile)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void gameOver() {
        CURRENT_STATE = States.IS_OVER;
        TIMER.stop();
    }

    private void move(){

        if(checkCollisions()) {
            gameOver();
            return;
        }
        // food encountered
        if(tileEncountered(SNAKE_HEAD, APPLE_COORDINATES)){
            SNAKE_BODY.add(new Tile(APPLE_COORDINATES.getX(), APPLE_COORDINATES.getY()));
            initAppleCoordinates();
            //refactorAppleCoordinates();
        }
        for(int i = SNAKE_BODY.size() - 1; i >=0; i--){
            Tile currentTile = SNAKE_BODY.get(i);

            if(i == 0) {
                currentTile.setX(SNAKE_HEAD.getX());
                currentTile.setY(SNAKE_HEAD.getY());

            } else {
                Tile prevTile = SNAKE_BODY.get(i-1);
                currentTile.setX(prevTile.getX());
                currentTile.setY(prevTile.getY());
            }
        }
        switch (MOVING_DIRECTION) {
            case UP -> SNAKE_HEAD.setY(SNAKE_HEAD.getY() - 1);
            case DOWN -> SNAKE_HEAD.setY(SNAKE_HEAD.getY() + 1);
            case RIGHT -> SNAKE_HEAD.setX(SNAKE_HEAD.getX() + 1);
            case LEFT -> SNAKE_HEAD.setX(SNAKE_HEAD.getX() - 1);

        }
    }

    private boolean tileEncountered(Tile tile1, Tile tile2) {
        return tile1.getX() == tile2.getX() && tile1.getY() == tile2.getY();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        draw(graphics);
    }

    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    private class CustomKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP -> {
                    if(!MOVING_DIRECTION.equals(Direction.DOWN)){
                        MOVING_DIRECTION = Direction.UP;
                    }
                }
                case KeyEvent.VK_DOWN -> {
                    if(!MOVING_DIRECTION.equals(Direction.UP)){
                        MOVING_DIRECTION = Direction.DOWN;
                    }
                }
                case KeyEvent.VK_RIGHT -> {
                    if(!MOVING_DIRECTION.equals(Direction.LEFT)){
                        MOVING_DIRECTION = Direction.RIGHT;
                    }
                }
                case KeyEvent.VK_LEFT -> {
                    if(!MOVING_DIRECTION.equals(Direction.RIGHT)){
                        MOVING_DIRECTION = Direction.LEFT;
                    }
                }
                case KeyEvent.VK_ENTER -> {
                    switch (CURRENT_STATE) {
                        case FIRST_ENTRY -> startGame();
                        case IS_LOADED -> loadFromSettings();
                        case IS_OVER -> {
                            SNAKE_BODY = new ArrayList<>();
                            SNAKE_HEAD = new Tile();
                            MOVING_DIRECTION = Direction.RIGHT;
                            startGame();
                        }
                        case IS_PAUSED, IS_SAVED -> {
                            CURRENT_STATE = States.IS_RUNNING;
                            TIMER.start();
                        }
                    }
                }
                case KeyEvent.VK_P -> {
                    if(CURRENT_STATE == States.IS_RUNNING) {
                        CURRENT_STATE = States.IS_PAUSED;
                        TIMER.stop();
                        repaint();
                    }
                }
                case KeyEvent.VK_S -> {
                    if(CURRENT_STATE == States.IS_RUNNING) {
                        CURRENT_STATE = States.IS_SAVED;
                        TIMER.stop();
                        repaint();
                    }
                }
            }
        }
    }
}
