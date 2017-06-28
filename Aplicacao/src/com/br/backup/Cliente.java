package com.br.backup;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlRootElement
public class Cliente implements Serializable {
	private static final long serialVersionUID = 1L;
	private String nome, senha, caminhoDir;
	private boolean conectado = false;

	public Cliente(String nome, String senha, String caminho){
		this.setNome(nome);
		this.setSenha(senha);
		this.setCaminhoDir(caminho);
	}
	
	@XmlElement
	public String getCaminhoDir() {
		return this.caminhoDir;
	}
	
	@XmlElement
	public void setCaminhoDir(String caminhoDir) {
		this.caminhoDir = caminhoDir;
	}
	
	@XmlElement
	public String getNome() {
		return nome;
	}
	
	@XmlElement
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	@XmlElement
	public String getSenha() {
		return senha;
	}
	
	@XmlElement
	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	@XmlElement
	public void conectar(String nome, String senha){
		if(this.getNome().equalsIgnoreCase(nome) && this.getSenha().equalsIgnoreCase(senha)){
			this.conectado = true;
		}else{
			this.conectado = false;
		}
	}
	
	@XmlElement
	public boolean isConnectado(){
		return conectado;
	}
}
