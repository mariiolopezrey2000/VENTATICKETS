import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GestionFormularios {
	private static Statement s;
	private static HttpServletRequest request;
	private static HttpServletResponse response;

	public static HttpServletRequest getRequest() {
		return request;
	}

	public static void setRequest(HttpServletRequest request) {
		GestionFormularios.request = request;
	}

	public static HttpServletResponse getResponse() {
		return response;
	}

	public static void setResponse(HttpServletResponse response) {
		GestionFormularios.response = response;
	}

	public static Statement getS() {
		return s;
	}

	public static void setS(Statement s) {
		GestionFormularios.s = s;
	}

	public void gestion() throws ServletException, IOException {
		/*
		 * String sql="select * from usuario";
		 * try {
		 * ResultSet rs=s.executeQuery(sql);
		 * while (rs.next()) {
		 * System.out.println( rs.getString("Mario"));
		 * }
		 * } catch (SQLException e) {
		 * // TODO Auto-generated catch block
		 * e.printStackTrace();
		 * }
		 */
		switch (request.getParameter("tipo")) {
			case "a":
				registrousuario();
				break;
			case "b":
				compra();
				break;
			case "c":
				//cfonsultacompra();
				break;
			case "d":
				cancelacion();
				break;
		}

	}

	public void registrousuario() throws ServletException, IOException {
		RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
		if (consultarusuario()) {
			System.out.println("usuario encontrado");
			request.setAttribute("mensajeu", "Usuario ya está dado de alta");
		} else {
			if (comprobartodosdatosusuario()) {
				request.setAttribute("mensajeu", "Usuario  registrado");
			} else {
				System.out.println("faltan datos");
				request.setAttribute("mensajeu", "faltan datos");
			}
		}
		rd.forward(request, response);
	}

	public void compra() throws ServletException, IOException {
		RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
		//consulta del precio de un evento

		if (consultarusuario()) {
			if (comprobartodosdatoscompra()) {
				System.out.println("todos datos");
				String sqlcomprobarprecio="select PRECIO_VENTA from eventos where ID_EVENTO=(SELECT ID_EVENTO FROM eventos WHERE NOMBRE='"+request.getParameter("tipoespectaculo")+"')";
				//String sqlconsultaentradasdisponibles="select N_E_DISPONIBLES from eventos where ID_EVENTO=(SELECT ID_EVENTO FROM eventos WHERE NOMBRE='"+request.getParameter("tipoespectaculo")+"')";
				Double precio_Evento=0.0;
				try {
					ResultSet rs=s.executeQuery(sqlcomprobarprecio);
					precio_Evento=rs.getDouble(0);
					//ResultSet rs2=s.executeQuery(sqlconsultaentradasdisponibles);
					//int entradasdisponibles=rs2.getInt(0);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//String sqlcomprobarentradasdisponibles="select N_E_DISPONIBLES from eventos where ID_EVENTO=(SELECT ID_EVENTO FROM eventos WHERE NOMBRE='"+request.getParameter("tipoespectaculo")+"')";
				Double preciototal=precio_Evento*Integer.parseInt(request.getParameter("numentradas"));
				String sqlINSERTARcompra="INSERT INTO r_compras (ID_COMPRA,PRECIO_TOTAL,DNI_USUARIO,NOMBRE_EVENTO,ID_EVENTO) VALUES ("+consultaid()+","+preciototal+","+request.getParameter("DNI")+","+request.getParameter("tipoespectaculo")+",1";
				
				//INSERT INTO r_compras (ID_COMPRA,PRECIO_TOTAL,DNI_USUARIO,NOMBRE_EVENTO,ID_EVENTO) VALUES ("+consultaid()+","+preciototal+","+request.getParameter("DNI")+","+request.getParameter("tipoespectaculo")+",(SELECT ID_EVENTO FROM eventos WHERE NOMBRE='"+request.getParameter("tipoespectaculo")+"),(SELECT ID_EVENTO FROM eventos WHERE NOMBRE='"+request.getParameter("tipoespectaculo")+"'))";
				//String sqlactualizaentradasdisponibles="UPDATE eventos SET N_E_DISPONIBLES=(select N_E_DISPONIBLES from eventos where ID_EVENTO=(SELECT ID_EVENTO FROM eventos WHERE NOMBRE='"+request.getParameter("tipoespectaculo")+"'))-" +Integer.parseInt(request.getParameter("numentradas"))+" WHERE ID_EVENTO=(SELECT ID_EVENTO FROM eventos WHERE NOMBRE='"+request.getParameter("tipoespectaculo")+"')";
				try {
					s.executeUpdate(sqlINSERTARcompra);
					//s.executeUpdate(sqlactualizaentradasdisponibles);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				request.setAttribute("mensajeC", "compra realizada");
			} else {
				request.setAttribute("mensajeC", "faltan datos");
				System.out.println("faltan datos");
			}
		} else {
			System.out.println("usuario no encontrado");
			request.setAttribute("mensajeC", "Primero Tiene que registrarse");
		}
		rd.forward(request, response);
	}

	public Integer consultaid() {
		String sql = "select ID_COMPRA from r_compras";
		ArrayList<Integer> listabbdd = new ArrayList<Integer>();
		try {
			ResultSet rs = s.executeQuery(sql);
			while (rs.next()) {
				listabbdd.add(rs.getInt("ID_COMPRA"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int num = 1;
		if (listabbdd.size() != 0) {
			Collections.sort(listabbdd);
			for (int i = 0; i < listabbdd.size(); i++) {
				if (listabbdd.get(i) == i + 1) {
					num = i + 1;
				} else {
					return i + 1;
				}
			}
			return num + 1;
		} else {
			return 1;
		}
	}

	public void cancelacion() {

	}

	public boolean comprobartodosdatosusuario() {
		boolean todos = true;
		String[] listadatos = new String[5];
		listadatos[0] = request.getParameter("nombre");
		listadatos[1] = request.getParameter("apellido");
		listadatos[2] = request.getParameter("DNI");
		listadatos[3] = request.getParameter("correo");
		listadatos[4] = request.getParameter("telf");
		for (int i = 0; i < listadatos.length; i++) {
			if (listadatos[i].equals("")) {
				todos = false;
			}
		}
		return todos;
	}

	public boolean comprobartodosdatoscompra() {
		boolean todos = true;
		String[] listadatos = new String[5];
		listadatos[0] = request.getParameter("DNI");
		listadatos[1] = request.getParameter("tipoespectaculo");
		listadatos[2] = String.valueOf(request.getParameter("numentradas"));
		listadatos[3] = request.getParameter("fecha");
		listadatos[4] = request.getParameter("hora");
		for (int i = 0; i < listadatos.length; i++) {
			if (listadatos[i].equals("") || listadatos[i] == null) {
				todos = false;
			}
		}
		return todos;
	}

	public boolean consultarusuario() {
		ResultSet rs;
		boolean encontrado = false;
		String comprobarusuario = "select * from usuario where DNI=" + request.getParameter("DNI");
		try {
			rs = s.executeQuery(comprobarusuario);
			while (rs.next()) {
				if (rs.getInt("DNI") == Integer.parseInt(request.getParameter("DNI"))) {
					encontrado = true;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encontrado;
	}

}