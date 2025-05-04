package org.example.game;

import java.io.Serializable;

public class  Tile  implements Serializable {
    private int x;
    private int y;

    public Tile() {
        this(0, 0);
    }
    public Tile(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isEqual(Tile tile) {
        if (this == tile) return true;
        if (tile == null || getClass() != tile.getClass()) return false;
        return x == tile.x && y == tile.y;
    }
}
