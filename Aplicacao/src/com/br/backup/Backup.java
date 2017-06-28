package com.br.backup;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.hadoop.conf.Configuration;

@Path("/backup")
public class Backup{
	private ArrayList<Cliente> listaUsuario;
	
	public Backup(){
		this.listaUsuario = new ArrayList<>();
	}
			
	@GET
	@Path("/conectar")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes("application/json")
	public String conectar(@QueryParam("nome") String nome, @QueryParam("senha") String senha){
		if(this.buscaCliente(nome, senha) != null){
			return "true";
		}
		return "false";
	}
	
	@GET
	@Path("/registrarUsuario")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes("application/json")
	public String registrarUsuario(@QueryParam("nome") String nome, @QueryParam("senha") String senha){
		if(nome != "" && senha != ""){
			this.carregaLista();
//			this.listaUsuario.add(new Cliente(nome, senha, System.getProperty("java.io.tmpdir")+ "/backups/"+nome));
			this.listaUsuario.add(new Cliente(nome, senha,  "C:/Jars/"+nome));
			this.salvarLista();
			return "true";
		}
		return "false";
	}
	
	public void removeCliente(Cliente cliente){
		this.carregaLista();
		this.listaUsuario.remove(cliente);
		this.salvarLista();
	}

	public Cliente buscaCliente(String nome, String senha){

		if(this.carregaLista()){
			for (int i = 0; i < this.listaUsuario.size(); i++) {
				this.listaUsuario.get(i).conectar(nome, senha);

				if(this.listaUsuario.get(i).isConnectado()){
					return this.listaUsuario.get(i);
				}
			}
		}
		return null;
	}

	public boolean salvarLista(){
		FileOutputStream fout;
		try {

			fout = new FileOutputStream(new File(System.getProperty("java.io.tmpdir")+"/usuarios.data").getAbsolutePath());
			ObjectOutputStream object = new ObjectOutputStream(fout);
			object.writeObject(this.listaUsuario);
			fout.close();
			object.close();

			return true;

		} catch (IOException e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean carregaLista(){
		try {
			if(new File(new File(System.getProperty("java.io.tmpdir")+"/usuarios.data").getAbsolutePath()).exists()){
				FileInputStream fin = new FileInputStream(new File(System.getProperty("java.io.tmpdir")+"/usuarios.data").getAbsolutePath());
				ObjectInputStream ois = new ObjectInputStream(fin);
				this.listaUsuario =  (ArrayList<Cliente>) ois.readObject();
				fin.close();
				ois.close();
				return true;
			}else{
				return false;
			}
		} catch (IOException | ClassNotFoundException e) {
			return false;
		}
	}
	
	public boolean isConnectado(String nome, String senha){
		return buscaCliente(nome, senha).isConnectado();
	}
	
	@GET
	@Path("/executar")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes("application/json")
	public String acao(@QueryParam("acao") String acao, @QueryParam("caminhoArq") String caminhoArq, @QueryParam("nome") String nome, @QueryParam("senha") String senha) throws IOException{

		Cliente clienteAtual = this.buscaCliente(nome, senha);

		if(clienteAtual != null && this.isConnectado(nome, senha)){
			HDFS acoes = new HDFS();
			
			Configuration conf = new Configuration();
			conf.set("fs.defaultFS","hdfs://one.hdp:8080");

			try {
				if (acao.equals("add")) {
					acoes.adicionarArquivos(caminhoArq, clienteAtual.getCaminhoDir(), conf);

				} else if (acao.equals("read")) {
					acoes.lerArquivos(caminhoArq, conf);

				} else if (acao.equals("delete")) {
					acoes.deletar(caminhoArq, conf);

				} else if (acao.equals("mkdir")) {
					acoes.mkdir(caminhoArq, conf);

				}
				return "true";
			} catch (IOException e) {
				e.printStackTrace();
				return "false";
			}
		}
		return "false";
	}
}
