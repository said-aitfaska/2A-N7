package pack;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class HW
 */
@WebServlet("/HW")
public class HW extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HW() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		//response.getWriter().println("<html><body>Hello World !</body></html>");
		//try {
			//String s1 = request.getParameter("nb1");
			//String s2 = request.getParameter("nb2");
			//int somme = Integer.parseInt(s1) + Integer.parseInt(s2);
		    //response.getWriter().println("<html><body>La somme des arguments " + s1 + " et " + s2 +  " est : " + somme + " </body></html>");
			
		//}catch (Exception e) {
			//System.out.println("Erreur dans ler arguments !");
		//}
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doGet(request, response);
		String s1 = request.getParameter("nb1");
		String s2 = request.getParameter("nb2");
		int somme = Integer.parseInt(s1) + Integer.parseInt(s2);
	    response.getWriter().println("<html><body>La somme des arguments " + s1 + " et " + s2 +  " est : " + somme + " </body></html>");
		
	}

}
