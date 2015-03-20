/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle-sdk.
 *
 * Dicoogle/dicoogle-sdk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle-sdk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */

package pt.ua.dicoogle.sdk.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author Frederico Valente <fmvalente@ua.pt>
 * 
 * This class creates a single iterator from a list of iterators to the same underlying object types
 * Essentially allows us to merge collections
 * @param <T> element of the collection we are iterating over
 * 
 */
public class Multirator<T> implements Iterator<T> {

    ArrayList<Iterable<T>> elementSources = new ArrayList<>();
    int currentElement;
    Iterator<T> currentElementIter;

    public Multirator(Iterable<Iterable<T>> collectionOfIterables) {
        for (Iterable<T> iter : collectionOfIterables) {
            elementSources.add(iter);
        }
        currentElement = 0;
        currentElementIter = elementSources.get(currentElement).iterator();
    }

    @Override
    public boolean hasNext() {
        boolean hasNxt = currentElementIter.hasNext();
        if (hasNxt) {return true;}

        currentElement++;
        if (currentElement == elementSources.size()) {return false;}
        
        currentElementIter = elementSources.get(currentElement).iterator();
        return hasNext();
    }

    @Override
    public T next() {
        boolean hasNxt = currentElementIter.hasNext();
        if (hasNxt) {return currentElementIter.next();}

        currentElement++;
        if (currentElement == elementSources.size()) {throw new NoSuchElementException();}

        currentElementIter = elementSources.get(currentElement).iterator();
        return next();
    }
}
