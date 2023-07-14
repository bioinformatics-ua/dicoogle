// 
// Decompiled by Procyon v0.5.36
// 

public class NiftiHeader
{
    public byte dim_info;
    public short[] dim;
    public float intent_p1;
    public float intent_p2;
    public float intent_p3;
    public short intent_code;
    public short datatype;
    public short bitpix;
    public short slice_start;
    public float[] pixdim;
    public float vox_offset;
    public float scl_slope;
    public float scl_inter;
    public short slice_end;
    public byte slice_code;
    public byte xyzt_units;
    public float cal_max;
    public float cal_min;
    public float slice_duration;
    public float toffset;
    public int glmax;
    public int glmin;
    public String descrip;
    public String aux_file;
    public short qform_code;
    public short sform_code;
    public float quatern_b;
    public float quatern_c;
    public float quatern_d;
    public float qoffset_x;
    public float qoffset_y;
    public float qoffset_z;
    public float[] srow_x;
    public float[] srow_y;
    public float[] srow_z;
    public String intent_name;
    public static final int DT_NONE = 0;
    public static final int DT_UNKNOWN = 0;
    public static final int DT_BINARY = 1;
    public static final int DT_UNSIGNED_CHAR = 2;
    public static final int DT_SIGNED_SHORT = 4;
    public static final int DT_SIGNED_INT = 8;
    public static final int DT_FLOAT = 16;
    public static final int DT_COMPLEX = 32;
    public static final int DT_DOUBLE = 64;
    public static final int DT_RGB = 128;
    public static final int DT_ALL = 255;
    public static final int DT_INT8 = 256;
    public static final int DT_UINT16 = 512;
    public static final int DT_UINT32 = 768;
    public static final int DT_INT64 = 1024;
    public static final int DT_UINT64 = 1280;
    public static final int DT_FLOAT128 = 1536;
    public static final int DT_COMPLEX128 = 1792;
    public static final int DT_COMPLEX256 = 2048;
    public static final int NIFTI_INTENT_NONE = 0;
    public static final int NIFTI_INTENT_CORREL = 2;
    public static final int NIFTI_INTENT_TTEST = 3;
    public static final int NIFTI_INTENT_FTEST = 4;
    public static final int NIFTI_INTENT_ZSCORE = 5;
    public static final int NIFTI_INTENT_CHISQ = 6;
    public static final int NIFTI_INTENT_BETA = 7;
    public static final int NIFTI_INTENT_BINOM = 8;
    public static final int NIFTI_INTENT_GAMMA = 9;
    public static final int NIFTI_INTENT_POISSON = 10;
    public static final int NIFTI_XFORM_UNKNOWN = 0;
    public static final int NIFTI_XFORM_SCANNER_ANAT = 1;
    public static final int NIFTI_XFORM_ALIGNED_ANAT = 2;
    public static final int NIFTI_XFORM_TAILAIRACH = 3;
    public static final int NIFTI_XFORM_MNI_152 = 4;
    public static final int UNITS_UNKNOWN = 0;
    public static final int UNITS_METER = 1;
    public static final int UNITS_MM = 2;
    public static final int UNITS_MICRON = 3;
    public static final int UNITS_SEC = 8;
    public static final int UNITS_MSEC = 16;
    public static final int UNITS_USEC = 24;
    public static final int UNITS_HZ = 32;
    public static final int UNITS_PPM = 40;
    
    public static int getCoorTypeCode(final String s) {
        if (s.equals("Scanner coordinates")) {
            return 1;
        }
        if (s.equals("Aligned coordinates")) {
            return 2;
        }
        if (s.equals("Tailairach")) {
            return 3;
        }
        if (s.equals("MNI152")) {
            return 4;
        }
        return 0;
    }
    
    public static String getCoorTypeString(final int n) {
        switch (n) {
            case 1: {
                return "Scanner coordinates";
            }
            case 2: {
                return "Aligned coordinates";
            }
            case 3: {
                return "Tailairach";
            }
            case 4: {
                return "MNI152";
            }
            default: {
                return "unknown";
            }
        }
    }
}
