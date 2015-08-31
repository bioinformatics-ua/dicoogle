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

package pt.ua.dicoogle.core.dim;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import pt.ua.dicoogle.sdk.Utils.Platform;


/**
 *
 * @author bastiao
 */
public class ConcatTags
{

    public static final String FILENAME= Platform.homePath() + "concatTags.conf";

    /**
     * @return the rules
     */
    public List<Rule> getRules() {
        return rules;
    }

    /**
     * @param rules the rules to set
     */
    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public class Rule
    {

        private String modality;
        private String tagToReplace;

        /**
         * @return the modality
         */
        public String getModality() {
            return modality;
        }

        /**
         * @param modality the modality to set
         */
        public void setModality(String modality) {
            this.modality = modality;
        }

        /**
         * @return the tagToReplace
         */
        public String getTagToReplace() {
            return tagToReplace;
        }

        /**
         * @param tagToReplace the tagToReplace to set
         */
        public void setTagToReplace(String tagToReplace) {
            this.tagToReplace = tagToReplace;
        }

    }

    private List<Rule> rules = new ArrayList<Rule>();


    public void parseConfig(String file) throws FileNotFoundException
    {

        StringBuilder text = new StringBuilder();
        String NL = System.getProperty("line.separator");
        Scanner scanner = new Scanner(new FileInputStream(file));
        try {
            while (scanner.hasNextLine()){

                Rule r = parseLine(scanner.nextLine());
                if (r!=null)
                {
                    this.rules.add(r);
                }
            }
        }
        finally{
            scanner.close();
        }

    }

    // Sample: CR;StudyDescription;EMPTY;AquisitionDeviceProcessionDescription;StudyDescription==staff

    public Rule parseLine(String line)
    {
        if (line==null || line.equals(""))
            return null;
        System.out.println(line);
        Rule r = new Rule();
        String [] tmp = line.split(";");
        for (int i = 0;i<tmp.length;i++)
            System.out.println(tmp[i]);
        String modality = tmp[0];
        String tag = tmp[3];
        r.setModality(modality);
        r.setTagToReplace(tag);
        return r;


    }

}
