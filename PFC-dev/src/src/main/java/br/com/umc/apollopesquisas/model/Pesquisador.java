package br.com.umc.apollopesquisas.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pesquisadores")
public class Pesquisador extends Usuario {


    private String crm;
    private String especialidade;
    private String areaDePesquisa;


    public String getCrm() { return crm; }

    public void setCrm(String crm) { this.crm = crm; }

    public String getEspecialidade() { return especialidade; }

    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    public String getAreaDePesquisa() { return areaDePesquisa; }

    public void setAreaDePesquisa(String areaDePesquisa) { this.areaDePesquisa = areaDePesquisa; }
}
