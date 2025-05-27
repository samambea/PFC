package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Pesquisa;
import br.com.umc.apollopesquisas.service.InstituicaoService;
import br.com.umc.apollopesquisas.service.PesquisaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


// Controller responsável pelas operações administrativas relacionadas a pesquisas.
// Permite listagem, edição, atualização, remoção e correção de status de pesquisas.
// Todas as rotas deste controller são protegidas pela role ADMIN no SecurityConfig.

@Controller // Marca como controller MVC que retorna views (páginas HTML)
@RequestMapping("/admin/pesquisas") // Prefixo base para todas as rotas deste controller
public class AdminPesquisaController {

    // Serviço para operações com pesquisas
    @Autowired
    private PesquisaService pesquisaService;


    @Autowired
    private InstituicaoService instituicaoService;


    // Exibe lista de todas as pesquisas cadastradas no sistema.
    // Página principal para administração de pesquisas.

    @GetMapping
    public String listarPesquisas(Model model) {
        // DADOS PARA VIEW: Adiciona lista de pesquisas ao modelo
        model.addAttribute("pesquisas", pesquisaService.listarTodas());
        // Retorna nome da view Thymeleaf (listar-pesquisas.html)
        return "listar-pesquisas";
    }


    // Carrega formulário de edição de uma pesquisa específica.
    // Inclui validação de existência.

    @GetMapping("/editar/{pesquisaId}")
    public String editarPesquisa(@PathVariable String pesquisaId, Model model) {
        // BUSCA: Procura pesquisa pelo ID
        Optional<Pesquisa> pesquisa = pesquisaService.buscarPorId(pesquisaId);

        // VALIDAÇÃO: Verifica se existe
        if (pesquisa.isPresent()) {
            // DADOS PARA FORMULÁRIO: Preenche campos do formulário de edição
            model.addAttribute("pesquisa", pesquisa.get());
            model.addAttribute("instituicoes", instituicaoService.listarTodas()); // <- ADICIONA AQUI

            return "editar-pesquisa";
        }
        // REDIRECIONAMENTO: Volta para lista se não encontrar
        return "redirect:/admin/pesquisas";
    }



    // Processa submissão do formulário de edição de pesquisa.
    // Atualiza dados e redireciona para a lista.

    @PostMapping("/atualizar/{pesquisaId}")
    public String atualizarPesquisa(@PathVariable String pesquisaId, @ModelAttribute Pesquisa pesquisaAtualizada) {
        // SETA ID: Garante que o ID da pesquisa está correto
        pesquisaAtualizada.setPesquisaId(pesquisaId);
        // ATUALIZAÇÃO: Salva alterações da pesquisa
        pesquisaService.atualizar(pesquisaId, pesquisaAtualizada);
        // REDIRECIONAMENTO: Volta para lista de pesquisas
        return "redirect:/admin/pesquisas";
    }


    // Remove pesquisa do sistema.
    // Operação irreversível.

    @GetMapping("/deletar/{pesquisaId}")
    public String deletarPesquisa(@PathVariable String pesquisaId) {
        // REMOÇÃO: Deleta pesquisa permanentemente
        pesquisaService.deletarPorId(pesquisaId);
        // CONFIRMAÇÃO: Redireciona com mensagem de sucesso
        return "redirect:/admin/pesquisas?success=deletado";
    }


    // Corrige o status enum de todas as pesquisas.
    // Endpoint para manutenção administrativa.

    @PostMapping("/corrigir-status")
    public ResponseEntity<String> corrigirStatus() {
        // CORREÇÃO: Atualiza status das pesquisas
        pesquisaService.corrigirStatusPesquisaEnum();
        // RETORNO: Confirmação da operação
        return ResponseEntity.ok("Status corrigidos.");
    }

}
