package br.com.umc.apollopesquisas.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/imagens")
public class ImagemController {

    private final Path raiz = Paths.get("uploads/imagens");

    @GetMapping("/{nomeArquivo:.+}")
    public ResponseEntity<Resource> servirImagem(@PathVariable String nomeArquivo) throws IOException {
        try {
            Path caminho = raiz.resolve(nomeArquivo).normalize();
            Resource recurso = new UrlResource(caminho.toUri());

            if (recurso.exists() || recurso.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"")
                        .body(recurso);
            } else {
                throw new IOException("Imagem não encontrada");
            }
        } catch (MalformedURLException e) {
            throw new IOException("Erro ao carregar imagem", e);
        }
    }
}
