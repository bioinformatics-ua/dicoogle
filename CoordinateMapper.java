// 
// Decompiled by Procyon v0.5.36
// 

public abstract class CoordinateMapper
{
    public static final int UNKNOWN = 0;
    public static final int NIFTI = 1;
    public static final int DICOM = 2;
    protected int coorType;
    
    public CoordinateMapper() {
        this.coorType = 1;
    }
    
    public int getCoorType() {
        return this.coorType;
    }
    
    public String getXDescription() {
        switch (this.coorType) {
            case 1: {
                return "left to right";
            }
            case 2: {
                return "right to left";
            }
            default: {
                return "unknown";
            }
        }
    }
    
    public String getYDescription() {
        switch (this.coorType) {
            case 1: {
                return "posterior to anterior";
            }
            case 2: {
                return "anterior to posterior";
            }
            default: {
                return "unknown";
            }
        }
    }
    
    public String getZDescription() {
        switch (this.coorType) {
            case 1:
            case 2: {
                return "inferior to superior";
            }
            default: {
                return "unknown";
            }
        }
    }
    
    public boolean convertToType(final int coorType) {
        if (this.coorType == coorType) {
            return true;
        }
        if ((this.coorType == 1 && coorType == 2) || (this.coorType == 2 && coorType == 1)) {
            this.flipResultX();
            this.flipResultY();
            this.coorType = coorType;
            return true;
        }
        return false;
    }
    
    public abstract String getName();
    
    public abstract CoordinateMapper copy();
    
    public abstract double getX(final double p0, final double p1, final double p2);
    
    public abstract double getY(final double p0, final double p1, final double p2);
    
    public abstract double getZ(final double p0, final double p1, final double p2);
    
    public abstract void flipResultX();
    
    public abstract void flipResultY();
    
    public abstract void flipResultZ();
    
    public abstract void rotate(final int[] p0, final double[] p1);
    
    public double getX(final int n, final int n2, final int n3) {
        return this.getX(n, n2, (double)n3);
    }
    
    public double getY(final int n, final int n2, final int n3) {
        return this.getY(n, n2, (double)n3);
    }
    
    public double getZ(final int n, final int n2, final int n3) {
        return this.getZ(n, n2, (double)n3);
    }
    
    public double[] transform(final double n, final double n2, final double n3) {
        return new double[] { this.getX(n, n2, n3), this.getY(n, n2, n3), this.getZ(n, n2, n3) };
    }
    
    public double[] transform(final double[] array) {
        return this.transform(array[0], array[1], array[2]);
    }
    
    public double[] transform(final int[] array) {
        return this.transform(array[0], array[1], (double)array[2]);
    }
    
    public double[] transform(final int n, final int n2, final int n3) {
        return this.transform(n, n2, (double)n3);
    }
}
