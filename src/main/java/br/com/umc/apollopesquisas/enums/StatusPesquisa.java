package br.com.umc.apollopesquisas.enums;

// Enum que representa os possíveis status de uma pesquisa.
public enum StatusPesquisa {
    ABERTA,    // Pesquisa está aberta para inscrições e participações.
    FECHADA,   // Pesquisa está fechada para novas inscrições, mas pode estar em andamento.
    CANCELADA, // Pesquisa foi cancelada e não será concluída.
    CONCLUIDA  // Pesquisa foi concluída com sucesso.
}
