package br.com.umc.apollopesquisas.model;

import br.com.umc.apollopesquisas.enums.StatusPesquisa;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "pesquisas")
public class Pesquisa {

    @Id
    private String pesquisaId;
    private String usuarioId;
    private String nomePesquisa;
    private String descricao;
    private String objetivo;
    private String nomeInstituicao;
    private String instituicaoId;
    private String areaDaPesquisa;
    private String pesquisadorResponsavel;
    private String criteriosInclusaoExclusao;
    private StatusPesquisa statusPesquisa;


    private List<String> participantes = new ArrayList<>();
    public List<String> getParticipantes() {
        return participantes;
    }

    @DBRef
    private List<Voluntario> voluntariosInscritos = new ArrayList<>();


    public void setParticipantes(List<String> participantes) {
        this.participantes = participantes;
    }


    public String getPesquisaId() {
        return pesquisaId;
    }

    public void setPesquisaId(String pesquisaId) {
        this.pesquisaId = pesquisaId;
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

    public String getNomeInstituicao() {
        return nomeInstituicao;
    }

    public void setNomeInstituicao(String nomeInstituicao) {
        this.nomeInstituicao = nomeInstituicao;
    }

    public String getInstituicaoId() {
        return instituicaoId;
    }

    public void setInstituicaoId(String instituicaoId) {
        this.instituicaoId = instituicaoId;
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



