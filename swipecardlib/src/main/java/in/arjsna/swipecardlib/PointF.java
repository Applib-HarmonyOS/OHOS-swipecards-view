package in.arjsna.swipecardlib;

import ohos.agp.utils.Point;

/**
 * PointF.
 */
public class PointF {
    public float pointX;
    public float pointY;

    public PointF() {
    }

    public PointF(float x, float y) {
        this.pointX = x;
        this.pointY = y;
    }

    public PointF(Point p) {
        this.pointX = p.getPointX();
        this.pointY = p.getPointY();
    }

    /**
     * Create a new PointF initialized with the values in the specified
     * PointF (which is left unmodified).
     *
     * @param p The point whose values are copied into the new
     *          point.
     */
    public PointF(PointF p) {
        this.pointX = p.pointX;
        this.pointY = p.pointY;
    }


    /**
     * Set the point's x and y coordinates.
     *
     * @param x x
     * @param y y
     */
    public final void set(float x, float y) {
        this.pointX = x;
        this.pointY = y;
    }

    /**
     * Set the point's x and y coordinates to the coordinates of p.
     *
     * @param p pointF
     */
    public final void set(PointF p) {
        this.pointX = p.pointX;
        this.pointY = p.pointY;
    }

    public final void negate() {
        pointX = -pointX;
        pointY = -pointY;
    }

    public final void offset(float dx, float dy) {
        pointX += dx;
        pointY += dy;
    }

    /**
     * Returns true if the point's coordinates equal (x,y).
     *
     * @param x x
     * @param y y
     * @return true if the point equal to coordinates
     */
    public final boolean equals(float x, float y) {
        return this.pointX == x && this.pointY == y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PointF pointF = (PointF) o;

        if (Float.compare(pointF.pointX, pointX) != 0) {
            return false;
        }
        if (Float.compare(pointF.pointY, pointY) != 0) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (pointX != +0.0f ? Float.floatToIntBits(pointX) : 0);
        result = 31 * result + (pointY != +0.0f ? Float.floatToIntBits(pointY) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PointF(" + pointX + ", " + pointY + ")";
    }


    /**
     * Return the euclidian distance from (0,0) to the point.
     *
     * @return Return the euclidian distance from (0,0) to the point.
     */
    public final float length() {
        return length(pointX, pointY);
    }

    /**
     * Returns the euclidian distance from (0,0) to (x,y).
     *
     * @param x x
     * @param y y
     * @return the euclidian distance from (0,0) to (x,y).
     */
    public static float length(float x, float y) {
        return (float) Math.hypot(x, y);
    }

}
