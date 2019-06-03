package server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import querys.ConsultaUsuario;

public class Server extends AbstractVerticle{
	ConsultaUsuario consultaUser = new ConsultaUsuario();
	JDBCClient cliente;
	@Override
	public void start() {
		cliente = JDBCClient.createShared(vertx, new JsonObject()
				.put("url", "jdbc:postgresql://localhost:3007/wag")
				.put("driver_class", "org.postgresql.Driver")
				.put("max_pool_size", 30)
				.put("user", "postgres")
				.put("password", "1234")//Impe98rio
				);
		Router router = Router.router(vertx);
		router.route("/assets/*").handler(StaticHandler.create("assets"));
		router.get("/api/usuario").handler(rc->{
			consultaUser.getAll(rc, cliente);
		});
		//Usuario
		router.route("/api/usuario*").handler(BodyHandler.create());
		router.post("/api/usuario").handler(rc->{
			consultaUser.insertarUsario(rc, cliente);
		});
		router.put("/api/usuario/:id").handler(rc->{
			consultaUser.actualizarUsuario(rc, cliente);
		});
		router.delete("/api/usuario/:id").handler(rc->{
			consultaUser.eliminarUsuario(rc, cliente);
		});
		router.get("/api/usuario/:nombre/:contrasena").handler(rc->{
			consultaUser.getOne(rc, cliente);
		});
		//Usuario Fin
		vertx.createHttpServer().requestHandler(router).listen(8080);
	}
	@Override
	public void stop() {
		cliente.close();
	}
}
