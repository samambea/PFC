package br.com.umc.apollopesquisas.model;

public class InscricaoInfo {
    private String nomeVoluntario;
    private String email;
    private String telefone;
    private String tituloPesquisa;

    public InscricaoInfo(String nomeVoluntario, String email, String telefone, String tituloPesquisa) {
        this.nomeVoluntario = nomeVoluntario;
        this.email = email;
        this.telefone = telefone;
        this.tituloPesquisa = tituloPesquisa;
    }

    public String getNomeVoluntario() {
        return nomeVoluntario;
    }

    public void setNomeVoluntario(String nomeVoluntario) {
        this.nomeVoluntario = nomeVoluntario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTituloPesquisa() {
        return tituloPesquisa;
    }

    public void setTituloPesquisa(String tituloPesquisa) {
        this.tituloPesquisa = tituloPesquisa;
    }
}
