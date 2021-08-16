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
package pt.ua.dicoogle.sdk.utils.query;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import pt.ua.dicoogle.sdk.core.DicooglePlatformInterface;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.task.JointQueryTask;
import pt.ua.dicoogle.sdk.task.Task;

/**
 * This class provides a very easy interface for querying in the Dicoogle.  
 * Especially in big data scenarios where users should be aware of memory restrictions. 
 * The usage of an iterative approach is enforced by the adapter. As such, query interface users are not tempted to use the tasks bulk get method.
 * The query's business logic should be as much encompassed in the adapter as possible.
 * 
 * @author Tiago Marques Godinho, tmgodinho@ua.pt
 *
 */
public class Query extends JointQueryTask {

    private DicooglePlatformInterface controller;

    private ForEachAdapter adapter;
    private CountDownLatch latch;

    /**
     * Initializes this helper class.
     * 
     * @param controller The dicoogle platform proxy.
     * @param adapter The adapter holding the business logic of the query.
     */
    public Query(DicooglePlatformInterface controller, ForEachAdapter adapter) {
        super();
        this.adapter = adapter;
        this.controller = controller;
        this.latch = new CountDownLatch(1);
    }

    @Override
    public void onCompletion() {
        this.latch.countDown();
    }

    @Override
    public void onReceive(Task<Iterable<SearchResult>> e) {

        Iterable<SearchResult> rs = null;
        try {
            rs = e.get();
        } catch (InterruptedException | ExecutionException e1) {
            e1.printStackTrace();
        }

        for (SearchResult r : rs) {
            this.adapter.forEach(r);
        }
    }

    /**
     * Awaits for the query to finish.
     */
    public void await() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * This method provides the same interface as the Dicoogle Platform Interface.
     * The await method is called after the query is performed. So this is a blocking method.
     * 
     * @see DicooglePlatformInterface
     * 
     * @param query Query String
     * @param parameters Parameters
     */
    public void queryAll(String query, Object... parameters) {
        @SuppressWarnings("unused") JointQueryTask task = controller.queryAll(this, query, parameters);
        await();
    }

    /**
     * This method provides the same interface as the Dicoogle Platform Interface.
     * The await method is called after the query is performed. So this is a blocking method.
     * 
     * @see DicooglePlatformInterface
     * 
     * @param querySource The query plugin's name
     * @param query Query String
     * @param parameters Parameters
     */
    public void query(String querySource, String query, Object... parameters) {
        List<String> querySources = new ArrayList<String>(1);
        querySources.add(querySource);

        this.query(querySources, query, parameters);
    }

    /**
     * This method provides the same interface as the Dicoogle Platform Interface.
     * The await method is called after the query is performed. So this is a blocking method.
     * 
     * @see DicooglePlatformInterface
     * 
     * @param querySources A List holding the queries plugin's names that should handle the query.
     * @param query Query String
     * @param parameters Parameters
     */
    public void query(List<String> querySources, String query, Object... parameters) {
        @SuppressWarnings("unused") JointQueryTask task = controller.query(this, querySources, query, parameters);

        await();
    }

    /**
     * Sets the Dicoogle Platform Controller.
     * @param controller The new controller.
     */
    public void setController(DicooglePlatformInterface controller) {
        this.controller = controller;
    }

    /**
     * Sets the for each adapter.
     * 
     * @param adapter The adapter.
     */
    public void setAdapter(ForEachAdapter adapter) {
        this.adapter = adapter;
    }
}
