/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle.
 *
 * Dicoogle/dicoogle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.rGUI.RFileBrowser;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.plaf.metal.MetalIconFactory;

/**
 * Icons Renderes to the Remote File Chooser
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class IconListRenderer extends DefaultListCellRenderer {

    private Map<Object, Icon> icons = null;

    public IconListRenderer() {
        icons = new HashMap<Object, Icon>();

        icons.put("file",
                MetalIconFactory.getTreeLeafIcon());

        icons.put("folder",
                MetalIconFactory.getTreeFolderIcon());
        icons.put("computer",
                MetalIconFactory.getTreeComputerIcon());

    }

    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {



        // Get the renderer component from parent class
        JLabel label =
                (JLabel) super.getListCellRendererComponent(list,
                value, index, isSelected, cellHasFocus);



        RemoteFile file = (RemoteFile) value;
        String type = "file";
        
        if(file != null && file.isDirectory())
            type = "folder";

        // Get icon to use for the list item value
        Icon icon = icons.get(type);

        // Set icon to display for value
        label.setIcon(icon);

        return label;
    }
}
