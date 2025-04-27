package org.example.databaseH2;

import org.example.game.Direction;
import org.example.game.Tile;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public record Settings(String userID, Color color, int speed, Direction direction,
                       ArrayList<Tile> bodyTiles, Tile headTiles, Tile appleTile) implements Serializable {
}
