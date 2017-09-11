/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tools.descartes.petsupplystore.webui.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.client.ClientException;

import tools.descartes.petsupplystore.entities.Category;
import tools.descartes.petsupplystore.entities.message.SessionBlob;

/**
 * Abstract servlet for the webUI
 * 
 * @author Andre Bauer
 * @author Simon Eismann
 */
public abstract class AbstractUIServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	protected static final String MESSAGECOOKIE = "petsupplystoreMessageCookie";
	protected static final String SUCESSLOGIN = "You are logged in!";
	protected static final String SUCESSLOGOUT = "You are logged out!";
	protected static final String PRODUCTCOOKIE = "petsupplystorenumberProductsCookie";
	protected static final String BLOB = "sessionBlob";
	protected static final String ORDERCONFIRMED = "Your order is confirmed!";
	protected static final String CARTUPDATED = "Your cart is updated!";
	protected static final String ADDPRODUCT = "Product %s is added to cart!";
	protected static final String REMOVEPRODUCT = "Product %s is removed from cart!";

	/**
	 * Try to read the SessionBlob from the cookie. If no SessioBlob exist, a new
	 * SessionBlob is created. If the SessionBlob is corrupted, an
	 * IlligalStateException is thrown.
	 * 
	 * @param request
	 * @return SessionBlob
	 */
	protected SessionBlob getSessionBlob(HttpServletRequest request) {
		if (request.getCookies() != null) {
			for (Cookie cook : request.getCookies()) {
				if (cook.getName().equals(BLOB)) {
					ObjectMapper o = new ObjectMapper();
					try {
						return o.readValue(URLDecoder.decode(cook.getValue(), "UTF-8"), SessionBlob.class);
					} catch (IOException e) {
						throw new IllegalStateException("Cookie corrupted!");
					}
				}
			}
		}
		return new SessionBlob();
	}

	/**
	 * Saves the SessionBlob as Cookie. Throws an IllegalStateException if the
	 * SessionBlob is corrupted.
	 * 
	 * @param blob
	 * @param response
	 */
	protected void saveSessionBlob(SessionBlob blob, HttpServletResponse response) {
		ObjectMapper o = new ObjectMapper();
		try {
			Cookie cookie = new Cookie(BLOB, URLEncoder.encode(o.writeValueAsString(blob), "UTF-8"));
			response.addCookie(cookie);
		} catch (JsonProcessingException | UnsupportedEncodingException e) {
			throw new IllegalStateException("Could not save blob!");
		}
	}

	/**
	 * Destroys the SessionBlob. Throws an IllegalStateException if the SessionBlob
	 * is corrupted.
	 * 
	 * @param blob
	 * @param response
	 */
	protected void destroySessionBlob(SessionBlob blob, HttpServletResponse response) {
		ObjectMapper o = new ObjectMapper();
		try {
			Cookie cookie = new Cookie(BLOB, URLEncoder.encode(o.writeValueAsString(blob), "UTF-8"));
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		} catch (JsonProcessingException | UnsupportedEncodingException e) {
			throw new IllegalStateException("Could not destroy blob!");
		}
	}

	/**
	 * Redirects to the target and creates an Cookie.
	 * 
	 * @param target
	 * @param response
	 * @param cookiename
	 * @param value
	 * @return if redirect was successful
	 * @throws IOException
	 */
	protected void redirect(String target, HttpServletResponse response, String cookiename, String value)
			throws IOException {
		if (!cookiename.equals("")) {
			Cookie cookie = new Cookie(cookiename, value.replace(" ", "_"));
			response.addCookie(cookie);
		}

		redirect(target, response);
	}

	/**
	 * Redirects to the target.
	 * 
	 * @param target
	 * @param response
	 * @return if redirect was successful
	 * @throws IOException
	 */
	protected void redirect(String target, HttpServletResponse response) throws IOException {
		if (!target.startsWith("/")) {
			target = "/" + target;
		}
		response.sendRedirect(getServletContext().getContextPath() + target);



	}

	/**
	 * Checks if specific cookies exist and save their value as message.
	 * 
	 * @param request
	 * @param response
	 */
	protected void checkforCookie(HttpServletRequest request, HttpServletResponse response) {
		if (request.getCookies() != null) {
			for (Cookie cook : request.getCookies()) {
				if (cook.getName().equals(MESSAGECOOKIE)) {
					request.setAttribute("message", cook.getValue().replaceAll("_", " "));
					cook.setMaxAge(0);
					response.addCookie(cook);
				} else if (cook.getName().equals(PRODUCTCOOKIE)) {
					request.setAttribute("numberProducts", cook.getValue());
				}
			}
		}
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			doGetInternal(request, response);
		} catch (ClientException e) {
			serveTimoutResponse(request, response);
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			doPostInternal(request, response);
		} catch (ClientException e) {
			serveTimoutResponse(request, response);
		}
		
	}
	
	/**
	 * Handles a http POST request internally.
	 * @param request The request.
	 * @param response The response to write to.
	 * @throws ServletException ServletException on error.
	 * @throws IOException IOException on error.
	 * @throws ClientException Exception on timeouts and load balancer errors.
	 */
	protected void doPostInternal(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, ClientException {
		doGetInternal(request, response);
	}
	
	/**
	 * Handles a http GET request internally.
	 * @param request The request.
	 * @param response The response to write to.
	 * @throws ServletException ServletException on error.
	 * @throws IOException IOException on error.
	 * @throws ClientException Exception on timeouts and load balancer errors.
	 */
	protected abstract void doGetInternal(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, ClientException;
	
	private void serveTimoutResponse(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(408);
		request.setAttribute("CategoryList", new ArrayList<Category>());
		request.setAttribute("storeIcon", "");
		request.setAttribute("errorImage", "");
		request.setAttribute("title", "408 : Pet Supply Store Timout");
		request.setAttribute("login", false);
		request.getRequestDispatcher("WEB-INF/pages/error.jsp").forward(request, response);
	}
}