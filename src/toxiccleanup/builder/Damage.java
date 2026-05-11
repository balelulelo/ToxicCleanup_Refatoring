package toxiccleanup.builder;

import toxiccleanup.engine.game.Positionable;

public class Damage implements Positionable {
    private int x = 0;
    private int y = 0;
    public static final String UNTYPED = "untyped";
    private String type = UNTYPED;

    public Damage(Positionable position) {
        this.x = position.getX();
        this.y = position.getY();
    }

    public void setType(String value) {
        this.type = value;
    }

    public String getType() {
        return type;
    }

    /**
     * Returns the horizontal (x-axis) coordinate of the component.
     *
     * @return The horizontal (x-axis) coordinate.
     * @ensures \result >= 0
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * Returns the vertical (y-axis) coordinate of the component.
     *
     * @return The vertical (y-axis) coordinate.
     * @ensures \result >= 0
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * Set the horizontal (x-axis) coordinate of the component.
     *
     * @param x The new horizontal coordinate for this component.
     * @requires x >= 0
     * @ensures getX() == x
     */
    @Override
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Set the vertical (y-axis) coordinate of the component.
     *
     * @param y The new vertical coordinate for this component.
     * @requires y >= 0
     * @ensures getY() == y
     */
    @Override
    public void setY(int y) {
        this.y = y;
    }
}
