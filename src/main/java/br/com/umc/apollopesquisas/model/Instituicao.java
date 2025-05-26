package br.com.umc.apollopesquisas.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "instituicoes")
public class Instituicao {

    @Id
    private String instituicaoId;
    private String nomeInstituicao;
    private String cnpj;

    public String getInstituicaoId() {
        return instituicaoId;
    }

    public void setInstituicaoId(String instituicaoId) {
        this.instituicaoId = instituicaoId;
    }

    public String getNomeInstituicao() {
        return nomeInstituicao;
    }

    public void setNomeInstituicao(String nomeInstituicao) {
        this.nomeInstituicao = nomeInstituicao;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

}
