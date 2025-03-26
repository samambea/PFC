package br.com.umc.apollopesquisas.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pesquisadores")
public class Pesquisador extends Usuario {

    @Id
    private int pesquisadorId;
    private String crm;
    private String especialidade;
    private String areaDePesquisa;

    public int getPesquisadorId() {
        return pesquisadorId;
    }

    public void setPesquisadorId(int pesquisadorId) {
        this.pesquisadorId = pesquisadorId;
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getAreaDePesquisa() {
        return areaDePesquisa;
    }

    public void setAreaDePesquisa(String areaDePesquisa) {
        this.areaDePesquisa = areaDePesquisa;
    }
}
