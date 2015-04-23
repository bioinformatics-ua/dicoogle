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
/** 
 * Based on a String it will able to separate the strings
 * and it will be easly extended
 *
 * NOTE:
 * The main proposal of the class is that Search GUI call it
 * and further for QueryRetrieve Service
 * 
 */
package pt.ua.dicoogle.core;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import pt.ua.dicoogle.sdk.utils.TagsStruct;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class QueryExpressionBuilder
{

    private ArrayList <String> tokens = null ;

    private ArrayList <String> tags = null ;



    public QueryExpressionBuilder(String text, ArrayList tags)
    {
        this(text);
        this.tags = tags ;
    }


    public QueryExpressionBuilder(String freetext)
    {

            setTokens(new ArrayList<String>());

            freetext = freetext.replace("^", " ");

             StringTokenizer st = new StringTokenizer(freetext);

             /** XXX probably it can be improved, there're some strange charsets
              * put it up set
              */

             while (st.hasMoreTokens())
             {
               String ss = st.nextToken();
               if (ss.contains("^"))
               {
                    for (String newToken : ss.split("^"))
                        tokens.add(newToken);

               }
               else
                tokens.add(ss);
             }


             /**
              * Get tags ;
              * it it allocated in Run Time, so I have instance access to
              * singletone
              */

             TagsStruct _tags = TagsStruct.getInstance();

             this.tags = _tags.getDIMAlias();

    }

    /**
     * @return the tokens
     */
    public ArrayList<String> getTokens()
    {
        return tokens;
    }

    /**
     * @param tokens the tokens to set
     */
    public void setTokens(ArrayList<String> tokens)
    {
        this.tokens = tokens;
    }


    public String getQueryString()
    {
        /** It will be used to call the Lucene - Indexer
         * It crucial respect the BNF grammer able to search in library
         */
        String queryString = "" ;


        /** Search in Lucene in freetext is non-Trivial
         *  There are some constrains by the library Lunce 2.X
         *  it was implemented with the repeat of fields
         *
         *  Get it for free text: "Smith CT", where the tags domain is:
         *  PatientName and Modality
         *
         *  the result should be:
         *
         *  (PatientName=Smith OR Modality=Smith) OR
         *  (PatientName=CT OR Modality=CT)
         *
         *  More general expression was:
         *  (field1=a1 or field2=a1 or field3=a3 .. or fieldn=an) or
         *  (...)
         *  (field1=z1 or field2=z2 or field3=z3 .. or fiendn=zn)
         *
         *  This way the search will be in free text really
         *
         */

        Iterator<String> itTokens = tokens.iterator();
        Iterator<String> itTags;

        String token = null ;

        /** Build the query string
         *
         * Iterating for each token
         */
        while(itTokens.hasNext())
        {
            /**
             * Iterating each tags
             */
            queryString += "(";
            String tag = null ;

            token = itTokens.next();
            itTags = tags.iterator();
            while (itTags.hasNext())
            {

                tag = itTags.next();
                queryString += tag+":"+token ;
                /**
                 * If it have next then the logical condition will continue
                 * in next iteration
                 */
                if (itTags.hasNext())
                {
                    queryString += " OR ";
                }
            }
            queryString += "OR others:" + token + " )";

            if (itTokens.hasNext())
            {
                queryString += " AND ";
            }

        }

        return queryString;
    }

    /**
     * @return the tags
     */
    public ArrayList<String> getTags()
    {
        return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(ArrayList<String> tags)
    {
        this.tags = tags;
    }

}
