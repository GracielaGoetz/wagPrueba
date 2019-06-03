package querys;

import java.util.List;
import java.util.stream.Collectors;

import datos.Usuario;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.RoutingContext;

public class ConsultaUsuario {
			
	public void insertarUsario(RoutingContext rc, JDBCClient cliente) {
		Usuario usuario = Json.decodeValue(rc.getBodyAsString(), Usuario.class);
		cliente.getConnection(conn -> {
			SQLConnection conexion = conn.result();
			insert(usuario, conexion, r -> {
				rc.response().setStatusCode(201)
				.putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encodePrettily(r.result()));
			});
		});
	}
	public void insert(Usuario user, SQLConnection conexion, Handler<AsyncResult<Usuario>> resultado) {
		String sql = "INSERT INTO public.usuario (id, nombre, apellido, contrasena, rol, correo) VALUES (?, ?, ?, ?, ?, ?);";
		conexion.updateWithParams(sql, new JsonArray()
				.add(user.getId()).add(user.getNombre()).add(user.getApellido()).add(user.getContrasena())
				.add(user.getRol()).add(user.getCorreo())
				, rb ->{
					if(rb.failed()) {
						resultado.handle(Future.failedFuture("GG IZI"));
					}else {
						Usuario u = new Usuario(user.getId(), user.getNombre(), user.getApellido(), user.getContrasena()
								, user.getRol(), user.getCorreo());
						resultado.handle(Future.succeededFuture(u));
					}
				});
	}
	public void actualizarUsuario(RoutingContext rc, JDBCClient cliente) {
		String id = rc.request().getParam("id");
		JsonObject json = rc.getBodyAsJson();
		if(id == null || json == null) {
			rc.response().setStatusCode(400).end();
		}else {
			cliente.getConnection(conn->{
				SQLConnection conexion = conn.result();
				update(id, json, conexion, r ->{
					
				});
			});
		}
	}
	public void update(String id, JsonObject json, SQLConnection conexion, Handler<AsyncResult<Usuario>> resultado) {
		String sql = "UPDATE public.usuario SET  nombre=?, apellido=?, contrasena=?, rol=?, correo=? WHERE id=?;";
		conexion.updateWithParams(sql, new JsonArray().add(json.getString("nombre")).add(json.getString("apellido"))
				.add(json.getString("contrasena"))
				.add(Integer.valueOf(json.getString("rol")))
				.add(json.getString("correo"))
				.add(Integer.valueOf(id))
				, rb->{
					if(rb.failed()){
						resultado.handle(Future.failedFuture("No se pudo insertar"));
					}
					if(rb.result().getUpdated() == 0) {
						resultado.handle(Future.failedFuture("No se encontro registro"));
					}
					Usuario u = new Usuario(Integer.valueOf(id), json.getString("nombre"), json.getString("apellido"), json.getString("contrasena"), 
							Integer.valueOf(json.getString("rol")), json.getString("correo"));
					resultado.handle(Future.succeededFuture(u));
				});
	}
	public void eliminarUsuario(RoutingContext rc, JDBCClient cliente) {
		String id = rc.request().getParam("id");
		if(id == null) {
			rc.response().setStatusCode(400).end();
		}else {
			cliente.getConnection(conn->{
				SQLConnection conexion = conn.result();
				conexion.execute("DELETE FROM public.usuario WHERE id = "+Integer.valueOf(id)+";", resultado ->{
					rc.response().setStatusCode(204).end();
				});
			});
		}
	}
	public void getAll(RoutingContext rc, JDBCClient cliente) {
		cliente.getConnection(conn->{
			SQLConnection conexion = conn.result();
				conexion.query("Select * From usuario", r ->{
					List<Usuario> user = r.result().getRows().stream().map(Usuario::new).collect(Collectors.toList());
					rc.response().putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(user));
					conexion.close();
				});
		});
	}
	public void getOne(RoutingContext rc, JDBCClient cliente) {
		String nombre = rc.request().getParam("nombre");
		String contrasena = rc.request().getParam("contrasena");
		if(nombre == null || contrasena == null) {
			rc.response().setStatusCode(400).end();
		}else {
			cliente.getConnection(conn->{
				SQLConnection conexion = conn.result();
				select(nombre, contrasena, conexion, r ->{
					if(r.succeeded()) {
						rc.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(r.result()));
					}else {
						rc.response().setStatusCode(404).putHeader("content-type", "text/html").end("<h1>No se encontro Usuario</h1>");
					}
				});
			});
		}
	}
	public void select(String nombre, String contrasena, SQLConnection conexion, Handler<AsyncResult<Usuario>> resultado) {
		conexion.query("Select * From usuario Where nombre = '"+nombre+"' and contrasena = '"+contrasena+"'"
				, rb->{
					if(rb.failed()) {
						resultado.handle(Future.failedFuture("Fallo la Busqueda"));
					}else {
						if(rb.result().getNumRows() >= 1) {
							resultado.handle(Future.succeededFuture(new Usuario(rb.result().getRows().get(0))));
						}else {
							resultado.handle(Future.failedFuture("Usuario No registrado"));
						}
					}
				});
	}
}
