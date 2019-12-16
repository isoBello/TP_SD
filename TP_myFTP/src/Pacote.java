import java.io.Serializable;

public class Pacote implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Boolean status;
	private String message;
	private byte []arquivo;
	
	public Pacote () {
	}
	
	public Pacote (Boolean status, String message) { 
		this.status = status;
		this.message = message;
	}
	
	public Pacote (String message) { 
		this.message = message;
	}
	
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public byte[] getArquivo() {
		return arquivo;
	}
	public void setArquivo(byte[] arquivo) {
		this.arquivo = arquivo;
	}
	
}
