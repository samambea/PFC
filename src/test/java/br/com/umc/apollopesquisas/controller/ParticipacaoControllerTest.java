package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.enums.StatusParticipacao;
import br.com.umc.apollopesquisas.model.Participacao;
import br.com.umc.apollopesquisas.model.Pesquisa;
import br.com.umc.apollopesquisas.security.CustomUserDetails;
import br.com.umc.apollopesquisas.service.ParticipacaoService;
import br.com.umc.apollopesquisas.service.PesquisaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ParticipacaoControllerTest {

    @Mock
    private ParticipacaoService participacaoService;

    @Mock
    private PesquisaService pesquisaService;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private CustomUserDetails customUserDetails;

    @InjectMocks
    private ParticipacaoController participacaoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void participarDaPesquisa_QuandoUsuarioNaoAutenticado_RetornaRedirectLogin() {
        String pesquisaId = "123";

        String result = participacaoController.participarDaPesquisa(pesquisaId, null, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("alertMessage", "Faça login para confirmar sua participação.");
        assertEquals("redirect:/login", result);
    }

    @Test
    void participarDaPesquisa_QuandoPesquisaNaoEncontrada_RetornaRedirectHome() {
        String pesquisaId = "123";
        when(customUserDetails.getUsuarioId()).thenReturn("userId");
        when(pesquisaService.buscarPorId(pesquisaId)).thenReturn(Optional.empty());

        String result = participacaoController.participarDaPesquisa(pesquisaId, customUserDetails, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("alertMessage", "Pesquisa não encontrada.");
        assertEquals("redirect:/home", result);
    }

    @Test
    void participarDaPesquisa_QuandoParticipacaoJaExiste_RetornaRedirectHome() {
        String pesquisaId = "123";
        String usuarioId = "userId";
        when(customUserDetails.getUsuarioId()).thenReturn(usuarioId);
        when(pesquisaService.buscarPorId(pesquisaId)).thenReturn(Optional.of(new Pesquisa()));
        when(participacaoService.buscarPorUsuarioEPesquisa(usuarioId, pesquisaId))
            .thenReturn(Optional.of(new Participacao()));

        String result = participacaoController.participarDaPesquisa(pesquisaId, customUserDetails, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("alertMessage", "Você já está participando desta pesquisa.");
        assertEquals("redirect:/home", result);
    }

    @Test
    void participarDaPesquisa_QuandoSucesso_CriaNovaParticipacao() {
        String pesquisaId = "123";
        String usuarioId = "userId";
        when(customUserDetails.getUsuarioId()).thenReturn(usuarioId);
        when(pesquisaService.buscarPorId(pesquisaId)).thenReturn(Optional.of(new Pesquisa()));
        when(participacaoService.buscarPorUsuarioEPesquisa(usuarioId, pesquisaId))
            .thenReturn(Optional.empty());

        ArgumentCaptor<Participacao> participacaoCaptor = ArgumentCaptor.forClass(Participacao.class);

        String result = participacaoController.participarDaPesquisa(pesquisaId, customUserDetails, redirectAttributes);

        verify(participacaoService).criar(participacaoCaptor.capture());
        Participacao participacaoCriada = participacaoCaptor.getValue();
        
        assertEquals(usuarioId, participacaoCriada.getUsuarioId());
        assertEquals(pesquisaId, participacaoCriada.getPesquisaId());
        assertEquals(StatusParticipacao.INSCRITO, participacaoCriada.getStatusParticipacao());
        verify(redirectAttributes).addFlashAttribute("alertMessage", "Participação registrada com sucesso.");
        assertEquals("redirect:/home", result);
    }
}