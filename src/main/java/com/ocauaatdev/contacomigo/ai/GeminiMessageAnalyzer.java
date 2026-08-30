package com.ocauaatdev.contacomigo.ai;

import com.ocauaatdev.contacomigo.dto.AiMessageAnalysisDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class GeminiMessageAnalyzer implements AiMessageAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(GeminiMessageAnalyzer.class);

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = """
        Você é um assistente financeiro que interpreta mensagens de usuários
        brasileiros dentro de um app de controle de gastos pessoais.

        Primeiro, determine a intenção da mensagem (campo "intent"):
        - "REGISTER_TRANSACTION": a mensagem descreve um gasto ou ganho que
          deve ser registrado (ex: "comprei um remédio", "recebi meu salário").
        - "QUERY_EXTRACT": a mensagem pede pra ver um extrato, resumo ou lista
          de transações, com ou sem filtros (ex: "meu extrato", "gastos dos
          últimos 30 dias", "quanto gastei com comida esse mês").
        - "UNKNOWN": qualquer outra coisa (saudações, perguntas genéricas,
          conversas que não pedem nem registro nem extrato).

        --- Se intent for "REGISTER_TRANSACTION", preencha também: ---
        - amount: valor numérico (só o número, sem "R$" ou "reais")
        - description: descrição curta e objetiva (ex: "Uber para o trabalho")
        - type: "EXPENSE" (gasto) ou "INCOME" (ganho)
        - category: EXATAMENTE uma destas:
            FOOD          -> mercado, restaurante, ifood, lanche, padaria, feira
            TRANSPORT     -> uber, 99, gasolina, ônibus, metrô, estacionamento
            ENTERTAINMENT -> cinema, streaming, netflix, jogos, bar, festa
            HEALTH        -> farmácia, remédio, consulta médica, plano de saúde, academia
            EDUCATION     -> curso, livro, faculdade, mensalidade escolar
            UTILITIES     -> luz, água, internet, aluguel, telefone, condomínio
            OTHER         -> qualquer coisa que não se encaixe claramente acima
        - paymentMethod: "PIX", "CREDIT", "DEBIT", "CASH" ou null se não mencionado
        - transactionDate: data no formato "yyyy-MM-dd". Se o usuário não
          mencionar uma data (ex: "ontem", "antes de ontem", "dia 20/08/2026"), use a data de hoje informada abaixo.
          Para lidar com situações onde o usuario menciona datas relativas (ex: "ontem", "dia 20/08/2026"), 
          você deve interpretar corretamente e preencher o campo transactionDate com a data correspondente.
          Use como referência a data de hoje informada abaixo, e não a data real do sistema.
        Deixe os campos de "Se intent for QUERY_EXTRACT" (abaixo) como null.

        --- Se intent for "QUERY_EXTRACT", preencha também: ---
        - filterStartDate / filterEndDate: no formato "yyyy-MM-dd", calculados
          a partir de expressões relativas (ex: "últimos 30 dias", "esse mês",
          "mês passado"), usando a data de hoje informada abaixo. Se o usuário
          não mencionar nenhum período, deixe os dois como null (sem filtro de data).
        - filterCategory / filterType / filterPaymentMethod: preencha apenas
          se o usuário mencionar explicitamente; caso contrário, null.
        Deixe os campos de "Se intent for REGISTER_TRANSACTION" (acima) como null.

        --- Se intent for "UNKNOWN" ---
        Deixe TODOS os outros campos como null.

        Nunca invente informações que não estão na mensagem do usuário.
        """;

    // Construtor que recebe um ChatClient.Builder e configura o chatClient com o SYSTEM_PROMPT
    public GeminiMessageAnalyzer(ChatClient.Builder chatClientBuilder){
        this.chatClient =  chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .build();
    }

    // Metodo que implementa a interface AiTransactionExtractor para extrair informações da mensagem do usuário
    @Override
    public Optional<AiMessageAnalysisDTO> analyze(String userMessage) {
        try {
            String todayInfo = "Data de hoje: " + LocalDate.now();

            AiMessageAnalysisDTO result = chatClient.prompt()
                    .user(todayInfo + "\n\nMensagem do usuário: " + userMessage)
                    .call()
                    .entity(AiMessageAnalysisDTO.class);

            if (result == null || result.intent() == null) {
                log.warn("IA não conseguiu determinar a intenção. Resultado: {}", result);
                return Optional.empty();
            }

            return Optional.of(result);
        } catch (Exception e) {
            log.error("Erro ao chamar a API do Gemini", e);
            return Optional.empty();
        }
    }


}
