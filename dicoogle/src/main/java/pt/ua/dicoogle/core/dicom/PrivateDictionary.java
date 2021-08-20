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
package pt.ua.dicoogle.core.dicom;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import org.slf4j.LoggerFactory;

import pt.ua.dicoogle.sdk.utils.TagValue;
import pt.ua.dicoogle.sdk.utils.TagsStruct;

/**
 *
 * @author bastiao
 */
public class PrivateDictionary {

    public void parse(String file) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            LoggerFactory.getLogger(PrivateDictionary.class).error(ex.getMessage(), ex);
        }
        try {
            while (scanner.hasNextLine()) {
                String text = scanner.nextLine();
                String tag = "";

                String type = "";
                String name = "";


                if (text.startsWith("(")) {
                    String txt[] = text.split(" |\t");

                    for (int i = 0; i < txt.length; i++) {
                        if (txt[i].startsWith("(")) {
                            tag = txt[i];
                        } else if (txt[i].length() == 2) {
                            type = txt[i];
                            name = txt[i + 1];
                        }
                    }
                }
                if (!tag.equals("") && !type.equals("") && !name.equals("")) {
                    // System.out.println("Tag: "+tag);
                    // System.out.println("Type: "+type);
                    // System.out.println("Name: "+name);

                    TagsStruct tg = TagsStruct.getInstance();
                    tag = tag.replaceAll("\\(", "");
                    tag = tag.replaceAll("\\)", "");
                    tag = tag.replaceAll(" ", "");
                    tag = tag.replaceAll(",", "");
                    // System.out.println("Tag: "+tag);
                    // System.out.println("Type: "+type);
                    // System.out.println("Name: "+name);

                    TagValue v = new TagValue(Integer.parseInt(tag, 16), name);

                    v.setVR(type);
                    tg.addPrivateField(v);

                }
            }
        } finally {
            scanner.close();
        }
    }

    public static void main(String[] args) {
        PrivateDictionary pd = new PrivateDictionary();
        pd.parse("/Users/bastiao/MAP-I/Code/dicomlamedictionaryanddicom/similarity.dic");


    }

}
