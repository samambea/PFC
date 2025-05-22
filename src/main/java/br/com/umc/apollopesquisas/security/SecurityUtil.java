package br.com.umc.apollopesquisas.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

// Utilitário para obter informações do usuário autenticado na sessão atual.
public class SecurityUtil {

    // Retorna o nome do usuário autenticado na sessão atual.
    // Se não houver usuário autenticado, retorna null.
    public static String getSessionUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
}
