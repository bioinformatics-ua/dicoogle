/*
 * Copyright (c) 2014 BMD Software, Lda.
 * All Rights Reserved
 *
 * All information contained herein is, and remains the property of BMD Software, Lda. and its suppliers, if any.
 * The intellectual and technical concepts contained herein are proprietary to BMD Software, Lda. and its suppliers,
 * being protected by trade secret or copyright law. Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission is obtained from BMD Software, Lda.
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
