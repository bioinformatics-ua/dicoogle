import ij.IJ;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.Panel;
import java.awt.Button;
import ij.WindowManager;
import java.awt.Component;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.LayoutManager;
import java.awt.GridBagLayout;
import ij.gui.ImageCanvas;
import ij.ImagePlus;
import java.awt.Label;
import java.awt.event.ActionListener;
import java.awt.event.MouseMotionListener;
import ij.plugin.frame.PlugInFrame;

// 
// Decompiled by Procyon v0.5.36
// 

public class Coordinate_Viewer extends PlugInFrame implements MouseMotionListener, ActionListener
{
    private Label imageName;
    private ImagePlus currentImage;
    private ImageCanvas currentCanvas;
    private CoordinateMapper[] mapper;
    private Label[] coors;
    
    public Coordinate_Viewer() {
        super("Coordinate Viewer");
        this.setUpWindow();
        this.setVisible(true);
    }
    
    private void setUpWindow() {
        this.removeAll();
        this.setLayout((LayoutManager)new GridBagLayout());
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagConstraints.fill = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.anchor = 18;
        this.add((Component)new Label("Image: "), (Object)gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 1.0;
        this.currentImage = WindowManager.getCurrentImage();
        this.add((Component)(this.imageName = new Label((this.currentImage != null) ? this.currentImage.getTitle() : "")), (Object)gridBagConstraints);
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = 12;
        final Button button = new Button("Set Image");
        gridBagConstraints.fill = 3;
        this.add((Component)button, (Object)gridBagConstraints);
        button.addActionListener(this);
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = 1;
        this.pack();
        if (this.currentImage == null) {
            gridBagConstraints.weighty = 1.0;
            this.add((Component)new Panel(), (Object)gridBagConstraints);
            return;
        }
        final Object property = this.currentImage.getProperty("coors");
        if (property == null) {
            return;
        }
        if (!(property instanceof CoordinateMapper[])) {
            gridBagConstraints.weighty = 1.0;
            this.add((Component)new Panel(), (Object)gridBagConstraints);
            return;
        }
        this.add((Component)new Label(" "), (Object)gridBagConstraints);
        this.mapper = (CoordinateMapper[])property;
        this.coors = new Label[this.mapper.length];
        gridBagConstraints.gridy = 3;
        for (int i = 0; i < this.mapper.length; ++i) {
            gridBagConstraints.anchor = 17;
            gridBagConstraints.gridwidth = 1;
            this.add((Component)new Label(this.mapper[i].getName() + ":"), (Object)gridBagConstraints);
            if (this.mapper[i].getCoorType() != 0) {
                final GridBagConstraints gridBagConstraints2 = gridBagConstraints;
                ++gridBagConstraints2.gridy;
                gridBagConstraints.gridx = 0;
                gridBagConstraints.anchor = 13;
                this.add((Component)new Label(" x increases from ", 2), (Object)gridBagConstraints);
                gridBagConstraints.gridx = 1;
                gridBagConstraints.anchor = 17;
                this.add((Component)new Label(this.mapper[i].getXDescription()), (Object)gridBagConstraints);
                final GridBagConstraints gridBagConstraints3 = gridBagConstraints;
                ++gridBagConstraints3.gridy;
                gridBagConstraints.gridx = 0;
                gridBagConstraints.anchor = 13;
                this.add((Component)new Label(" y increases from ", 2), (Object)gridBagConstraints);
                gridBagConstraints.gridx = 1;
                gridBagConstraints.anchor = 17;
                this.add((Component)new Label(this.mapper[i].getYDescription()), (Object)gridBagConstraints);
                final GridBagConstraints gridBagConstraints4 = gridBagConstraints;
                ++gridBagConstraints4.gridy;
                gridBagConstraints.gridx = 0;
                gridBagConstraints.anchor = 13;
                this.add((Component)new Label(" z increases from ", 2), (Object)gridBagConstraints);
                gridBagConstraints.gridx = 1;
                gridBagConstraints.anchor = 17;
                this.add((Component)new Label(this.mapper[i].getZDescription()), (Object)gridBagConstraints);
            }
            gridBagConstraints.gridx = 0;
            final GridBagConstraints gridBagConstraints5 = gridBagConstraints;
            ++gridBagConstraints5.gridy;
            gridBagConstraints.anchor = 11;
            gridBagConstraints.gridwidth = 2;
            this.add((Component)(this.coors[i] = new Label("(0.00,0.00,0.00)", 1)), (Object)gridBagConstraints);
            final GridBagConstraints gridBagConstraints6 = gridBagConstraints;
            ++gridBagConstraints6.gridy;
        }
        gridBagConstraints.weighty = 1.0;
        this.add((Component)new Panel(), (Object)gridBagConstraints);
        (this.currentCanvas = this.currentImage.getWindow().getCanvas()).addMouseMotionListener((MouseMotionListener)this);
        this.pack();
    }
    
    public void actionPerformed(final ActionEvent actionEvent) {
        if (this.currentCanvas != null) {
            this.currentCanvas.removeMouseMotionListener((MouseMotionListener)this);
        }
        this.setUpWindow();
    }
    
    public void mouseDragged(final MouseEvent mouseEvent) {
    }
    
    public void mouseMoved(final MouseEvent mouseEvent) {
        final int offScreenX = this.currentCanvas.offScreenX(mouseEvent.getX());
        final int offScreenY = this.currentCanvas.offScreenY(mouseEvent.getY());
        final int n = this.currentImage.getSlice() - 1;
        for (int i = 0; i < this.mapper.length; ++i) {
            this.coors[i].setText("(" + IJ.d2s(this.mapper[i].getX(offScreenX, offScreenY, n)) + "," + IJ.d2s(this.mapper[i].getY(offScreenX, offScreenY, n)) + "," + IJ.d2s(this.mapper[i].getZ(offScreenX, offScreenY, n)) + ")");
            this.coors[i].repaint();
        }
    }
}
