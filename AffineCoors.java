// 
// Decompiled by Procyon v0.5.36
// 

public class AffineCoors extends CoordinateMapper
{
    public double[][] s;
    private String name;
    
    public AffineCoors(final double[][] array) {
        this(array, 0);
    }
    
    public AffineCoors(final double[][] array, final int n) {
        this(array, n, "");
    }
    
    public AffineCoors(final double[][] array, final int coorType, final String name) {
        this.s = new double[3][4];
        this.name = "";
        this.coorType = coorType;
        this.name = name;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 4; ++j) {
                this.s[i][j] = array[i][j];
            }
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public double[][] getMatrix() {
        return this.s;
    }
    
    public CoordinateMapper copy() {
        return new AffineCoors(this.s, this.coorType, this.name);
    }
    
    public double getX(final double n, final double n2, final double n3) {
        return this.s[0][0] * n + this.s[0][1] * n2 + this.s[0][2] * n3 + this.s[0][3];
    }
    
    public double getY(final double n, final double n2, final double n3) {
        return this.s[1][0] * n + this.s[1][1] * n2 + this.s[1][2] * n3 + this.s[1][3];
    }
    
    public double getZ(final double n, final double n2, final double n3) {
        return this.s[2][0] * n + this.s[2][1] * n2 + this.s[2][2] * n3 + this.s[2][3];
    }
    
    public void flipResultX() {
        for (int i = 0; i < 4; ++i) {
            this.s[0][i] = -this.s[0][i];
        }
    }
    
    public void flipResultY() {
        for (int i = 0; i < 4; ++i) {
            this.s[1][i] = -this.s[1][i];
        }
    }
    
    public void flipResultZ() {
        for (int i = 0; i < 4; ++i) {
            this.s[2][i] = -this.s[2][i];
        }
    }
    
    public void rotate(final int[] array, final double[] array2) {
        final double[][] array3 = new double[3][4];
        final double[][] s = new double[3][4];
        for (int i = 0; i < 3; ++i) {
            final int n = Math.abs(array[i]) - 1;
            array3[n][i] = ((array[i] > 0) ? 1.0 : -1.0);
            if (array[i] < 0) {
                array3[n][3] = array2[n] - 1.0;
            }
        }
        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 4; ++k) {
                for (int l = 0; l < 3; ++l) {
                    final double[] array4 = s[j];
                    final int n2 = k;
                    array4[n2] += this.s[j][l] * array3[l][k];
                }
            }
            final double[] array5 = s[j];
            final int n3 = 3;
            array5[n3] += this.s[j][3];
        }
        this.s = s;
    }
}
