import ij.process.ColorProcessor;
import ij.process.ImageStatistics;
import ij.measure.Calibration;
import ij.io.FileInfo;
import java.io.IOException;
import ij.io.ImageWriter;
import ij.VirtualStack;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import ij.ImageStack;
import ij.ImagePlus;
import java.io.File;
import ij.io.SaveDialog;
import ij.IJ;
import ij.WindowManager;
import ij.plugin.PlugIn;

// 
// Decompiled by Procyon v0.5.36
// 

public class Nifti_Writer implements PlugIn
{
    public static final int ANALYZE_7_5 = 0;
    public static final int NIFTI_ANALYZE = 1;
    public static final int NIFTI_FILE = 2;
    public boolean littleEndian;
    private int output_type;
    private boolean signed16Bit;
    
    public Nifti_Writer() {
        this.littleEndian = false;
        this.output_type = 2;
        this.signed16Bit = false;
    }
    
    public void run(String pathname) {
        final ImagePlus currentImage = WindowManager.getCurrentImage();
        if (currentImage == null) {
            IJ.noImage();
            return;
        }
        if (pathname != null) {
            if (pathname.startsWith("::ANALYZE_7_5:")) {
                this.output_type = 0;
                pathname = pathname.substring(14).trim();
            }
            else if (pathname.startsWith("::NIFTI_ANALYZE:")) {
                this.output_type = 1;
                pathname = pathname.substring(16).trim();
            }
            else if (pathname.startsWith("::NIFTI_FILE:")) {
                this.output_type = 2;
                pathname = pathname.substring(13).trim();
            }
        }
        String s2;
        String s3;
        if (pathname == null || pathname.equals("")) {
            String str = "";
            String s = "";
            switch (this.output_type) {
                case 0:
                case 1: {
                    str = "Analyze";
                    s = ".img";
                    break;
                }
                case 2: {
                    str = "Nifti";
                    s = ".nii";
                    break;
                }
            }
            final SaveDialog saveDialog = new SaveDialog("Save as " + str, currentImage.getTitle(), s);
            s2 = saveDialog.getDirectory();
            s3 = saveDialog.getFileName();
        }
        else {
            final File file = new File(pathname);
            if (file.isDirectory()) {
                s2 = pathname;
                s3 = currentImage.getTitle();
            }
            else {
                s2 = file.getParent();
                s3 = file.getName();
            }
        }
        if (s3 == null || s3 == "") {
            IJ.showStatus("");
            return;
        }
        if (this.is16BitSigned(currentImage)) {
            this.add(currentImage, -32768);
            this.signed16Bit = true;
        }
        final ImageStack stack = currentImage.getStack();
        if (this.output_type == 0) {
            for (int i = 1; i <= stack.getSize(); ++i) {
                stack.getProcessor(i).flipVertical();
            }
        }
        final int nChannels = currentImage.getNChannels();
        final int n = currentImage.getNFrames() * currentImage.getNSlices();
        if (nChannels != 1) {
            this.reshuffleStack(stack.getImageArray(), nChannels, nChannels * n);
        }
        this.save(currentImage, s2, s3);
        if (currentImage.getNChannels() != 1) {
            this.reshuffleStack(stack.getImageArray(), n, nChannels * n);
        }
        if (this.output_type == 0) {
            for (int j = 1; j <= stack.getSize(); ++j) {
                stack.getProcessor(j).flipVertical();
            }
        }
        if (this.signed16Bit) {
            this.add(currentImage, 32768);
            this.signed16Bit = false;
        }
        IJ.showStatus("");
    }
    
    public boolean save(final ImagePlus imagePlus, String str, String s) {
        if (s == null) {
            return false;
        }
        s = s.trim();
        str = str.trim();
        if (this.output_type != 2) {
            if (s.toLowerCase().endsWith(".img")) {
                s = s.substring(0, s.length() - 4);
            }
            if (s.toLowerCase().endsWith(".hdr")) {
                s = s.substring(0, s.length() - 4);
            }
        }
        if (!str.endsWith(File.separator)) {
            str += File.separator;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream((this.output_type == 2) ? (str + s) : (str + s + ".hdr"));
            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
            IJ.showStatus("Saving as Analyze: " + str + s);
            this.writeHeader(imagePlus, dataOutputStream, this.output_type);
            if (this.output_type != 2) {
                dataOutputStream.close();
                fileOutputStream.close();
                fileOutputStream = new FileOutputStream(str + s + ".img");
                dataOutputStream = new DataOutputStream(fileOutputStream);
            }
            final FileInfo fileInfo = imagePlus.getFileInfo();
            fileInfo.intelByteOrder = this.littleEndian;
            if (fileInfo.fileType != 6) {
                if (imagePlus.getStackSize() > 1 && imagePlus.getStack().isVirtual()) {
                    fileInfo.virtualStack = (VirtualStack)imagePlus.getStack();
                    fileInfo.fileName = "FlipTheseImages";
                }
                new ImageWriter(fileInfo).write((OutputStream)dataOutputStream);
            }
            else {
                this.writeRGBPlanar(imagePlus, dataOutputStream);
            }
            dataOutputStream.close();
            fileOutputStream.close();
            return true;
        }
        catch (IOException ex) {
            IJ.log("Nifti_Writer: " + ex.getMessage());
            return false;
        }
    }
    
    private void writeHeader(final ImagePlus imagePlus, final DataOutputStream dataOutputStream, final int n) throws IOException {
        final FileInfo fileInfo = imagePlus.getFileInfo();
        final NiftiHeader niftiHeader = (NiftiHeader)imagePlus.getProperty("nifti");
        final Calibration calibration = imagePlus.getCalibration();
        short n2 = 0;
        short n3 = 0;
        switch (fileInfo.fileType) {
            case 0: {
                n2 = 2;
                n3 = 8;
                break;
            }
            case 1:
            case 2: {
                n2 = 4;
                n3 = 16;
                break;
            }
            case 3: {
                n2 = 8;
                n3 = 32;
                break;
            }
            case 4: {
                n2 = 16;
                n3 = 32;
                break;
            }
            case 6: {
                n2 = 128;
                n3 = 24;
                break;
            }
            default: {
                n2 = 0;
                n3 = (short)(fileInfo.getBytesPerPixel() * 8);
                break;
            }
        }
        this.writeInt(dataOutputStream, 348);
        for (int i = 0; i < 10; ++i) {
            dataOutputStream.write(0);
        }
        for (int j = 0; j < 18; ++j) {
            dataOutputStream.write(0);
        }
        this.writeInt(dataOutputStream, 16384);
        dataOutputStream.writeShort(0);
        dataOutputStream.writeByte(114);
        dataOutputStream.writeByte(0);
        final short[] array = { (short)((imagePlus.getNChannels() == 1) ? 4 : 5), (short)fileInfo.width, (short)fileInfo.height, (short)imagePlus.getNSlices(), (short)imagePlus.getNFrames(), (short)imagePlus.getNChannels(), 0, 0 };
        for (int k = 0; k < 8; ++k) {
            this.writeShort(dataOutputStream, array[k]);
        }
        if (n == 0) {
            dataOutputStream.writeBytes((calibration.getUnit() + "\u0000\u0000\u0000\u0000").substring(0, 4));
            for (int l = 0; l < 8; ++l) {
                dataOutputStream.write(0);
            }
            dataOutputStream.writeShort(0);
        }
        else {
            this.writeFloat(dataOutputStream, (niftiHeader == null) ? 0.0 : ((double)niftiHeader.intent_p1));
            this.writeFloat(dataOutputStream, (niftiHeader == null) ? 0.0 : ((double)niftiHeader.intent_p2));
            this.writeFloat(dataOutputStream, (niftiHeader == null) ? 0.0 : ((double)niftiHeader.intent_p3));
            this.writeShort(dataOutputStream, (short)((niftiHeader == null) ? 0 : niftiHeader.intent_code));
        }
        this.writeShort(dataOutputStream, n2);
        this.writeShort(dataOutputStream, n3);
        if (n == 0 || niftiHeader == null) {
            dataOutputStream.writeShort(0);
        }
        else {
            this.writeShort(dataOutputStream, niftiHeader.slice_start);
        }
        double[] quaterns = new double[5];
        int n4 = 0;
        int n5 = 0;
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;
        for (int n6 = 0; n6 < 5; ++n6) {
            quaterns[n6] = 0.0;
        }
        double[][] matrix = new double[3][4];
        for (int n7 = 0; n7 < 3; ++n7) {
            matrix[n7][n7] = 1.0;
        }
        if (n != 0) {
            final Object property = imagePlus.getProperty("coors");
            if (property instanceof CoordinateMapper[]) {
                final CoordinateMapper[] array2 = (CoordinateMapper[])property;
                for (int n8 = 0; n8 < array2.length; ++n8) {
                    if (array2[n8] instanceof AffineCoors) {
                        final AffineCoors affineCoors = (AffineCoors)array2[n8].copy();
                        if (affineCoors.convertToType(1)) {
                            matrix = affineCoors.getMatrix();
                            n5 = NiftiHeader.getCoorTypeCode(affineCoors.getName());
                        }
                    }
                    if (array2[n8] instanceof QuaternCoors) {
                        final QuaternCoors quaternCoors = (QuaternCoors)array2[n8].copy();
                        if (quaternCoors.convertToType(1)) {
                            n4 = NiftiHeader.getCoorTypeCode(quaternCoors.getName());
                            quaterns = quaternCoors.getQuaterns();
                            x = array2[n8].getX(0, 0, 0);
                            y = array2[n8].getY(0, 0, 0);
                            z = array2[n8].getZ(0, 0, 0);
                        }
                    }
                }
            }
            else if (niftiHeader != null) {
                quaterns[0] = niftiHeader.pixdim[0];
                quaterns[2] = niftiHeader.quatern_b;
                quaterns[3] = niftiHeader.quatern_c;
                quaterns[4] = niftiHeader.quatern_d;
                n4 = niftiHeader.qform_code;
                x = niftiHeader.qoffset_x;
                y = niftiHeader.qoffset_y;
                z = niftiHeader.qoffset_z;
                n5 = niftiHeader.sform_code;
                for (int n9 = 0; n9 < 4; ++n9) {
                    matrix[0][n9] = niftiHeader.srow_x[n9];
                    matrix[1][n9] = niftiHeader.srow_y[n9];
                    matrix[2][n9] = niftiHeader.srow_z[n9];
                }
            }
        }
        final float[] array3 = { (float)quaterns[0], (float)fileInfo.pixelWidth, (float)fileInfo.pixelHeight, (float)fileInfo.pixelDepth, (float)fileInfo.frameInterval, 0.0f, 0.0f, 0.0f };
        if (n != 0 && niftiHeader != null) {
            for (int n10 = 5; n10 < 8; ++n10) {
                array3[n10] = niftiHeader.pixdim[n10];
            }
        }
        for (int n11 = 0; n11 < 8; ++n11) {
            this.writeFloat(dataOutputStream, array3[n11]);
        }
        this.writeFloat(dataOutputStream, (n == 2) ? 352.0f : 0.0f);
        double[] coefficients = { 0.0, 1.0 };
        if (calibration.getFunction() == 0) {
            coefficients = calibration.getCoefficients();
        }
        double n12 = coefficients[0];
        if (this.signed16Bit) {
            if (coefficients[1] != 0.0) {
                n12 += 32768.0 * coefficients[1];
            }
            else {
                n12 += 32768.0;
            }
        }
        if (n == 0) {
            this.writeFloat(dataOutputStream, 1.0f);
            this.writeFloat(dataOutputStream, 0.0f);
            this.writeFloat(dataOutputStream, 0.0f);
        }
        else {
            this.writeFloat(dataOutputStream, coefficients[1]);
            this.writeFloat(dataOutputStream, n12);
            this.writeShort(dataOutputStream, (niftiHeader != null) ? niftiHeader.slice_end : ((short)(array[3] - 1)));
            dataOutputStream.write((niftiHeader != null) ? niftiHeader.slice_code : 0);
            final String trim = calibration.getUnit().toLowerCase().trim();
            int b = 0;
            if (trim.equals("meter") || trim.equals("metre") || trim.equals("m")) {
                b = (byte)(b | 0x1);
            }
            else if (trim.equals("mm")) {
                b = (byte)(b | 0x2);
            }
            else if (trim.equals("micron")) {
                b = (byte)(b | 0x3);
            }
            dataOutputStream.write(b);
        }
        final double min = imagePlus.getProcessor().getMin();
        this.writeFloat(dataOutputStream, coefficients[0] + coefficients[1] * (imagePlus.getProcessor().getMax() + 1.0));
        this.writeFloat(dataOutputStream, coefficients[0] + coefficients[1] * min);
        if (n == 0) {
            dataOutputStream.writeInt(0);
            dataOutputStream.writeInt(0);
            final ImageStatistics statistics = imagePlus.getStatistics();
            this.writeInt(dataOutputStream, this.signed16Bit ? ((int)statistics.max + 32768) : ((int)statistics.max));
            this.writeInt(dataOutputStream, this.signed16Bit ? ((int)statistics.min + 32768) : ((int)statistics.min));
        }
        else {
            this.writeFloat(dataOutputStream, (niftiHeader != null) ? ((double)niftiHeader.slice_duration) : 0.0);
            this.writeFloat(dataOutputStream, (niftiHeader != null) ? ((double)niftiHeader.toffset) : 0.0);
            this.writeFloat(dataOutputStream, 0.0);
            this.writeFloat(dataOutputStream, 0.0);
        }
        if (n == 0) {
            for (int n13 = 0; n13 < 80; ++n13) {
                dataOutputStream.write(0);
            }
            for (int n14 = 0; n14 < 24; ++n14) {
                dataOutputStream.write(0);
            }
            dataOutputStream.write(0);
            for (int n15 = 0; n15 < 10; ++n15) {
                dataOutputStream.write(0);
            }
            for (int n16 = 0; n16 < 10; ++n16) {
                dataOutputStream.write(0);
            }
            for (int n17 = 0; n17 < 10; ++n17) {
                dataOutputStream.write(0);
            }
            for (int n18 = 0; n18 < 10; ++n18) {
                dataOutputStream.write(0);
            }
            for (int n19 = 0; n19 < 10; ++n19) {
                dataOutputStream.write(0);
            }
            for (int n20 = 0; n20 < 10; ++n20) {
                dataOutputStream.write(0);
            }
            for (int n21 = 0; n21 < 3; ++n21) {
                dataOutputStream.write(0);
            }
            dataOutputStream.writeInt(0);
            dataOutputStream.writeInt(0);
            dataOutputStream.writeInt(0);
            dataOutputStream.writeInt(0);
            dataOutputStream.writeInt(0);
            dataOutputStream.writeInt(0);
            dataOutputStream.writeInt(0);
            dataOutputStream.writeInt(0);
        }
        else {
            final String s = (niftiHeader == null) ? new String() : niftiHeader.descrip.trim();
            final int length = s.length();
            if (length > 80) {
                dataOutputStream.writeBytes(s.substring(0, 80));
            }
            else {
                dataOutputStream.writeBytes(s);
                for (int n22 = length; n22 < 80; ++n22) {
                    dataOutputStream.write(0);
                }
            }
            final String s2 = (niftiHeader == null) ? "" : niftiHeader.aux_file.trim();
            final int length2 = s2.length();
            if (length2 > 24) {
                dataOutputStream.writeBytes(s2.substring(0, 24));
            }
            else {
                dataOutputStream.writeBytes(s2);
                for (int n23 = length2; n23 < 24; ++n23) {
                    dataOutputStream.write(0);
                }
            }
            this.writeShort(dataOutputStream, (short)n4);
            this.writeShort(dataOutputStream, (short)n5);
            this.writeFloat(dataOutputStream, quaterns[2]);
            this.writeFloat(dataOutputStream, quaterns[3]);
            this.writeFloat(dataOutputStream, quaterns[4]);
            this.writeFloat(dataOutputStream, x);
            this.writeFloat(dataOutputStream, y);
            this.writeFloat(dataOutputStream, z);
            for (int n24 = 0; n24 < 3; ++n24) {
                for (int n25 = 0; n25 < 4; ++n25) {
                    this.writeFloat(dataOutputStream, matrix[n24][n25]);
                }
            }
            final String s3 = (niftiHeader == null) ? "" : niftiHeader.intent_name.trim();
            final int length3 = s3.length();
            if (length3 > 16) {
                dataOutputStream.writeBytes(s3.substring(0, 16));
            }
            else {
                dataOutputStream.writeBytes(s3);
                for (int n26 = length3; n26 < 16; ++n26) {
                    dataOutputStream.write(0);
                }
            }
            dataOutputStream.writeBytes((n == 1) ? "ni1\u0000" : "n+1\u0000");
            if (n == 2) {
                dataOutputStream.writeInt(0);
            }
        }
    }
    
    private void reshuffleStack(final Object[] array, final int n, final int n2) {
        final Object[] array2 = new Object[array.length];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = array[i];
        }
        int j = 0;
        int n3 = 0;
        while (j < n) {
            for (int k = j; k < n2; k += n, ++n3) {
                array[n3] = array2[k];
            }
            ++j;
        }
    }
    
    private void writeRGBPlanar(final ImagePlus imagePlus, final OutputStream outputStream) throws IOException {
        ImageStack stack = null;
        final int stackSize = imagePlus.getStackSize();
        if (stackSize > 1) {
            stack = imagePlus.getStack();
        }
        final int width = imagePlus.getWidth();
        final int height = imagePlus.getHeight();
        for (int i = 1; i <= stackSize; ++i) {
            final ColorProcessor colorProcessor = (ColorProcessor)((stackSize == 1) ? imagePlus.getProcessor() : ((ColorProcessor)stack.getProcessor(i)));
            final byte[] b = new byte[width * height];
            final byte[] b2 = new byte[width * height];
            final byte[] b3 = new byte[width * height];
            colorProcessor.getRGB(b, b2, b3);
            outputStream.write(b, 0, width * height);
            outputStream.write(b2, 0, width * height);
            outputStream.write(b3, 0, width * height);
            IJ.showProgress(i / (double)stackSize);
        }
    }
    
    private void writeInt(final DataOutputStream dataOutputStream, final int v) throws IOException {
        if (this.littleEndian) {
            final byte v2 = (byte)(v & 0xFF);
            final byte v3 = (byte)(v >> 8 & 0xFF);
            final byte v4 = (byte)(v >> 16 & 0xFF);
            final byte v5 = (byte)(v >> 24 & 0xFF);
            dataOutputStream.writeByte(v2);
            dataOutputStream.writeByte(v3);
            dataOutputStream.writeByte(v4);
            dataOutputStream.writeByte(v5);
        }
        else {
            dataOutputStream.writeInt(v);
        }
    }
    
    private void writeShort(final DataOutputStream dataOutputStream, final short v) throws IOException {
        if (this.littleEndian) {
            final byte v2 = (byte)(v & 0xFF);
            final byte v3 = (byte)(v >> 8 & 0xFF);
            dataOutputStream.writeByte(v2);
            dataOutputStream.writeByte(v3);
        }
        else {
            dataOutputStream.writeShort(v);
        }
    }
    
    private void writeFloat(final DataOutputStream dataOutputStream, final float value) throws IOException {
        this.writeInt(dataOutputStream, Float.floatToIntBits(value));
    }
    
    private void writeFloat(final DataOutputStream dataOutputStream, final double n) throws IOException {
        this.writeFloat(dataOutputStream, (float)n);
    }
    
    boolean is16BitSigned(final ImagePlus imagePlus) {
        if (imagePlus.getType() != 1) {
            return false;
        }
        int n = 65536;
        int n2 = 0;
        final ImageStack stack = imagePlus.getStack();
        final int n3 = imagePlus.getWidth() * imagePlus.getHeight();
        for (int i = 1; i <= stack.getSize(); ++i) {
            final short[] array = (short[])stack.getProcessor(i).getPixels();
            for (int j = 0; j < n3; ++j) {
                n = ((n < (array[j] & 0xFFFF)) ? n : (array[j] & 0xFFFF));
                n2 = ((n2 > (array[j] & 0xFFFF)) ? n2 : (array[j] & 0xFFFF));
            }
        }
        return n2 > 32767;
    }
    
    void add(final ImagePlus imagePlus, final int n) {
        final ImageStack stack = imagePlus.getStack();
        for (int i = 1; i <= stack.getSize(); ++i) {
            final short[] array = (short[])stack.getProcessor(i).getPixels();
            for (int j = 0; j < array.length; ++j) {
                array[j] = (short)((array[j] & 0xFFFF) + n);
            }
        }
    }
}
