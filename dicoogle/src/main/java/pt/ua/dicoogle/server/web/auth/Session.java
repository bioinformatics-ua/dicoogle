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

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Handles and manages login requests and the information associated with it.
 *
 * @author António Novo <antonio.novo@ua.pt>
 */
public class Session
{
	/**
	 * Attemps to login using the supplied params.
	 *
	 * @param username the user name.
	 * @param password the users password.
	 * @return true if the login is successfull, false otherwise.
	 */
	public static boolean isSuccessfulLogin(String username, String password)
	{
		LoggedIn result = getSuccessfulLogin(username, password);

		return (result != null);
	}

	/**
	 * Attemps to login using the supplied params.
	 *
	 * @param username the user name.
	 * @param password the users password.
	 * @return a LoggedIn object if the login is successfull, null otherwise.
	 */
	public static LoggedIn getSuccessfulLogin(String username, String password)
	{
		Authentication auth = Authentication.getInstance();
		LoggedIn result = auth.login(username, password);

		return result;
	}

	/**
	 * Check if there is a user logged in on the session supplied.
	 *
	 * @param session the HttpSession object of the request.
	 * @return true if there is a user logged in on the session supplied, false otherwise.
	 */
	public static boolean isUserLoggedIn(HttpSession session)
	{
		return (getUserLoggedIn(session) != null);
	}

	/**
	 * Check if there is a user logged in on the session supplied and if (s)he ia an administrator.
	 *
	 * @param session the HttpSession object of the request.
	 * @return true if there is a user logged in on the session supplied and is an administrator, false otherwise.
	 */
	public static boolean isUserLoggedInAnAdmin(HttpSession session)
	{
		LoggedIn login = getUserLoggedIn(session);
		return ((login != null) && (login.isAdmin()));
	}

	/**
	 * Gets the LoggedIn object that has the information relative to user logged in on the session supplied.
	 *
	 * @param session the HttpSession object of the request.
	 * @return a LoggedIn object if there is a user logged in on the session supplied, null otherwise.
	 */
	public static LoggedIn getUserLoggedIn(HttpSession session)
	{
		// if the sessio is invalid
		if (session == null)
			return null;

		// if the client is not yet aware of (or didn't aceepted/joined) the session
		if (session.isNew())
			return null;

		// if no login information was found
		Object login = session.getAttribute("login");
		if (login == null)
			return null;

		// the client is currently logged in
		return (LoggedIn) login;
	}

	/**
	 * If there is a user logged in on the current HttpSession, log out.
	 *
	 * @param session the HttpSession object of the request.
	 * @return if there was a logout action performed or not.
	 */
	public static boolean logout(HttpServletRequest request)
	{
		HttpSession session = request.getSession(false);
		try
		{
			session.invalidate();
		}
		catch (Exception e )
		{
			System.err.println("Tracking session");
		}



		// if the sessio is invalid
		if (session == null)
			return false;

		// if the client is not yet aware of (or didn't aceepted/joined) the session
		if (session.isNew())
			return false;

		// if no login information was found
		Object login = session.getAttribute("login");
		if (login == null)
			return false;

		// the client is currently logged in, so logout
		session.removeAttribute("login");
		session.invalidate();
		return true;
	}

	/**
	 * Checks if the request made to a servlet was made by a logged is user,
	 * or if the request carries the required login information.
	 * <br>
	 * This is done by first checking if the request has a valid
	 * (with valid login information) session (like an AJAX request from a
	 * cookies enabled browser), if not checks if the request parameters
	 * have valid login information (like made a REST[less] stand-alone
	 * application), and if none of the above situations happened send an
	 * adequate response to the client.
	 *
	 * @param requiresAdminRights if the current request requires admin rights to be served/performed.
	 * @return a LoggedIn object if the request is to be served, null otherwise.
	 */
	public static LoggedIn servletLogin(HttpServletRequest request, HttpServletResponse response, boolean requiresAdminRights) throws IOException
	{
		// check if there a session and a login information attached to it
		HttpSession session = request.getSession(false);
		LoggedIn login = getUserLoggedIn(session);
		if (login != null)
		{
			// check if this request needs admin rights and the user has them
			if (requiresAdminRights)
			{
				if (login.isAdmin())
					return login;
				else
				{
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Administrator rights are needed to process this request!");
					return null;
				}
			}
			else
				return login;
		}

		// since the above failed, check if there is valid login information on the request parameters
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		login = getSuccessfulLogin(username, password);
		if (login != null)
		{
			// check if this request needs admin rights and the user has them
			if (requiresAdminRights)
			{
				if (login.isAdmin())
					return login;
				else
				{
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Administrator rights are needed to process this request!");
					return null;
				}
			}
			else
				return login;
		}

		// both situations failed
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No login information found!");
		return null;
	}

	/**
	 * Checks if the request made to a webapp was made by a logged is user,
	 * or if the request carries the required login information.
	 * <br>
	 * This is done by first checking if the request has a valid
	 * (with valid login information) session (like an AJAX request from a
	 * cookies enabled browser), if not checks if the request parameters
	 * have valid login information (like made a REST[less] stand-alone
	 * application), and if none of the above situations happened send an
	 * adequate response to the client, on most cases a redirection to the
	 * login page will be sent.
	 *
	 * @param requiresAdminRights if the current request requires admin rights to be served/performed.
	 * @return a LoggedIn object if the request is to be served, null otherwise.
	 */
	public static LoggedInStatus webappLogin(HttpServletRequest request, HttpServletResponse response, boolean requiresAdminRights) throws IOException
	{
		// check if there a session and a login information attached to it
		HttpSession session = request.getSession(true);
		LoggedIn login = getUserLoggedIn(session);
		/*if (login != null)
		{
			// check if this request needs admin rights and the user has them
			if (requiresAdminRights && (! login.isAdmin()))
			{
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return new LoggedInStatus(null, LoggedInStatus.S_UNAUTHORIZEDACCESS);
			}

			return new LoggedInStatus(login, LoggedInStatus.S_ALREADYLOGGEDIN);
		}*/

		// since the above failed, check if there is valid login information on the request parameters
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		login = getSuccessfulLogin(username, password);
		if (login != null)
		{
			// check if this request needs admin rights and the user has them
			if (requiresAdminRights && (! login.isAdmin()))
			{
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return new LoggedInStatus(null, LoggedInStatus.S_UNAUTHORIZEDACCESS);
			}

			// add the login information to the session
			session = request.getSession(true); // force the creation of a new session if there is none
			session.setAttribute("login", login);
			return new LoggedInStatus(login, LoggedInStatus.S_VALIDLOGIN);
		}

		// both situations failed
		if (! request.getRequestURI().equalsIgnoreCase("/login.jsp"))
			response.sendRedirect("/login.jsp");
		if ((username == null) && (password == null))
			return new LoggedInStatus(null, LoggedInStatus.S_NOINFORMATION);
		else
			return new LoggedInStatus(null, LoggedInStatus.S_INVALIDCREDENTIALS);
	}

	/**
	 * Based on the request referer returns the address of the previously visited
	 * page or to the default main page if none.
	 *
	 * @param request the original http request object.
	 * @return a string containing the previous URL.
	 */
	public static String getLastVisitedURL(HttpServletRequest request)
	{
		String result = "/index.jsp";

		if (request == null)
			return result;

		result = request.getHeader("Referer");
		if (result == null)
			result = "/index.jsp";

		return result;
	}
}
