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
package pt.ua.dicoogle.server.web.auth;

import pt.ua.dicoogle.server.users.*;

/**
 * Provides login routines for users.
 *
 * @author António Novo <antonio.novo@ua.pt>
 */
public class Authentication
{
	private static Authentication instance = null;
	private static UsersStruct users;

	private Authentication()
	{
		RolesXML rolesXML = new RolesXML();
		RolesStruct rolesStruct = rolesXML.getXML();
		// init the user list, if it wasn't done yet
		UsersXML usersXML = new UsersXML();
		usersXML.getXML();

		// gets the instance of the user list
		users = UsersStruct.getInstance();
	}

	/**
	 * Returns the current instance of the authentication singleton.
	 *
	 * @return the current instance of the authentication singleton.
	 */
	public static synchronized Authentication getInstance()
	{
		if (instance == null)
			instance = new Authentication();

		return instance;
	}

	/**
	 * Attemps to login on the plataform.
	 *
	 * @param username the user name of the user to login.
	 * @param password the clear text password of the user.
	 * @return a Login object if successful login, null otherwise.
	 */
	public LoggedIn login(String username, String password)
	{
		// must have both username and password
		if ((username == null) || (password == null))
			return null;

		// check if the user exists in the user list
		User user = users.getUser(username);
		if (user == null)
			return null;

		// calculate the supplied passwords hash and see if it matches the users
		String passwordHash = HashService.getSHA1Hash(password);
		if (! user.verifyPassword(passwordHash))
			return null;

		// return a successfull login object
		return new LoggedIn(username, user.isAdmin());
	}
}
