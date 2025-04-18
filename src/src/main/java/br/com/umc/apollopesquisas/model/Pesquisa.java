package br.com.umc.apollopesquisas.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pesquisas")
public class Pesquisa {

    @Id
    private String id;
    private String usuarioId;
    private String nomePesquisa;
    private String descricao;
    private String objetivo;
    private String instituicao;
    private String areaDaPesquisa;
    private String pesquisadorResponsavel;
    private String criteriosInclusaoExclusao;
    private StatusPesquisa statusPesquisa;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }



    public String getNomePesquisa() {
        return nomePesquisa;
    }

    public void setNomePesquisa(String nomePesquisa) {
        this.nomePesquisa = nomePesquisa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public String getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    public String getAreaDaPesquisa() {
        return areaDaPesquisa;
    }

    public void setAreaDaPesquisa(String areaDaPesquisa) {
        this.areaDaPesquisa = areaDaPesquisa;
    }

    public String getPesquisadorResponsavel() {
        return pesquisadorResponsavel;
    }

    public void setPesquisadorResponsavel(String pesquisadorResponsavel) {
        this.pesquisadorResponsavel = pesquisadorResponsavel;
    }

    public String getCriteriosInclusaoExclusao() {
        return criteriosInclusaoExclusao;
    }

    public void setCriteriosInclusaoExclusao(String criteriosInclusaoExclusao) {
        this.criteriosInclusaoExclusao = criteriosInclusaoExclusao;
    }

    public StatusPesquisa getStatusPesquisa() {
        return statusPesquisa;
    }

    public void setStatusPesquisa(StatusPesquisa statusPesquisa) {
        this.statusPesquisa = statusPesquisa;
    }
}
