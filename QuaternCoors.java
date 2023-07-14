// 
// Decompiled by Procyon v0.5.36
// 

public class QuaternCoors extends CoordinateMapper
{
    public double[][] s;
    public double[] quaterns;
    private double[] pixdim;
    private double[] qoffsets;
    private String name;
    
    public QuaternCoors(final double[] array, final double[] array2, final double[] array3) {
        this(array, array2, array3, 0);
    }
    
    public QuaternCoors(final double[] array, final double[] array2, final double[] array3, final int n) {
        this(array, array2, array3, n, "");
    }
    
    public QuaternCoors(final double[] quaterns, final double[] pixdim, final double[] qoffsets, final int coorType, final String name) {
        this.s = new double[3][4];
        this.quaterns = new double[5];
        this.pixdim = new double[3];
        this.qoffsets = new double[3];
        this.name = "";
        this.coorType = coorType;
        this.quaterns = quaterns;
        this.pixdim = pixdim;
        this.qoffsets = qoffsets;
        this.name = name;
        this.s = this.getOrientation(this.quaterns, pixdim, qoffsets);
    }
    
    public String getName() {
        return this.name;
    }
    
    public CoordinateMapper copy() {
        return new QuaternCoors(this.quaterns, this.pixdim, this.qoffsets, this.coorType, this.name);
    }
    
    public void flipResultX() {
        final double[] quaterns = { -this.quaterns[0], this.quaterns[3], this.quaterns[4], this.quaterns[1], this.quaterns[2] };
        if (quaterns[1] < 0.0) {
            for (int i = 1; i < 5; ++i) {
                quaterns[i] = -quaterns[i];
            }
        }
        this.qoffsets[0] = -this.qoffsets[0];
        this.quaterns = quaterns;
        this.s = this.getOrientation(this.quaterns, this.pixdim, this.qoffsets);
    }
    
    public void flipResultY() {
        final double[] quaterns = { -this.quaterns[0], this.quaterns[2], this.quaterns[1], -this.quaterns[4], -this.quaterns[3] };
        if (quaterns[1] < 0.0) {
            for (int i = 1; i < 5; ++i) {
                quaterns[i] = -quaterns[i];
            }
        }
        this.qoffsets[1] = -this.qoffsets[1];
        this.quaterns = quaterns;
        this.s = this.getOrientation(this.quaterns, this.pixdim, this.qoffsets);
    }
    
    public void flipResultZ() {
        final double[] quaterns = { -this.quaterns[0], this.quaterns[1], -this.quaterns[2], -this.quaterns[3], this.quaterns[4] };
        this.qoffsets[2] = -this.qoffsets[2];
        this.quaterns = quaterns;
        this.s = this.getOrientation(this.quaterns, this.pixdim, this.qoffsets);
    }
    
    public void flipX(final double[] array) {
        final double[] quaterns = { -this.quaterns[0], -this.quaterns[3], -this.quaterns[4], this.quaterns[1], this.quaterns[2] };
        if (quaterns[1] < 0.0) {
            for (int i = 1; i < 5; ++i) {
                quaterns[i] = -quaterns[i];
            }
        }
        this.quaterns = quaterns;
        for (int j = 0; j < 3; ++j) {
            final double[] qoffsets = this.qoffsets;
            final int n = j;
            qoffsets[n] += this.s[j][0] * (array[0] - 1.0);
        }
        this.s = this.getOrientation(this.quaterns, this.pixdim, this.qoffsets);
    }
    
    public void flipY(final double[] array) {
        final double[] quaterns = { -this.quaterns[0], -this.quaterns[2], this.quaterns[1], this.quaterns[4], -this.quaterns[3] };
        if (quaterns[1] < 0.0) {
            for (int i = 1; i < 5; ++i) {
                quaterns[i] = -quaterns[i];
            }
        }
        this.quaterns = quaterns;
        for (int j = 0; j < 3; ++j) {
            final double[] qoffsets = this.qoffsets;
            final int n = j;
            qoffsets[n] += this.s[j][1] * (array[1] - 1.0);
        }
        this.s = this.getOrientation(this.quaterns, this.pixdim, this.qoffsets);
    }
    
    public void flipZ(final double[] array) {
        final double[] quaterns = this.quaterns;
        final int n = 0;
        quaterns[n] *= -1.0;
        for (int i = 0; i < 3; ++i) {
            final double[] qoffsets = this.qoffsets;
            final int n2 = i;
            qoffsets[n2] += this.s[i][2] * (array[2] - 1.0);
        }
        this.s = this.getOrientation(this.quaterns, this.pixdim, this.qoffsets);
    }
    
    public void swapXY() {
        final double sqrt = Math.sqrt(0.5);
        final double[] quaterns = { -this.quaterns[0], -sqrt * (this.quaterns[2] + this.quaterns[3]), sqrt * (this.quaterns[1] - this.quaterns[4]), sqrt * (this.quaterns[1] + this.quaterns[4]), sqrt * (this.quaterns[2] - this.quaterns[3]) };
        if (quaterns[1] < 0.0) {
            for (int i = 1; i < 5; ++i) {
                quaterns[i] = -quaterns[i];
            }
        }
        final double n = this.pixdim[1];
        this.pixdim[1] = this.pixdim[0];
        this.pixdim[0] = n;
        this.quaterns = quaterns;
        this.s = this.getOrientation(this.quaterns, this.pixdim, this.qoffsets);
    }
    
    public void swapXZ() {
        final double sqrt = Math.sqrt(0.5);
        final double[] quaterns = { -this.quaterns[0], sqrt * ((this.quaterns[0] < 0.0) ? (this.quaterns[1] - this.quaterns[3]) : (this.quaterns[1] + this.quaterns[3])), sqrt * ((this.quaterns[0] < 0.0) ? (this.quaterns[2] - this.quaterns[4]) : (this.quaterns[2] + this.quaterns[4])), sqrt * ((this.quaterns[0] < 0.0) ? (this.quaterns[3] + this.quaterns[1]) : (this.quaterns[3] - this.quaterns[1])), sqrt * ((this.quaterns[0] < 0.0) ? (this.quaterns[4] + this.quaterns[2]) : (this.quaterns[4] - this.quaterns[2])) };
        if (quaterns[1] < 0.0) {
            for (int i = 1; i < 5; ++i) {
                quaterns[i] = -quaterns[i];
            }
        }
        final double n = this.pixdim[2];
        this.pixdim[2] = this.pixdim[0];
        this.pixdim[0] = n;
        this.quaterns = quaterns;
        this.s = this.getOrientation(this.quaterns, this.pixdim, this.qoffsets);
    }
    
    public void swapYZ() {
        final double sqrt = Math.sqrt(0.5);
        final double[] quaterns = { -this.quaterns[0], sqrt * ((this.quaterns[0] > 0.0) ? (this.quaterns[1] - this.quaterns[2]) : (this.quaterns[1] + this.quaterns[2])), sqrt * ((this.quaterns[0] > 0.0) ? (this.quaterns[2] + this.quaterns[1]) : (this.quaterns[2] - this.quaterns[1])), sqrt * ((this.quaterns[0] > 0.0) ? (this.quaterns[3] + this.quaterns[4]) : (this.quaterns[3] - this.quaterns[4])), sqrt * ((this.quaterns[0] > 0.0) ? (this.quaterns[4] - this.quaterns[3]) : (this.quaterns[4] - this.quaterns[3])) };
        if (quaterns[1] < 0.0) {
            for (int i = 1; i < 5; ++i) {
                quaterns[i] = -quaterns[i];
            }
        }
        final double n = this.pixdim[2];
        this.pixdim[2] = this.pixdim[1];
        this.pixdim[1] = n;
        this.quaterns = quaterns;
        this.s = this.getOrientation(this.quaterns, this.pixdim, this.qoffsets);
    }
    
    public void rotate(final int[] array, final double[] array2) {
        final int[] array3 = new int[3];
        final int[] array4 = new int[3];
        final double[] array5 = new double[3];
        for (int i = 0; i < 3; ++i) {
            array4[i] = i + 1;
            array5[i] = array2[i];
        }
        switch (Math.abs(array[0])) {
            case 2: {
                this.swapXY();
                final double n = array5[0];
                array5[0] = array5[1];
                array5[1] = n;
                array4[0] = 2;
                array4[1] = 1;
                break;
            }
            case 3: {
                this.swapXZ();
                final double n2 = array5[0];
                array5[0] = array5[2];
                array5[2] = n2;
                array4[0] = 3;
                array4[2] = 1;
                break;
            }
        }
        if (Math.abs(array[1]) != array4[1]) {
            this.swapYZ();
            final int n3 = array4[1];
            array4[1] = array4[2];
            array4[2] = n3;
            final double n4 = array5[1];
            array5[1] = array5[2];
            array5[2] = n4;
        }
        if (array[0] < 0) {
            this.flipX(array5);
        }
        if (array[1] < 0) {
            this.flipY(array5);
        }
        if (array[2] < 0) {
            this.flipZ(array5);
        }
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
    
    private double[][] getOrientation(final double[] array, final double[] array2, final double[] array3) {
        final double[][] array4 = new double[3][4];
        final double n = array[2];
        final double n2 = array[3];
        final double n3 = array[4];
        final double a = 1.0 - n * n - n2 * n2 - n3 * n3;
        double sqrt;
        if (a <= 0.0) {
            sqrt = 0.0;
        }
        else {
            sqrt = Math.sqrt(a);
        }
        this.quaterns[1] = sqrt;
        array4[0][0] = sqrt * sqrt + n * n - n2 * n2 - n3 * n3;
        array4[0][1] = 2.0 * n * n2 - 2.0 * sqrt * n3;
        array4[0][2] = 2.0 * n * n3 + 2.0 * sqrt * n2;
        array4[1][0] = 2.0 * n * n2 + 2.0 * sqrt * n3;
        array4[1][1] = sqrt * sqrt + n2 * n2 - n * n - n3 * n3;
        array4[1][2] = 2.0 * n2 * n3 - 2.0 * sqrt * n;
        array4[2][0] = 2.0 * n * n3 - 2.0 * sqrt * n2;
        array4[2][1] = 2.0 * n2 * n3 + 2.0 * sqrt * n;
        array4[2][2] = sqrt * sqrt + n3 * n3 - n2 * n2 - n * n;
        if (((array[0] >= 0.0) ? 1 : -1) == -1) {
            final double[] array5 = array4[0];
            final int n4 = 2;
            array5[n4] *= -1.0;
            final double[] array6 = array4[1];
            final int n5 = 2;
            array6[n5] *= -1.0;
            final double[] array7 = array4[2];
            final int n6 = 2;
            array7[n6] *= -1.0;
        }
        final double[][] array8 = new double[3][4];
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                array8[i][j] = array4[i][j] * array2[j];
            }
        }
        array8[0][3] = array3[0];
        array8[1][3] = array3[1];
        array8[2][3] = array3[2];
        return array8;
    }
    
    public double[] getQuaterns() {
        return this.quaterns;
    }
    
    public static double[] getQuaterns(final double[][] array) {
        final double[][] array2 = new double[3][3];
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                array2[i][j] = array[i][j];
            }
        }
        final double[] array3 = new double[5];
        final double[] array4 = new double[3];
        double n = 1.0;
        if (array2[0][0] * array2[1][1] * array2[2][2] - array2[0][0] * array2[2][1] * array2[1][2] - array2[1][0] * array2[0][1] * array2[2][2] + array2[1][0] * array2[2][1] * array2[0][2] + array2[2][0] * array2[0][1] * array2[1][2] - array2[2][0] * array2[1][1] * array2[0][2] < 0.0) {
            array2[0][2] = -array2[0][2];
            array2[1][2] = -array2[1][2];
            array2[2][2] = -array2[2][2];
            n = -1.0;
        }
        final double a = array2[0][0] + array2[1][1] + array2[2][2] + 1.0;
        double n2;
        double n3;
        double n4;
        double n5;
        if (a > 0.5) {
            n2 = 0.5 * Math.sqrt(a);
            n3 = 0.25 * (array2[2][1] - array2[1][2]) / n2;
            n4 = 0.25 * (array2[0][2] - array2[2][0]) / n2;
            n5 = 0.25 * (array2[1][0] - array2[0][1]) / n2;
        }
        else {
            final double a2 = 1.0 + array2[0][0] - array2[1][1] - array2[2][2];
            final double a3 = 1.0 + array2[1][1] - array2[0][0] - array2[2][2];
            final double a4 = 1.0 + array2[2][2] - array2[0][0] - array2[1][1];
            if (a2 > 1.0) {
                n3 = 0.5 * Math.sqrt(a2);
                n4 = 0.25 * (array2[0][1] + array2[1][0]) / n3;
                n5 = 0.25 * (array2[0][2] + array2[2][1]) / n3;
                n2 = 0.25 * (array2[2][1] - array2[1][2]) / n3;
            }
            else if (a3 > 1.0) {
                n4 = 0.5 * Math.sqrt(a3);
                n3 = 0.25 * (array2[0][1] + array2[1][0]) / n4;
                n5 = 0.25 * (array2[1][2] + array2[2][1]) / n4;
                n2 = 0.25 * (array2[0][2] - array2[2][0]) / n4;
            }
            else {
                n5 = 0.5 * Math.sqrt(a4);
                n3 = 0.25 * (array2[0][2] + array2[2][0]) / n5;
                n4 = 0.25 * (array2[1][2] + array2[2][1]) / n5;
                n2 = 0.25 * (array2[1][0] - array2[0][1]) / n5;
            }
            if (n2 < 0.0) {
                n3 = -n3;
                n4 = -n4;
                n5 = -n5;
            }
        }
        array3[0] = n;
        array3[1] = n2;
        array3[2] = n3;
        array3[3] = n4;
        array3[4] = n5;
        return array3;
    }
}
