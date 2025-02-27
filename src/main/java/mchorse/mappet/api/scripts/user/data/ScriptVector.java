package mchorse.mappet.api.scripts.user.data;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Script vector (position) represents a position in the space
 */
public class ScriptVector
{
    /**
     * X coordinate
     */
    public double x;

    /**
     * Y coordinate
     */
    public double y;

    /**
     * Z coordinate
     */
    public double z;

    public ScriptVector(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public ScriptVector(Vec3d vector)
    {
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
    }

    public ScriptVector(BlockPos pos)
    {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    @Override
    public String toString()
    {
        return "ScriptVector(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    /**
     * Convert this vector to an array string
     *
     * <pre>{@code
     * function main(c)
     * {
     *     var subject = c.getSubject();
     *     var subjectPosition = subject.getPosition();
     *     c.send("The player is at " + subjectPosition.toArrayString() + "!");
     *     // The player is at [x, y, z]!
     * }
     * }</pre>
     */
    public String toArrayString()
    {
        return "[" + this.x + ", " + this.y + ", " + this.z + "]";
    }

    public ScriptVector add(ScriptVector other)
    {
        return new ScriptVector(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public ScriptVector subtract(ScriptVector other)
    {
        return new ScriptVector(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public ScriptVector multiply(double scalar)
    {
        return new ScriptVector(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public double length()
    {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public ScriptVector normalize()
    {
        double length = this.length();
        return new ScriptVector(this.x / length, this.y / length, this.z / length);
    }

    /**
     * Computes dot product with another vector.
     *
     * @param vector The other vector
     * @return Dot product value
     */
    public double dotProduct(ScriptVector vector) {
        return this.x * vector.x + this.y * vector.y + this.z * vector.z;
    }

    /**
     * Computes cross product with another vector.
     *
     * @param vector The other vector
     * @return The cross product vector
     */
    public ScriptVector crossProduct(ScriptVector vector) {
        return new ScriptVector(
                this.y * vector.z - this.z * vector.y,
                this.z * vector.x - this.x * vector.z,
                this.x * vector.y - this.y * vector.x
        );
    }

    /**
     * Converts this vector to pitch and yaw angles.
     *
     * @param vector The target vector
     * @return Vector containing pitch, yaw, 0
     */
    public ScriptVector toRotations(ScriptVector vector) {
        ScriptVector subtractVector = new ScriptVector(this.x, this.y, this.z).subtract(vector);

        double hypotenuse = Math.sqrt(Math.pow(subtractVector.x, 2) + Math.pow(subtractVector.z, 2));

        double pitch = Math.toDegrees(Math.atan2(hypotenuse, subtractVector.y));
        double yaw = Math.toDegrees(-Math.atan2(subtractVector.x, subtractVector.z));

        return new ScriptVector(pitch, yaw, 0);
    }

    /**
     * Return the angle between vectors.
     *
     * If the vectors are not normalized, the method will return NaN.
     *
     * @param vector The vector
     */
    public double getAngle(ScriptVector vector) {
        return Math.toDegrees(Math.acos(this.dotProduct(vector)));
    }

    /**
     * Rotate this vector by given pitch and yaw angles.
     *
     <pre>{@code
     *     function main(c)
     *     {
     *         var look = c.player.look.rotate(0, 90)
     *         c.player.setMotion(look.x, 0, look.z)
     *     }
     * }</pre>
     *
     * @param pitch Pitch angle in degrees
     * @param yaw Yaw angle in degrees
     * @return The rotated vector
     */
    public ScriptVector rotate(double pitch, double yaw) {
        ScriptVector normalizeVector = new ScriptVector(this.x, this.y, this.z).normalize();

        double radiansPitch = Math.toRadians(-pitch);
        double radiansYaw = Math.toRadians(-yaw);

        double x = normalizeVector.x;
        double y = normalizeVector.y * Math.cos(radiansPitch) - normalizeVector.z * Math.sin(radiansPitch);
        double z = normalizeVector.y * Math.sin(radiansPitch) + normalizeVector.z * Math.cos(radiansPitch);

        return new ScriptVector(x * Math.cos(radiansYaw) + z * Math.sin(radiansYaw), y, x * -Math.sin(radiansYaw) + z * Math.cos(radiansYaw));
    }

    /**
     * Linearly interpolates between this and the given vector.
     *
     * @param vector Target vector
     * @param coefficient Interpolation coefficient
     * @return The interpolated vector
     */
    public ScriptVector interpolation(ScriptVector vector, double coefficient) {
        if (coefficient < 0 || coefficient > 1) {
            return null;
        }

        return new ScriptVector(
                this.x + (vector.x - this.x) * coefficient,
                this.y + (vector.y - this.y) * coefficient,
                this.z + (vector.z - this.z) * coefficient
        );
    }

    /**
     * Multiplies this vector by components of given vector.
     *
     * @param vector Vector to multiply
     * @return Product vector
     */
    public ScriptVector vectorMultiply(ScriptVector vector) {
        return new ScriptVector(this.x * vector.x, this.y * vector.y, this.z * vector.z);
    }

    /**
     * Divides this vector by components of given vector.
     *
     * @param vector Vector to divide by
     * @return Result vector
     */
    public ScriptVector divide(ScriptVector vector) {
        return new ScriptVector(this.x / vector.x, this.y / vector.y, this.z / vector.z);
    }

    /**
     * Creates a copy of this vector.
     *
     * @return Copy of this vector
     */
    public ScriptVector copy() {
        return new ScriptVector(this.x, this.y, this.z);
    }

    /**
     * Checks if this vector equals another vector.
     *
     * @param vector The other vector
     * @return true if equal, false if not
     */
    public boolean equals(ScriptVector vector) {
        return (this.x == vector.x) && (this.y == vector.y) && (this.z == vector.z);
    }

    /**
     * Checks if this vector equals another x, y, z.
     *
     * @param x
     * @param y
     * @param z
     * @return true if equal, false if not
     */
    public boolean equals(double x, double y, double z) {
        return (this.x == x) && (this.y == y) && (this.z == z);
    }

    /**
     * Computes distance between this and another vector.
     *
     * @param vector The other vector
     * @return The distance
     */
    public double distance(ScriptVector vector) {
        double dx = this.x - vector.x;
        double dy = this.y - vector.y;
        double dz = this.z - vector.z;

        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2));
    }

    /**
     * Computes distance between this and another vector.
     *
     * @return The distance
     */
    public double distance(double x, double y, double z) {
        double dx = this.x - x;
        double dy = this.y - y;
        double dz = this.z - z;

        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2));
    }
}