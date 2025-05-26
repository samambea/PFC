package br.com.umc.apollopesquisas.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usuarios")
public class Administrador extends Usuario {

    public Administrador() {
        this.setRole("ADMIN");
    }


}
