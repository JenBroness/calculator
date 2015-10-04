package com.benjonas.calculator.service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.NamingException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.benjonas.calculator.parse.CalcGrammar;
import com.benjonas.calculator.parse.CalcGrammar.Expr;
import com.benjonas.calculator.parse.ParseException;

@WebServlet("/calculator/*")
public class CalcServlet extends HttpServlet {

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/html");

		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (request.getParameter("jdbc_query") != null) {
			String problem = request.getParameter("problem_input");
			Expr expr = null;
			if (problem != null) {
				try {
					expr = CalcGrammar.parse(problem);
					out.println("Problem given: "+expr.toString()+"<br>");
				} catch (ParseException e) {
					out.println("Parse exception<br>");
				}
			}
			try {
				QueryWithContext.query(out,expr);
			} catch (NamingException e) {
				out.println("Naming Exception<br>");
			}
		}
		out.println("<a href=\"index.jsp\">Back</a><br>");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4621216927964635476L;

}
