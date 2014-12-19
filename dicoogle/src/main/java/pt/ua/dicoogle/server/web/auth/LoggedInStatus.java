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

/**
 * A wrapper for the LogggedIn objects that are used on webapps.
 * Holds the LoggedIn object and a status of why the LoggedIn object has the current value.
 *
 * @author António Novo <antonio.novo@ua.pt>
 */
public class LoggedInStatus
{
	public static int S_NOINFORMATION = 0;
	public static int S_INVALIDCREDENTIALS = 1;
	public static int S_UNAUTHORIZEDACCESS = 2;
	public static int S_ALREADYLOGGEDIN = 3;
	public static int S_VALIDLOGIN = 4;

	private LoggedIn login;
	private int status;

	public LoggedInStatus()
	{
		this.login = null;
		this.status = S_NOINFORMATION;
	}

	public LoggedInStatus(LoggedIn login, int status)
	{
		this.login = login;
		this.status = status;
	}

	/**
	 * @return the login
	 */
	public LoggedIn getLogin()
	{
		return login;
	}

	/**
	 * @return the status
	 */
	public int getStatus()
	{
		return status;
	}

	public boolean wasAlreadyLoggedIn()
	{
		return (status == S_ALREADYLOGGEDIN);
	}

	public boolean noInformationSupplied()
	{
		return (status == S_NOINFORMATION);
	}

	public boolean invalidCredentialsSupplied()
	{
		return (status == S_INVALIDCREDENTIALS);
	}

	public boolean loggedInSuccessfully()
	{
		return (status == S_VALIDLOGIN);
	}

	public boolean unAuthorizedAccess()
	{
		return (status == S_UNAUTHORIZEDACCESS);
	}

	public boolean isCurrentlyLoggedIn()
	{
		return (((status == S_ALREADYLOGGEDIN) || (status == S_VALIDLOGIN)) && (login != null));
	}
}
