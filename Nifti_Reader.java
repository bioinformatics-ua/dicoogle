import ij.process.ImageProcessor;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.io.FileInputStream;
import ij.io.FileOpener;
import java.io.IOException;
import java.io.File;
import ij.io.FileInfo;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.IJ;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;
import ij.ImagePlus;

// 
// Decompiled by Procyon v0.5.36
// 

public class Nifti_Reader extends ImagePlus implements PlugIn
{
    private boolean littleEndian;
    private boolean isNiftiData;
    private double cal_min;
    private double cal_max;
    private int nChannels;
    private int depth;
    private int frames;
    private boolean complex;
    private NiftiHeader nfti_hdr;
    
    public Nifti_Reader() {
        this.littleEndian = false;
        this.isNiftiData = false;
        this.cal_min = 0.0;
        this.cal_max = 0.0;
        this.nChannels = 1;
        this.depth = 1;
        this.frames = 1;
    }
    
    public void run(final String s) {
        final OpenDialog openDialog = new OpenDialog("Open Nifti...", s);
        final String directory = openDialog.getDirectory();
        final String fileName = openDialog.getFileName();
        if (fileName == null) {
            return;
        }
        IJ.showStatus("Opening: " + directory + fileName);
        ImagePlus imagePlus = this.load(directory, fileName);
        if (imagePlus != null) {
            if (this.complex && imagePlus.getStackSize() == 1) {
                imagePlus = this.splitComplexImage(imagePlus);
            }
            this.setStack(imagePlus.getTitle(), imagePlus.getStack());
            this.setFileInfo(imagePlus.getOriginalFileInfo());
            final Calibration calibration = imagePlus.getCalibration();
            boolean b = calibration.isSigned16Bit();
            if (b) {
                b = this.checkDataRange();
            }
            if (this.nfti_hdr != null) {
                final double[] array = { this.nfti_hdr.scl_inter, this.nfti_hdr.scl_slope };
                if (array[1] == 0.0) {
                    array[1] = 1.0;
                }
                if (b) {
                    final double[] array2 = array;
                    final int n = 0;
                    array2[n] -= 32768.0 * array[1];
                }
                calibration.setFunction(0, array, "gray value");
                this.cal_max = (this.cal_max - array[0]) / array[1];
                this.cal_min = (this.cal_min - array[0]) / array[1];
            }
            else if (b) {
                this.cal_max += 32768.0;
                this.cal_min += 32768.0;
            }
            if (this.cal_max != this.cal_min) {
                this.getProcessor().setMinAndMax(this.cal_min, (this.getType() == 2) ? this.cal_max : (this.cal_max - 1.0));
            }
            final ImageStack stack = this.getStack();
            if (!this.isNiftiData) {
                for (int i = 1; i <= stack.getSize(); ++i) {
                    stack.getProcessor(i).flipVertical();
                }
            }
            else {
                final CoordinateMapper[] coors = this.getCoors(this.nfti_hdr);
                if (coors != null) {
                    this.setProperty("coors", (Object)coors);
                }
                this.setProperty("nifti", (Object)this.nfti_hdr);
            }
            this.setCalibration(calibration);
            if (this.nChannels * this.depth * this.frames != stack.getSize()) {
                final int size = stack.getSize();
                this.nChannels = size / (this.depth * this.frames);
                if (this.nChannels == 0) {
                    this.nChannels = 1;
                    this.frames = size / this.depth;
                    if (this.frames == 0) {
                        this.frames = 1;
                        this.depth = size;
                    }
                }
                for (int j = this.nChannels * this.depth * this.frames; j < size; ++j) {
                    stack.deleteLastSlice();
                }
            }
            this.setDimensions(this.nChannels, this.depth, this.frames);
            if (this.nChannels != 1) {
                this.reshuffleStack(stack.getImageArray(), this.depth * this.frames, stack.getSize());
            }
            if (this.nChannels * this.frames != 1) {
                this.setOpenAsHyperStack(true);
            }
            if (s.equals("")) {
                this.show();
            }
        }
    }
    
    public ImagePlus load(String string, String s) {
        if (s == null || s == "") {
            return null;
        }
        FileInfo header = new FileInfo();
        String substring = "";
        if (s.endsWith(".gz") || s.endsWith(".GZ")) {
            substring = s.substring(s.length() - 3);
            s = s.substring(0, s.length() - 3);
        }
        String s2;
        String fileName;
        if (s.endsWith(".img") || s.endsWith(".hdr")) {
            s = s.substring(0, s.length() - 4);
            s2 = s + ".hdr" + substring;
            fileName = s + ".img" + substring;
        }
        else {
            s2 = s + substring;
            fileName = s + substring;
        }
        if (!string.endsWith(File.separator) && !string.equals("")) {
            string += File.separator;
        }
        IJ.showStatus("Reading Header File: " + string + s2);
        try {
            header = this.readHeader(string + s2);
            if (header == null) {
                return null;
            }
        }
        catch (IOException ex) {
            IJ.log("FileLoader: " + ex.getMessage());
        }
        if (this.isNiftiData) {
            IJ.showStatus("Reading Nifti File: " + string + fileName);
        }
        else {
            IJ.showStatus("Reading Analyze File: " + string + fileName);
        }
        header.fileName = fileName;
        header.directory = string;
        header.fileFormat = 1;
        return new FileOpener(header).open(false);
    }
    
    public FileInfo readHeader(final String name) throws IOException {
        final FileInputStream fileInputStream = new FileInputStream(name);
        DataInputStream dataInputStream;
        if (name.endsWith(".gz") || name.endsWith(".GZ")) {
            dataInputStream = new DataInputStream(new GZIPInputStream(fileInputStream));
        }
        else {
            dataInputStream = new DataInputStream(fileInputStream);
        }
        final FileInfo fileInfo = new FileInfo();
        final byte[] array = new byte[4];
        this.littleEndian = false;
        dataInputStream.readInt();
        for (int i = 0; i < 10; ++i) {
            dataInputStream.readByte();
        }
        for (int j = 0; j < 18; ++j) {
            dataInputStream.readByte();
        }
        dataInputStream.readInt();
        dataInputStream.readShort();
        dataInputStream.readByte();
        final byte byte1 = dataInputStream.readByte();
        final short[] dim = new short[8];
        dim[0] = this.readShort(dataInputStream);
        if (dim[0] < 0 || dim[0] > 7) {
            this.littleEndian = true;
            fileInfo.intelByteOrder = true;
            dim[0] >>= 8;
        }
        for (int k = 1; k < 8; ++k) {
            dim[k] = this.readShort(dataInputStream);
        }
        fileInfo.width = dim[1];
        fileInfo.height = dim[2];
        int nImages = 1;
        for (short n = 3; n <= dim[0]; ++n) {
            nImages *= dim[n];
        }
        fileInfo.nImages = nImages;
        dataInputStream.read(array, 0, 4);
        float intent_p1;
        if (this.littleEndian) {
            intent_p1 = Float.intBitsToFloat((array[3] & 0xFF) << 24 | (array[2] & 0xFF) << 16 | (array[1] & 0xFF) << 8 | (array[0] & 0xFF));
        }
        else {
            intent_p1 = Float.intBitsToFloat((array[0] & 0xFF) << 24 | (array[1] & 0xFF) << 16 | (array[2] & 0xFF) << 8 | (array[3] & 0xFF));
        }
        fileInfo.unit = new String(array, 0, 4).trim();
        final float float1 = this.readFloat(dataInputStream);
        final float float2 = this.readFloat(dataInputStream);
        final short short1 = this.readShort(dataInputStream);
        final short short2 = this.readShort(dataInputStream);
        final short short3 = this.readShort(dataInputStream);
        final short short4 = this.readShort(dataInputStream);
        final float[] pixdim = new float[8];
        for (int l = 0; l < 8; ++l) {
            pixdim[l] = this.readFloat(dataInputStream);
        }
        fileInfo.pixelWidth = pixdim[1];
        fileInfo.pixelHeight = pixdim[2];
        fileInfo.pixelDepth = pixdim[3];
        fileInfo.frameInterval = pixdim[4];
        fileInfo.offset = (int)this.readFloat(dataInputStream);
        final float float3 = this.readFloat(dataInputStream);
        final float float4 = this.readFloat(dataInputStream);
        final short short5 = this.readShort(dataInputStream);
        final byte byte2 = dataInputStream.readByte();
        final byte byte3 = dataInputStream.readByte();
        this.cal_max = this.readFloat(dataInputStream);
        this.cal_min = this.readFloat(dataInputStream);
        final float float5 = this.readFloat(dataInputStream);
        final float float6 = this.readFloat(dataInputStream);
        this.readInt(dataInputStream);
        this.readInt(dataInputStream);
        final byte[] bytes = new byte[80];
        for (int n2 = 0; n2 < 80; ++n2) {
            bytes[n2] = dataInputStream.readByte();
        }
        final String descrip = new String(bytes);
        final byte[] bytes2 = new byte[24];
        for (int n3 = 0; n3 < 24; ++n3) {
            bytes2[n3] = dataInputStream.readByte();
        }
        final String aux_file = new String(bytes2);
        final short short6 = this.readShort(dataInputStream);
        final short short7 = this.readShort(dataInputStream);
        final float float7 = this.readFloat(dataInputStream);
        final float float8 = this.readFloat(dataInputStream);
        final float float9 = this.readFloat(dataInputStream);
        final float float10 = this.readFloat(dataInputStream);
        final float float11 = this.readFloat(dataInputStream);
        final float float12 = this.readFloat(dataInputStream);
        final float[] srow_x = new float[4];
        final float[] srow_y = new float[4];
        final float[] srow_z = new float[4];
        for (int n4 = 0; n4 < 4; ++n4) {
            srow_x[n4] = this.readFloat(dataInputStream);
        }
        for (int n5 = 0; n5 < 4; ++n5) {
            srow_y[n5] = this.readFloat(dataInputStream);
        }
        for (int n6 = 0; n6 < 4; ++n6) {
            srow_z[n6] = this.readFloat(dataInputStream);
        }
        final byte[] bytes3 = new byte[16];
        for (int n7 = 0; n7 < 16; ++n7) {
            bytes3[n7] = dataInputStream.readByte();
        }
        final String intent_name = new String(bytes3);
        final byte[] bytes4 = new byte[4];
        for (int n8 = 0; n8 < 4; ++n8) {
            bytes4[n8] = dataInputStream.readByte();
        }
        final String s = new String(bytes4, 0, 3);
        if (bytes4[3] == 0 && (s.equals("ni1") || s.equals("n+1"))) {
            this.isNiftiData = true;
            this.nfti_hdr = new NiftiHeader();
            this.nfti_hdr.dim_info = byte1;
            this.nfti_hdr.dim = dim;
            this.nfti_hdr.intent_p1 = intent_p1;
            this.nfti_hdr.intent_p2 = float1;
            this.nfti_hdr.intent_p3 = float2;
            this.nfti_hdr.intent_code = short1;
            this.nfti_hdr.datatype = short2;
            this.nfti_hdr.bitpix = short3;
            this.nfti_hdr.slice_start = short4;
            this.nfti_hdr.pixdim = pixdim;
            this.nfti_hdr.vox_offset = (float)fileInfo.offset;
            this.nfti_hdr.scl_slope = float3;
            this.nfti_hdr.scl_inter = float4;
            this.nfti_hdr.slice_end = short5;
            this.nfti_hdr.slice_code = byte2;
            this.nfti_hdr.xyzt_units = byte3;
            this.nfti_hdr.cal_max = (float)this.cal_max;
            this.nfti_hdr.cal_min = (float)this.cal_min;
            this.nfti_hdr.slice_duration = float5;
            this.nfti_hdr.toffset = float6;
            this.nfti_hdr.glmax = 0;
            this.nfti_hdr.glmin = 0;
            this.nfti_hdr.descrip = descrip;
            this.nfti_hdr.aux_file = aux_file;
            this.nfti_hdr.qform_code = short6;
            this.nfti_hdr.sform_code = short7;
            this.nfti_hdr.quatern_b = float7;
            this.nfti_hdr.quatern_c = float8;
            this.nfti_hdr.quatern_d = float9;
            this.nfti_hdr.qoffset_x = float10;
            this.nfti_hdr.qoffset_y = float11;
            this.nfti_hdr.qoffset_z = float12;
            this.nfti_hdr.srow_x = srow_x;
            this.nfti_hdr.srow_y = srow_y;
            this.nfti_hdr.srow_z = srow_z;
            this.nfti_hdr.intent_name = intent_name;
        }
        else {
            this.isNiftiData = false;
        }
        dataInputStream.close();
        fileInputStream.close();
        switch (short2) {
            case 2: {
                fileInfo.fileType = 0;
                break;
            }
            case 4: {
                fileInfo.fileType = 1;
                break;
            }
            case 8: {
                fileInfo.fileType = 3;
                break;
            }
            case 16: {
                fileInfo.fileType = 4;
                break;
            }
            case 64: {
                fileInfo.fileType = 16;
                break;
            }
            case 128: {
                fileInfo.fileType = 7;
                break;
            }
            case 512: {
                fileInfo.fileType = 2;
                break;
            }
            case 768: {
                fileInfo.fileType = 11;
                break;
            }
            case 32: {
                fileInfo.fileType = 4;
                final FileInfo fileInfo2 = fileInfo;
                fileInfo2.width *= 2;
                this.complex = true;
                break;
            }
            default: {
                IJ.log("Data type " + short2 + " not supported\n");
                return null;
            }
        }
        if (dim[0] > 5 && dim[3] * dim[4] * dim[5] != fileInfo.nImages) {
            IJ.log(dim[0] + "-D data not supported\n");
        }
        else {
            this.depth = ((dim[0] < 3) ? 1 : dim[3]);
            this.frames = ((dim[0] < 4) ? 1 : dim[4]);
            this.nChannels = ((dim[0] < 5) ? 1 : dim[5]);
        }
        if (this.isNiftiData) {
            final int n9 = byte3 & 0x7;
            if (n9 == 1) {
                fileInfo.unit = "m";
            }
            else if (n9 == 2) {
                fileInfo.unit = "mm";
            }
            else if (n9 == 3) {
                fileInfo.unit = "um";
            }
            final int n10 = byte3 & 0x18;
            if (n10 == 16) {
                final FileInfo fileInfo3 = fileInfo;
                fileInfo3.frameInterval *= 0.001;
            }
            else if (n10 == 24) {
                final FileInfo fileInfo4 = fileInfo;
                fileInfo4.frameInterval *= 1.0E-6;
            }
        }
        return fileInfo;
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
    
    private CoordinateMapper[] getCoors(final NiftiHeader niftiHeader) {
        CoordinateMapper coordinateMapper = null;
        CoordinateMapper coordinateMapper2 = null;
        if (niftiHeader.qform_code != 0) {
            coordinateMapper = new QuaternCoors(new double[] { niftiHeader.pixdim[0], 0.0, niftiHeader.quatern_b, niftiHeader.quatern_c, niftiHeader.quatern_d }, new double[] { niftiHeader.pixdim[1], niftiHeader.pixdim[2], niftiHeader.pixdim[3] }, new double[] { niftiHeader.qoffset_x, niftiHeader.qoffset_y, niftiHeader.qoffset_z }, 1, NiftiHeader.getCoorTypeString(niftiHeader.qform_code));
        }
        if (niftiHeader.sform_code != 0) {
            final double[][] array = new double[3][4];
            for (int i = 0; i < 4; ++i) {
                array[0][i] = niftiHeader.srow_x[i];
                array[1][i] = niftiHeader.srow_y[i];
                array[2][i] = niftiHeader.srow_z[i];
            }
            coordinateMapper2 = new AffineCoors(array, 1, NiftiHeader.getCoorTypeString(niftiHeader.sform_code));
        }
        if (coordinateMapper == null && coordinateMapper2 == null) {
            return null;
        }
        if (coordinateMapper != null && coordinateMapper2 == null) {
            return new CoordinateMapper[] { coordinateMapper };
        }
        if (coordinateMapper == null && coordinateMapper2 != null) {
            return new CoordinateMapper[] { coordinateMapper2 };
        }
        return new CoordinateMapper[] { coordinateMapper, coordinateMapper2 };
    }
    
    public boolean checkDataRange() {
        int n = 65536;
        int n2 = 0;
        final ImageStack stack = this.getStack();
        final int n3 = this.getWidth() * this.getHeight();
        for (int i = 1; i <= stack.getSize(); ++i) {
            final short[] array = (short[])stack.getProcessor(i).getPixels();
            for (int j = 0; j < n3; ++j) {
                n = ((n < (array[j] & 0xFFFF)) ? n : (array[j] & 0xFFFF));
                n2 = ((n2 > (array[j] & 0xFFFF)) ? n2 : (array[j] & 0xFFFF));
            }
        }
        if (n >= 32768) {
            for (int k = 1; k <= stack.getSize(); ++k) {
                final short[] array2 = (short[])stack.getProcessor(k).getPixels();
                for (int l = 0; l < n3; ++l) {
                    array2[l] = (short)((array2[l] & 0xFFFF) - 32768);
                }
            }
            final ImageProcessor processor = this.getProcessor();
            processor.setMinAndMax(processor.getMin() - 32768.0, processor.getMax() - 32768.0);
            return false;
        }
        return true;
    }
    
    ImagePlus splitComplexImage(final ImagePlus imagePlus) {
        final int width = imagePlus.getWidth();
        final int height = imagePlus.getHeight();
        final ImageProcessor processor = imagePlus.getProcessor();
        processor.setInterpolationMethod(0);
        final ImageProcessor resize = processor.resize(width / 2, height);
        IJ.run(imagePlus, "Canvas Size...", "width=" + (width + 1) + " height=" + height + " position=Top-Left zero");
        final ImageProcessor processor2 = imagePlus.getProcessor();
        processor2.setRoi(1, 0, width, height);
        final ImageProcessor resize2 = processor2.resize(width / 2, height);
        final ImageStack imageStack = new ImageStack(width / 2, height);
        imageStack.addSlice("re", resize);
        imageStack.addSlice("im", resize2);
        imagePlus.setStack((String)null, imageStack);
        return imagePlus;
    }
    
    public int readInt(final DataInputStream dataInputStream) throws IOException {
        if (!this.littleEndian) {
            return dataInputStream.readInt();
        }
        return (dataInputStream.readByte() & 0xFF) << 24 | (dataInputStream.readByte() & 0xFF) << 16 | (dataInputStream.readByte() & 0xFF) << 8 | (dataInputStream.readByte() & 0xFF);
    }
    
    public short readShort(final DataInputStream dataInputStream) throws IOException {
        if (!this.littleEndian) {
            return dataInputStream.readShort();
        }
        return (short)((dataInputStream.readByte() & 0xFF) << 8 | (dataInputStream.readByte() & 0xFF));
    }
    
    public float readFloat(final DataInputStream dataInputStream) throws IOException {
        if (!this.littleEndian) {
            return dataInputStream.readFloat();
        }
        return Float.intBitsToFloat(this.readInt(dataInputStream));
    }
}
