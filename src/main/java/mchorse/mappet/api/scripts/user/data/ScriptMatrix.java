package mchorse.mappet.api.scripts.user.data;

public class ScriptMatrix {
    private final Double[][] data;
    private final int rows;
    private final int cols;

    public ScriptMatrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.data = new Double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.setValue(i, j, 0);
            }
        }
    }

    public ScriptMatrix(Double[][] data) {
        this.rows = data.length;
        this.cols = data[0].length;
        this.data = new Double[rows][cols];

        for (int i = 0; i < rows; i++) {
            if (data[i].length != cols) {
                throw new IllegalArgumentException("All strings must have the same length");
            }
            for (int j = 0; j < cols; j++) {
                this.setValue(i, j, data[i][j]);
            }
        }
    }

    /**
     * Returns the number of rows in the matrix.
     *
     * @return the number of rows in the matrix
     */
    public int getRows() {
        return this.rows;
    }

    /**
     * Returns the number of columns in the matrix.
     *
     * @return the number of columns in the matrix
     */
    public int getCols() {
        return this.cols;
    }

    /**
     * Returns the value of the matrix element by the given row and column indices.
     *
     * @param row row index
     * @param col column index
     * @return value of matrix element by given indices
     */
    public double getValue(int row, int col) {
        return this.data[row][col];
    }

    /**
     * Sets the value of the matrix element by the given row and column indices.
     *
     * @param row row index
     * @param col column index
     * @param value new value of the matrix element
     */
    public void setValue(int row, int col, double value) {
        this.data[row][col] = value;
    }

    /**
     * Checks if the matrix contains elements within a given radius from a specified point.
     *
     * @param x x-coordinate of the circle center
     * @param y y-coordinate of the circle center
     * @param r radius of the circle
     * @param args additional arguments (if required)
     * @return true if the matrix contains elements within the given radius, otherwise false
     */
    public boolean hasInRadius(int x, int y, int r, Object... args) {
        for (int i = x-r; i <= x+r; i++) {
            for (int j = y-r; j <= y+r; j++) {
                for(Object arg : args) if (this.hasDot(i, j) && this.data[i][j] == arg) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Creates and returns a copy of the current matrix.
     *
     * @return a copy of the current matrix
     */
    public ScriptMatrix copy() {
        return new ScriptMatrix(this.data);
    };

    /**
     * Creates and returns a smoothed version of the current matrix.
     *
     * @return a smoothed version of the current matrix
     */
    public ScriptMatrix smooth() {
        Double[][] matrix = new Double[][]{};
        for (int y = 0; y < this.getRows(); y++) {
            for (int x = 0; x < this.getCols(); x++) {
                for (int ny = -1; ny <= 1; ny++) {
                    for (int nx = -1; nx <= 1; nx++) {
                        if (this.hasDot(x, y) && this.hasDot(x+nx, y+ny)) {
                            matrix[y+ny][x+nx] = (matrix[y][x] + matrix[y+ny][x+nx]) / 2;
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        return new ScriptMatrix(matrix);
    }

    /**
     * Creates and returns a transposed version of the current matrix.
     *
     * @return the transposed version of the current matrix
     */
    public ScriptMatrix transposeMatrix() {
        Double[][] matrix = new Double[][]{};
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                matrix[i][j] = matrix[j][i];
            }
        }

        return new ScriptMatrix(matrix);
    }

    /**
     *
     */
    public boolean hasDot(int x, int y) {
        return (x < this.getRows() && y < this.getCols() && x >= 0 && y >= 0 && this.data[y] != null && this.data[y][x] != null);
    }
}
