package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.*;
import br.com.umc.apollopesquisas.service.AdministradorService;
import br.com.umc.apollopesquisas.service.FeedbackService;
import br.com.umc.apollopesquisas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @Autowired
    private AdministradorService administradorService;

    @Autowired
    private FeedbackService feedbackService;

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

        // DADOS PARA FORMULÁRIO: Preenche campos do formulário de edição
        model.addAttribute("usuario", (Voluntario) usuarioOptional.get());
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

        // DADOS PARA FORMULÁRIO: Preenche campos do formulário de edição
        model.addAttribute("usuario", (Pesquisador) usuarioOptional.get());
        return "editar-pesquisador"; // View específica para pesquisadores
    }

    // Processa submissão do formulário de edição de pesquisador.
    // Salva alterações e redireciona com mensagem de sucesso.
    @PostMapping("/salvar-pesquisador")
    public String salvarPesquisador(@ModelAttribute Pesquisador pesquisador) {
        // PERSISTÊNCIA: Salva alterações do pesquisador
        usuarioService.save(pesquisador);
        // REDIRECIONAMENTO COM SUCESSO
        return "redirect:/admin/usuarios?success=editado";
    }

    // Carrega formulário de edição específico para administradores.
    // Mesma lógica de validação por tipo e existência.
    @GetMapping("/editar-administrador/{id}")
    public String editarAdministrador(@PathVariable String id, Model model) {
        Optional<Usuario> usuarioOptional = usuarioService.findById(id);
        if (usuarioOptional.isEmpty() || !(usuarioOptional.get() instanceof Administrador)) {
            return "redirect:/admin/usuarios?error=notfound";
        }

        // DADOS PARA FORMULÁRIO
        model.addAttribute("administrador", (Administrador) usuarioOptional.get());
        return "editar-administrador";
    }

    // Processa submissão do formulário de edição de administrador.
    @PostMapping("/salvar-administrador")
    public String salvarAdministrador(@ModelAttribute Administrador admin) {
        // Usa serviço específico para administrador
        administradorService.salvar(admin);
        return "redirect:/admin/usuarios?success=editado";
    }

    // Exibe formulário para cadastro de novo administrador.
    @GetMapping("/cadastrar-admin")
    public String mostrarFormCadastroAdmin(Model model) {
        model.addAttribute("usuario", new Administrador());
        return "cadastrar-admin";
    }

    // Processa cadastro de novo administrador.
    @PostMapping("/cadastrar-admin")
    public String cadastrarAdministrador(@ModelAttribute Administrador administrador) {
        administradorService.salvar(administrador);
        return "redirect:/admin/usuarios/lista-usuarios?success=cadastrado";
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
    @GetMapping("/sugestoes")
    public String listarSugestoes(Model model) {
        return "sugestoes/listar"; // View em subdiretório
    }

    // Lista todos os feedbacks recebidos.
    @GetMapping("/feedbacks")
    public String listarFeedbacks(Model model) {
        List<Feedback> feedbacks = feedbackService.listarTodos();
        model.addAttribute("feedbacks", feedbacks);
        return "listar-feedbacks";
    }

    // Redirecionamento padrão para a rota base do admin.
    // Quando acessam /admin/usuarios, são direcionados para a lista.
    @GetMapping
    public String redirecionarParaListaUsuarios() {
        return "redirect:/admin/usuarios/lista-usuarios";
    }
}
