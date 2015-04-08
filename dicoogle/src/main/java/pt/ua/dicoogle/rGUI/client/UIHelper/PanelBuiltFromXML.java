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
package pt.ua.dicoogle.rGUI.client.UIHelper;

import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Carlos Ferreira
 */
@Deprecated
public class PanelBuiltFromXML extends JPanel
{
    private List<ComponentFromXML> components;

    public PanelBuiltFromXML(byte[] xml)
    {
        SAXReader saxReader = new SAXReader();

        Document document = null;
        try
        {
            document = saxReader.read(new ByteArrayInputStream(xml));
        } catch (DocumentException ex)
        {
            ex.printStackTrace(System.out);
        }

        Element root = document.getRootElement();
        if (root.getName().compareTo("jpanel") != 0)
        {
            return;
        }
        List<Element> elements = root.elements("component");
        this.components = new ArrayList<ComponentFromXML>();

        for (Element e : elements)
        {
            ComponentFromXML newComponent = new ComponentFromXML(e);
            if (newComponent.isValidComponent())
            {
                this.components.add(newComponent);
            }

        }
    }

    private class ComponentFromXML extends JComponent
    {

        private String name;
        private JComponent component;
        private boolean valid = false;

        public ComponentFromXML(Element element)
        {
            Element tmp = element.element("name");
            if (tmp.getText() == null)
            {
                return;
            }
            this.name = tmp.getText();

            tmp = element.element("type");
            if (tmp.getText() == null)
            {
                return;
            }

            if (tmp.getText().compareTo("Integer") == 0)
            {
                int minimum = Integer.MIN_VALUE;
                int maximum = Integer.MAX_VALUE;
                tmp = element.element("min");
                if (tmp.getText() != null)
                {
                    minimum = Integer.parseInt(tmp.getText());
                }
                tmp = element.element("max");
                if (tmp.getText() != null)
                {
                    maximum = Integer.parseInt(tmp.getText());
                }

                JTextField newcomponent = new JTextField();

                newcomponent.addActionListener(new IntegerListener(minimum, maximum, newcomponent));

                this.component = newcomponent;
            }
            if (tmp.getText().compareTo("Enum") == 0)
            {
                List<Element> tmpelems = tmp.elements("constant");
                if ((tmpelems == null) || tmpelems.isEmpty())
                {
                    return;
                }
                String strings[] = new String[tmpelems.size()];
                int i = 0;
                for (Element el : tmpelems)
                {
                    strings[i] = el.getText();
                    i++;
                }
            }

            this.valid = true;
        }

        public boolean isValidComponent()
        {
            return this.valid;
        }
    }

    private class IntegerListener implements java.awt.event.ActionListener
    {

        private int minimum = Integer.MIN_VALUE;
        private int maximum = Integer.MAX_VALUE;
        private JTextField component;

        public IntegerListener(int minimum, int maximum, JTextField component)
        {
            if (minimum < maximum)
            {
                this.minimum = minimum;
                this.maximum = maximum;
            }
            this.component = component;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            String text = this.component.getText();
            int i = Integer.parseInt(text);

            if (i < this.minimum)
            {
                this.component.setText(Integer.toString(this.minimum));
            }
            if (i > this.maximum)
            {
                this.component.setText(Integer.toString(this.maximum));
            }
            this.component.setText(Integer.toString(i));
        }
    }
}
