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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Provides login routines for users.
 *
 * @author António Novo <antonio.novo@ua.pt>
 */
public class Authentication
{
	private static Authentication instance = null;
	private final UsersStruct users;

	private final Map<String, String> usersToken = new HashMap<>();
	private final Map<String, String> tokenUsers = new HashMap<>();

	private Authentication()
	{
		RolesXML rolesXML = new RolesXML();
		RolesStruct rolesStruct = rolesXML.getXML();
		// init the user list, if it wasn't done yet

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


	public User getUsername(String token)
	{
		String user = tokenUsers.get(token);
		if (user==null)
			return null;
		return UsersStruct.getInstance().getUser(user);

	}

	public void logout(String token){
		String user = tokenUsers.get(token);
		String ntoken = usersToken.get(user);
		tokenUsers.remove(ntoken);
		usersToken.remove(user);

	}

	/**
	 * Attempts to login on the platform.
	 *
	 * @param username the user name of the user to login.
	 * @param password the clear text password of the user.
	 * @return a LoggedIn object if successful login, null otherwise.
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

		if (! user.verifyPassword(password))
			return null;
		LoggedIn in = new LoggedIn(username, user.isAdmin());
		if (usersToken.containsKey(username))
			in.setToken(usersToken.get(username));

		else {
			String token = UUID.randomUUID().toString();
			usersToken.put(username, token);
			tokenUsers.put(token, username);
			in.setToken(usersToken.get(username));
		}
		// return a successfull login object
		return in;
	}
}
