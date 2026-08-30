package com.ocauaatdev.contacomigo.ai;

import com.ocauaatdev.contacomigo.dto.AiExtractedTransactionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GeminiTransactionExtractor implements AiTransactionExtractor{

    private static final Logger log = LoggerFactory.getLogger(GeminiTransactionExtractor.class);

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = """
        Você é um assistente financeiro que extrai dados estruturados de mensagens
        de usuários brasileiros sobre seus gastos e ganhos.

        A partir da mensagem do usuário, extraia:

        - amount: o valor numérico da transação (só o número, sem "R$" ou "reais")
        - description: uma descrição curta e objetiva (ex: "Uber para o trabalho")
        - type: "EXPENSE" se foi um gasto, "INCOME" se foi um ganho/recebimento
        - category: escolha EXATAMENTE uma destas, sem inventar outras:
            FOOD          -> mercado, restaurante, ifood, lanche, padaria, feira
            TRANSPORT     -> uber, 99, gasolina, ônibus, metrô, estacionamento
            ENTERTAINMENT -> cinema, streaming, netflix, jogos, bar, festa
            HEALTH        -> farmácia, remédio, consulta médica, plano de saúde, academia
            EDUCATION     -> curso, livro, faculdade, mensalidade escolar
            UTILITIES     -> luz, água, internet, aluguel, telefone, condomínio
            OTHER         -> qualquer coisa que não se encaixe claramente acima
        - paymentMethod: "PIX", "CREDIT", "DEBIT" ou "CASH" se for mencionado
          explícita ou implicitamente; caso contrário, null.
        - transactionDate: a data da transação no formato "yyyy-MM-dd", se mencionada; caso contrário, a data atual.

        Se não for possível identificar um valor numérico, retorne amount como null.
        Nunca invente informações que não estão na mensagem.
        """;

    // Construtor que recebe um ChatClient.Builder e configura o chatClient com o SYSTEM_PROMPT
    public GeminiTransactionExtractor(ChatClient.Builder chatClientBuilder){
        this.chatClient =  chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .build();
    }

    // Metodo que implementa a interface AiTransactionExtractor para extrair informações da mensagem do usuário
    @Override
    public Optional<AiExtractedTransactionDTO> extract(String userMessage) {
        try {
            AiExtractedTransactionDTO result = chatClient.prompt() // Inicia um prompt de chat
                    .user(userMessage)// Adiciona a mensagem do usuário ao prompt
                    .call()// Executa o prompt e obtém a resposta
                    .entity(AiExtractedTransactionDTO.class); // Converte a resposta em um objeto AiExtractedTransactionDTO

            if (result == null || result.amount() == null) {
                log.warn("IA nao conseguiu extrair amount. Resultado: {}", result);
                return Optional.empty();
            }

            return Optional.of(result);
        } catch (Exception e) {
            log.error("Erro ao chamar a API do Gemini", e);
            return Optional.empty();
        }
    }


}
