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
package pt.ua.dicoogle.sdk.observables;

import java.util.Observable;

/**
 * Observable responsible for the notification of user interfaces about a message received.
 * @author Pedro Bento
 * @author Carlos Ferreira
 */
public class MessageObservable<I> extends Observable
{
    //The last message received.

    private I Message;
    private String address = null;

    /**
     * Constructor that just initialize the message.
     */
    public MessageObservable()
    {
        this.Message = null;
        this.address = null;
    }

    /**
     * Getter of the message.
     * @return the last message received.
     */
    public I getMessage()
    {
        return this.Message;
    }

    public String getAddress()
    {
        return address;
    }


    public I getObject()
    {
        return this.Message;
    }

    /**
     * Setter of the message and consequent notification of all observers.
     * @param Message New message of the observer.
     */
    public void setMessage(I Message, String address)
    {
        this.Message = Message;
        this.address = address;
        this.setChanged();
        this.notifyObservers();
    }
}
