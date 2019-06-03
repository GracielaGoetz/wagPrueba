package datos;

import io.vertx.core.json.JsonObject;

public class Usuario {
	
	private int id;
	private String nombre;
	private String apellido;
	private String contrasena;
	private int rol;
	private String correo;
	
	public Usuario() {
	}
	public Usuario(JsonObject json) {
		this.id = json.getInteger("id");
		this.nombre = json.getString("nombre");
		this.apellido = json.getString("apellido");
		this.contrasena = json.getString("contrasena");
		this.rol = json.getInteger("rol");
		this.correo = json.getString("correo");
	}
	public Usuario(int id, String nombre, String apellido, String contrasena, int rol, String correo) {
		this.id = id;
		this.nombre = nombre;
		this.apellido = apellido;
		this.contrasena = contrasena;
		this.rol = rol;
		this.correo = correo;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getContrasena() {
		return contrasena;
	}
	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}
	public int getRol() {
		return rol;
	}
	public void setRol(int rol) {
		this.rol = rol;
	}
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	
	
}
