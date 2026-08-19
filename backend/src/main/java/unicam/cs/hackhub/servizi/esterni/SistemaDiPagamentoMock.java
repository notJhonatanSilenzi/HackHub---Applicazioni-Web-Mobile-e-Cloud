package unicam.cs.hackhub.servizi.esterni;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SistemaDiPagamentoMock implements SistemaDiPagamento {

    public void pagaPremio(String recapitoOrg, String recapitoBancario, BigDecimal premio){}
}
