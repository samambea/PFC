package br.com.umc.apollopesquisas.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usuarios")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Pesquisador.class, name = "pesquisador"),
        @JsonSubTypes.Type(value = Voluntario.class, name = "voluntario")
})
public abstract class Usuario {

    private int usuarioId;
    private String nome;
    private String email;
    private String senha;
    private String categoria;
    private String token;

    public Usuario() {
    }

    public Usuario(int usuarioId, String nome, String email, String senha, String categoria, String token) {
        this.usuarioId = usuarioId;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.categoria = categoria;
        this.token = token;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean login(String email, String senha) {
        if (this.email.equals(email) && this.senha.equals(senha)) {
            System.out.println("Usuário " + email + " realizou login.");
            return true;
        }
        System.out.println("Falha no login para " + email);
        return false;
    }


    public void logout() {

        System.out.println("Usuário " + email + " realizou logout.");
    }

    public void esqueciSenha(String email) {

        System.out.println("Solicitação de redefinição de senha enviada para " + email);
    }
}
