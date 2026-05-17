package _Bibliotecapp.modelo;

import org.bson.types.ObjectId;

public class Libro {
    private ObjectId id;
    private String referencia;
    private String titulo;
    private String autor;
    private String isbn;
    private boolean prestado; 
    private String fechaPrestamo;
    private String usuarioPrestamo;
    private int vecesPrestado;
    private String telefonoPrestamo;

    public Libro() {}

    public Libro(String referencia, String titulo, String autor, String isbn) {
    	this.referencia = referencia;
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.prestado = false;
        this.fechaPrestamo = "-";
        this.usuarioPrestamo = "-"; 
        this.vecesPrestado = 0;    
        this.telefonoPrestamo = "-";
        
    }

    public String getTelefonoPrestamo() {
		return telefonoPrestamo;
	}

	public void setTelefonoPrestamo(String telefonoPrestamo) {
		this.telefonoPrestamo = telefonoPrestamo;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public String getUsuarioPrestamo() {
		return usuarioPrestamo;
	}

	public void setUsuarioPrestamo(String usuarioPrestamo) {
		this.usuarioPrestamo = usuarioPrestamo;
	}

	public int getVecesPrestado() {
		return vecesPrestado;
	}

	public void setVecesPrestado(int vecesPrestado) {
		this.vecesPrestado = vecesPrestado;
	}

	public String getPrestadoTexto() {
        return prestado ? "Sí" : "No";
    }

    public ObjectId getId() { 
    	return id; 
    }
    
    public void setId(ObjectId id) {
    	this.id = id; 
    }
    
    public String getTitulo() {
    	return titulo; 
    }
    
    public void setTitulo(String titulo) {
    	this.titulo = titulo; 
    }
    
    public String getAutor() {
    	return autor; 
    	
    }
    
    public void setAutor(String autor) {
    	this.autor = autor; 
    }
    
    public String getIsbn() {
    	return isbn; 
    }
    
    public void setIsbn(String isbn) {
    	this.isbn = isbn; 
    }
    
    public boolean isPrestado() {
    	return prestado; 
    }
    
    public void setPrestado(boolean prestado) {
    	this.prestado = prestado; 
    }
    
    public String getFechaPrestamo() {
    	return fechaPrestamo; 
    }
    
    public void setFechaPrestamo(String fechaPrestamo) {
    	this.fechaPrestamo = fechaPrestamo;
    }
}