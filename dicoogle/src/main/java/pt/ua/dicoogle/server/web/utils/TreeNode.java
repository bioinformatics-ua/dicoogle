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
package pt.ua.dicoogle.server.web.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * This is just a helper class to make it a lot easier to addMoveDestination the data onto the
 * treeview in html form the data gets sent to one of these first.
 *
 * @author Antonio
 */
public class TreeNode<T>
{
	private String name; // the name of this node
	private T data; // some data attached to it

	private TreeNode<T> parent;
	private List<TreeNode<T>> children;

	public TreeNode(String name, T data, TreeNode<T> parent)
	{
		this.name = name;
		this.data = data;

		this.parent = parent;
		this.children = new ArrayList<TreeNode<T>>();
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the data
	 */
	public T getData()
	{
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(T data)
	{
		this.data = data;
	}

	/**
	 * @return the parent
	 */
	public TreeNode<T> getParent()
	{
		return parent;
	}

	/**
	 * @return the child count
	 */
	public int getChildCount()
	{
		return children.size();
	}

	/**
	 * Returns the i-child.
	 *
	 * @param i the index of the child to get.
	 * @return a TreeNode object or null if i is not a valid index.
	 */
	public TreeNode<T> getChild(int i)
	{
		// if it's not a valid index return null
		if ((i < 0) || (i >= children.size()))
			return null;

		return children.get(i);
	}

	/**
	 * Returns the child with the specified name.
	 *
	 * @param name the name of the child to get.
	 * @return a TreeNode object or null if the name is not a valid/found child.
	 */
	public TreeNode<T> getChild(String name)
	{
		// if there are no children
		if (children.size() < 1)
			return null;

		for (TreeNode<T> child : children)
		{
			if (child.getName().equals(name))
				return child;
		}

		// child not found
		return null;
	}

	/**
	 * Adds a child to this node.
	 *
	 * @param name the name of the child.
	 * @param data the data of the child.
	 * @return the child inserted.
	 */
	public TreeNode<T> addChild(String name, T data)
	{
		TreeNode<T> child = new TreeNode<T>(name, data, this);
		this.children.add(child);
		return child;
	}

	/**
	 * Based on a path-alike set of dir entries, returns the final node of it.
	 * If the path doesn't exist, it's created.
	 *
	 * @param names a set of TreeNode entries.
	 * @return the last TreeNode of the path.
	 */
	public TreeNode<T> getTreeNode(String... names)
	{
		TreeNode<T> node = this;

		for (String aName : names)
		{
			// get the child node with this name was found
			TreeNode<T> child = node.getChild(aName);

			// if it was not found the create it
			if (child == null)
			{
				child = node.addChild(aName, null);
			}

			// now that we are absolutely sure that the child node exists move onto it, so we can continuing parsing the next part of the "path"
			node = child;
		}

		return node;
	}
}
