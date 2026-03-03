package br.org.postalis.training.rh.shared.infrastructure.messaging;

import br.org.postalis.training.rh.funcionario.domain.Funcionario;
import br.org.postalis.training.rh.rh.infrastructure.FuncionarioRepository;
import br.org.postalis.training.rh.shared.infrastructure.EmailService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificacaoService {

    private final FuncionarioRepository funcionarioRepository;
    private final EmailService emailService;


    public void notificarGerenteNovasFerias(UUID funcionarioId,
                                            LocalDate dataInicio,
                                            LocalDate dataFim) {

        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Funcionário não encontrado: " + funcionarioId));

        String assunto = "Solicitação de férias";

        String mensagem = montarMensagem(funcionario.getNome(), dataInicio, dataFim);

        emailService.enviarConfirmacaoFerias(
                mensagem
        );
    }

    private String montarMensagem(String nomeFuncionario,
                                  LocalDate inicio,
                                  LocalDate fim) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return "O funcionário " + nomeFuncionario +
                " solicitou férias no período de " +
                inicio.format(formatter) + " até " +
                fim.format(formatter) + ".";
    }
}
