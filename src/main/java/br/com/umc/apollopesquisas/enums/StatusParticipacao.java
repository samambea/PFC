package br.com.umc.apollopesquisas.enums;

// Enum que representa os possíveis status de uma participação em uma pesquisa.
public enum StatusParticipacao {
    INSCRITO,  // Participante se inscreveu na pesquisa, aguardando aprovação.
    APROVADO,  // Participação aprovada, participante autorizado a participar.
    REJEITADO, // Participação rejeitada, participante não autorizado.
    CANCELADO  // Participação cancelada pelo participante ou pesquisador.
}
