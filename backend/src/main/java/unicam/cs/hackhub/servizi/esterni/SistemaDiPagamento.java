package unicam.cs.hackhub.servizi.esterni;

import java.math.BigDecimal;

public interface SistemaDiPagamento {

    void pagaPremio(String recapitoOrg, String recapitoBancario, BigDecimal premio);
}
