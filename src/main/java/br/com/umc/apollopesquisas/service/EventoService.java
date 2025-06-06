package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.Evento;
import br.com.umc.apollopesquisas.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    // Cria um novo evento

    public Evento criar(Evento evento) {
        evento.setEventoId(null);

        // Define a data de criação como a data atual, caso esteja nula
        if (evento.getDataCriacao() == null) {
            evento.setDataCriacao(LocalDate.now());
        }

        return eventoRepository.save(evento);
    }


     //Retorna todos os eventos cadastrados

    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }


     // Busca um evento pelo ID

    public Optional<Evento> buscarPorId(String id) {
        return eventoRepository.findById(id);
    }

    // Atualiza um evento existente com base no ID.

    public Evento atualizar(String id, Evento novoEvento) {
        Evento eventoExistente = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado!"));

        // Preserva o nome da imagem anterior, caso uma nova não tenha sido enviada
        if (novoEvento.getNomeImagem() == null || novoEvento.getNomeImagem().isEmpty()) {
            novoEvento.setNomeImagem(eventoExistente.getNomeImagem());
        }

        // Preserva também a data de criação original
        novoEvento.setDataCriacao(eventoExistente.getDataCriacao());

        // Garante que o ID seja o mesmo (caso não tenha vindo no novo objeto)
        novoEvento.setEventoId(id);

        return eventoRepository.save(novoEvento);
    }

    // Deleta um evento com base no ID. Retorna true se excluído com sucesso, false se não encontrado.

    public boolean deletar(String id) {
        if (eventoRepository.existsById(id)) {
            eventoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Salva a imagem enviada no diretório local e retorna o nome do arquivo salvo. Usa UUID para evitar conflitos de nome.

    public String salvarImagem(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Arquivo vazio");
        }

        String nomeArquivo = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path pasta = Paths.get(System.getProperty("user.dir"), "uploads", "imagens");

        Files.createDirectories(pasta); // Cria diretório se não existir

        Path destino = pasta.resolve(nomeArquivo);
        file.transferTo(destino.toFile());

        return nomeArquivo;
    }

}
