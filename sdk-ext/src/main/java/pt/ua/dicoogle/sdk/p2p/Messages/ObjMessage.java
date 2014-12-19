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
package pt.ua.dicoogle.sdk.p2p.Messages;

/**
 * Object to be sent
 * @author Carlos Ferreira
 * @author Pedro Bento
 */
public class ObjMessage<type> implements MessageI<type>
{
    private type message;
    private String Type;
    
    public ObjMessage(type message, String Type)
    {
        this.message = message;
        this.Type = Type;
    }

    public type getMessage()
    {
        return this.message;
    }

    public String getType()
    {
        return this.Type;
    }
}
