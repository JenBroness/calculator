package com.benjonas.calculator.service;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.benjonas.calculator.parse.CalcGrammar;
import com.benjonas.calculator.parse.CalcGrammar.Expr;

public class QueryWithContext {

	public static void query(PrintWriter out, Expr expr) throws NamingException {
		Context context = null;
		DataSource datasource = null;
		Connection connect = null;
		Statement statement = null;

		try {
			// Get the context and create a connection
			context = new InitialContext();
			datasource = (DataSource) context.lookup("java:/comp/env/jdbc/calculator");
			connect = datasource.getConnection();

			// Create the statement + result set that we will use for everything
			statement = connect.createStatement();
			ResultSet resultSet = null;

			//Get string representation & hash representation of problem
			String exprName = expr.toString();
			int exprId = exprName.hashCode();
			Float exprAns = null; //will contain answer, eventually
			// TODO: add cost

			//Search to see if answer is already in db; if so, set exprAns to that answer
			String selectProblem = "SELECT result FROM problems "
					+ "WHERE P_Id = " + exprId;
			resultSet = statement.executeQuery(selectProblem);
			if (resultSet.next()) { //should only be one value in the set
				exprAns = resultSet.getFloat("result");
				out.println("Found answer in database: "+exprAns+"<br>");
			} else {
				//If answer not in db, get it from generated AST and add result to db
				exprAns = CalcGrammar.evalExpr(expr);
				out.println("Evaluated to find answer: "+exprAns+"<br>");
				String insertProblem = "INSERT INTO problems VALUES (" +
						exprId + ",'" + exprName + "'," + exprAns + ",1);";
				out.println("Updating database:"+insertProblem+"<br>");
				statement.executeUpdate(insertProblem);
			}
			
			String selectAll = "SELECT * FROM problems";

			// Execute the query and print off the result set
			resultSet = statement.executeQuery(selectAll);
			out.println("<strong>Printing result using DataSource...</strong><br>");
			while (resultSet.next()) {
				int p_id = resultSet.getInt("P_Id");
				String problem = resultSet.getString("problem");
				float result = resultSet.getFloat("result");
				int score = resultSet.getInt("score");
				out.println("P_Id: "+p_id + " Problem: " + problem + " Result: " + result +
						" Score: " + score + "<br>");
			}
		} catch (SQLException e) { e.printStackTrace(out);
		} finally {
			// Close the connection and release the resources used.
			try { statement.close(); } catch (SQLException e) { e.printStackTrace(out); }
			try { connect.close(); } catch (SQLException e) { e.printStackTrace(out); }
		}

	}
}
