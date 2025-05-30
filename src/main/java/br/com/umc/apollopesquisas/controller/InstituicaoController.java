package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Instituicao;
import br.com.umc.apollopesquisas.repository.InstituicaoRepository;
import br.com.umc.apollopesquisas.service.InstituicaoService;
import br.com.umc.apollopesquisas.service.PesquisaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller // Define que esta classe é um Controller do Spring MVC
@RequestMapping("/instituicoes")  // Define o caminho base para as rotas deste controller
public class InstituicaoController {

    @Autowired
    private InstituicaoService instituicaoService;

    @Autowired
    private InstituicaoRepository instituicaoRepository;

    @Autowired
    private PesquisaService pesquisaService;

    //Exibe o formulário para cadastrar uma nova instituição.


    @GetMapping("/nova")
    @PreAuthorize("hasRole('ADMIN')")
    public String novaForm(Model model) {
        model.addAttribute("instituicao", new Instituicao());
        return "cadastrar-instituicao";
    }

//Exibe o formulário de edição de uma instituição específica.
    @GetMapping("/{id}/editar")
    @PreAuthorize("hasRole('ADMIN')")
    public String editarForm(@PathVariable String id, Model model) {
        Instituicao instituicao = instituicaoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Instituição não encontrada"));
        model.addAttribute("instituicao", instituicao);
        return "editar-instituicao";
    }
//Atualiza uma instituição existente.
    @PostMapping("/{id}/editar")
    @PreAuthorize("hasRole('ADMIN')")
    public String atualizar(@PathVariable String id, @ModelAttribute Instituicao instituicaoAtualizada) {
        instituicaoService.atualizar(id, instituicaoAtualizada);
        return "redirect:/instituicoes";
    }
//Exclui uma instituição existente.
    @PostMapping("/{id}/excluir")
    @PreAuthorize("hasRole('ADMIN')")
    public String deletar(@PathVariable String id) {
        instituicaoService.deletarPorId(id);
        return "redirect:/instituicoes";
    }

    // API REST
    @GetMapping("/api")
    @ResponseBody
    public List<Instituicao> listarApi() {
        return instituicaoService.listarTodas();
    }
//Endpoint REST: Busca uma instituição por ID (JSON)
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Instituicao> buscarApi(@PathVariable String id) {
        return instituicaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
//Lista todas as instituições para exibição na página HTML.
    @GetMapping
    public String listarInstituicoes(Model model) {
        model.addAttribute("instituicoes", instituicaoService.listarTodas());
        return "listar-instituicoes";


    }
//Salva uma nova instituição (ou atualização vinda do formulário).
    @PostMapping("/salvar")
    public String salvarInstituicao(@ModelAttribute Instituicao instituicao) {
        instituicaoRepository.save(instituicao);
        return "redirect:/instituicoes";
    }


}
