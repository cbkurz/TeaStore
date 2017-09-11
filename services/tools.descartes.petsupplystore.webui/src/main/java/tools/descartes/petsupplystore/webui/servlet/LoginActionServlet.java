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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netflix.client.ClientException;

import tools.descartes.petsupplystore.entities.message.SessionBlob;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedStoreOperations;

/**
 * Servlet for handling the login actions
 * 
 * @author Andre Bauer
 */
@WebServlet("/loginAction")
public class LoginActionServlet extends AbstractUIServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginActionServlet() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doGetInternal(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, ClientException {

		redirect("/", response);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doPostInternal(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, ClientException {
		boolean login = false;
		if (request.getParameter("username") != null && request.getParameter("password") != null) {
			SessionBlob blob = LoadBalancedStoreOperations.login(getSessionBlob(request),
					request.getParameter("username"), request.getParameter("password"));
			saveSessionBlob(blob, response);
			login = (blob.getSID() != null);

			if (login) {
				if (request.getParameter("referer") != null
						&& request.getParameter("referer").contains("tools.descartes.petsupplystore.webui/cart")) {
					redirect("/cart", response, MESSAGECOOKIE, SUCESSLOGIN);
				} else {
					redirect("/", response, MESSAGECOOKIE, SUCESSLOGIN);
				}

			} else {
				redirect("/login", response);
			}

		} else if (request.getParameter("logout") != null) {
			SessionBlob blob = LoadBalancedStoreOperations.logout(getSessionBlob(request));
			saveSessionBlob(blob, response);
			destroySessionBlob(blob, response);
			redirect("/", response, MESSAGECOOKIE, SUCESSLOGOUT);

		} else {
			doGetInternal(request, response);
		}

	}

}
