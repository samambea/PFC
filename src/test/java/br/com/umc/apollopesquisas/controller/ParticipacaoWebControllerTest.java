package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.service.ParticipacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ParticipacaoWebControllerTest {

    @Mock
    private ParticipacaoService participacaoService;
    
    @Mock
    private Principal principal;
    
    @Mock
    private RedirectAttributes redirectAttributes;
    
    @InjectMocks
    private ParticipacaoController participacaoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void participar_QuandoSucesso_RetornaRedirectHome() {
        // Arrange
        String pesquisaId = "123";
        String email = "user@example.com";
        when(principal.getName()).thenReturn(email);

        // Act
        String result = participacaoController.participar(pesquisaId, principal, redirectAttributes);

        // Assert
        verify(participacaoService).registrarParticipacao(pesquisaId, email);
        verify(redirectAttributes).addFlashAttribute("alertMessage", 
            "Participação confirmada! Em breve, o pesquisador responsável entrará em contato.");
        assertEquals("redirect:/home", result);
    }

    @Test
    void participar_QuandoErro_RetornaRedirectHomeComMensagemDeErro() {
        // Arrange
        String pesquisaId = "123";
        String email = "user@example.com";
        String errorMessage = "Erro ao registrar participação";
        when(principal.getName()).thenReturn(email);
        doThrow(new RuntimeException(errorMessage))
            .when(participacaoService).registrarParticipacao(pesquisaId, email);

        // Act
        String result = participacaoController.participar(pesquisaId, principal, redirectAttributes);

        // Assert
        verify(participacaoService).registrarParticipacao(pesquisaId, email);
        verify(redirectAttributes).addFlashAttribute("alertMessage", errorMessage);
        assertEquals("redirect:/home", result);
    }

    @Test
    void participar_QuandoPrincipalNull_RetornaRedirectHomeComErro() {
        String pesquisaId = "123";

        assertThrows(NullPointerException.class, () ->
                participacaoController.participar(pesquisaId, null, redirectAttributes));

        verifyNoInteractions(participacaoService);
        verifyNoInteractions(redirectAttributes);
    }

}
