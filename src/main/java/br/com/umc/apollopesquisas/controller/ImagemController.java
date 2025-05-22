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

// Controller REST responsável por servir imagens armazenadas no servidor.
// Permite acesso a imagens via URL, retornando o recurso diretamente.

@RestController // Marca como controller REST que retorna dados binários
@RequestMapping("/imagens") // Prefixo base para todas as rotas deste controller
public class ImagemController {

    // Caminho raiz onde as imagens estão armazenadas
    private final Path raiz = Paths.get("uploads/imagens");

    // Endpoint para servir imagem pelo nome do arquivo
    @GetMapping("/{nomeArquivo:.+}")
    public ResponseEntity<Resource> servirImagem(@PathVariable String nomeArquivo) throws IOException {
        try {
            // Resolve o caminho do arquivo e normaliza para evitar path traversal
            Path caminho = raiz.resolve(nomeArquivo).normalize();
            Resource recurso = new UrlResource(caminho.toUri());

            // Verifica se o recurso existe e é legível
            if (recurso.exists() || recurso.isReadable()) {
                // Retorna o recurso com cabeçalho para exibição inline
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"")
                        .body(recurso);
            } else {
                // Lança exceção se imagem não encontrada
                throw new IOException("Imagem não encontrada");
            }
        } catch (MalformedURLException e) {
            // Lança exceção em caso de erro ao carregar a imagem
            throw new IOException("Erro ao carregar imagem", e);
        }
    }
}
