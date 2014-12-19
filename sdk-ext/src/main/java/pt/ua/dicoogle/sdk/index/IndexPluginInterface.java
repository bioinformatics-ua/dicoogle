/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle-sdk-ext.
 *
 * Dicoogle/dicoogle-sdk-ext is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle-sdk-ext is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.sdk.index;

import java.util.List;
import java.util.Set;
import pt.ua.dicoogle.sdk.GenericPluginInterface;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;

/**
 * This interface contains all methods to be implemented for Core entities.
 * All Index extensions and IndexEngine should implement it
 *
 * 
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public interface IndexPluginInterface extends GenericPluginInterface
{
    public  void index(IDoc doc);
    public void index(List<IDoc> docs);
    public void optimize();
    
    public List<SearchResult> searchSync(String query,  List<String> extrafields);
    
    public Set<String> enumField(String fieldName, boolean isFloat);
    public int countResults(String query);

}
