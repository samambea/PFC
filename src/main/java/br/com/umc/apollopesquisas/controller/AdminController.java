package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Pesquisador;
import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.model.Voluntario;
import br.com.umc.apollopesquisas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


  // Controller responsável pelas funcionalidades administrativas da aplicação.
  // Gerencia operações CRUD de usuários que só podem ser executadas por administradores.
  // Todas as rotas deste controller são protegidas pela role ADMIN no SecurityConfig.

@Controller // Marca como controller MVC que retorna views (páginas HTML)
@RequestMapping("/admin/usuarios") // Prefixo base para todas as rotas deste controller
public class AdminController {

    // Serviço para operações com usuários
    @Autowired
    private UsuarioService usuarioService;


      // Exibe lista de todos os usuários do sistema.
      // Página principal do painel administrativo para gerenciamento de usuários.

    @GetMapping("/lista-usuarios")
    public String listarUsuarios(Model model) {
        // DADOS PARA VIEW: Adiciona lista completa de usuários ao modelo
        model.addAttribute("usuarios", usuarioService.findAll());
        // Retorna nome da view Thymeleaf (templates/lista-usuarios.html)
        return "lista-usuarios";
    }


      // Carrega formulário de edição específico para voluntários.
      // Inclui validações de tipo e existência do usuário.

    @GetMapping("/editar-voluntario/{id}")
    public String editarVoluntario(@PathVariable String id, Model model) {
        // BUSCA E VALIDAÇÃO: Procura usuário pelo ID
        Optional<Usuario> usuarioOptional = usuarioService.findById(id);

        // VALIDAÇÃO DUPLA: Verifica se existe E se é do tipo correto
        if (usuarioOptional.isEmpty() || !(usuarioOptional.get() instanceof Voluntario)) {
            // REDIRECIONAMENTO COM ERRO: Volta para lista com mensagem de erro
            return "redirect:/admin/usuarios?error=notfound";
        }

        // CAST SEGURO: Converte para tipo específico após validação
        Voluntario voluntario = (Voluntario) usuarioOptional.get();

        // DADOS PARA FORMULÁRIO: Preenche campos do formulário de edição
        model.addAttribute("usuario", voluntario);
        return "editar-voluntario"; // View específica para voluntários
    }


    // Processa submissão do formulário de edição de voluntário.
    // Salva alterações e redireciona com mensagem de sucesso.

    @PostMapping("/salvar-voluntario")
    public String salvarVoluntario(@ModelAttribute Voluntario voluntario) {
        // PERSISTÊNCIA: Salva/atualiza dados do voluntário
        usuarioService.save(voluntario);
        // REDIRECIONAMENTO COM SUCESSO: Volta para lista com confirmação
        return "redirect:/admin/usuarios?success=editado";
    }


      // Carrega formulário de edição específico para pesquisadores.
      // Lógica idêntica ao editar voluntário, mas com tipo diferente.

    @GetMapping("/editar-pesquisador/{id}")
    public String editarPesquisador(@PathVariable String id, Model model) {
        // BUSCA E VALIDAÇÃO: Mesma lógica do voluntário
        Optional<Usuario> usuarioOptional = usuarioService.findById(id);

        // VALIDAÇÃO DE TIPO: Verifica se é pesquisador
        if (usuarioOptional.isEmpty() || !(usuarioOptional.get() instanceof Pesquisador)) {
            return "redirect:/admin/usuarios?error=notfound";
        }

        // CAST PARA TIPO ESPECÍFICO
        Pesquisador pesquisador = (Pesquisador) usuarioOptional.get();
        model.addAttribute("usuario", pesquisador);
        return "editar-pesquisador"; // View específica para pesquisadores
    }


      // Processa submissão do formulário de edição de pesquisador.

    @PostMapping("/salvar-pesquisador")
    public String salvarPesquisador(@ModelAttribute Pesquisador pesquisador) {
        // PERSISTÊNCIA: Salva alterações do pesquisador
        usuarioService.save(pesquisador);
        return "redirect:/admin/usuarios?success=editado";
    }


      // Remove usuário do sistema.
      // Operação irreversível que deve ser usada com cuidado.

    @GetMapping("/deletar/{id}")
    public String deletarUsuario(@PathVariable("id") String id) {
        // REMOÇÃO: Deleta usuário permanentemente do banco
        usuarioService.deleteById(id);
        // CONFIRMAÇÃO: Redireciona com mensagem de sucesso
        return "redirect:/admin/usuarios?success=deletado";
    }


      // Endpoint para funcionalidade futura de sugestões.
      // Atualmente com implementação placeholder.

    @GetMapping("/admin/sugestoes")
    public String listarSugestoes(Model model) {
        // FUNCIONALIDADE FUTURA: Código comentado para implementação posterior
        // opcional: adicionar sugestões no model
        // model.addAttribute("sugestoes", sugestaoService.buscarTodas());

        return "sugestoes/listar"; // View em subdiretório
    }


    // Redirecionamento padrão para a rota base do admin.
    // Quando acessam /admin/usuarios, são direcionados para a lista.

    @GetMapping
    public String redirecionarParaListaUsuarios() {
        // REDIRECIONAMENTO INTERNO: Evita duplicação de lógica
        return "redirect:/admin/usuarios/lista-usuarios";
    }

}