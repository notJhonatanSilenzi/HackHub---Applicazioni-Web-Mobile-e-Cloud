package unicam.cs.hackhub.testHttp;

import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@Sql(
        statements = {
                "SET FOREIGN_KEY_CHECKS=0",
                "TRUNCATE TABLE valutazioni",
                "TRUNCATE TABLE sottomissioni",
                "TRUNCATE TABLE richiesta",
                "TRUNCATE TABLE notifica",
                "TRUNCATE TABLE iscrizione_team",
                "TRUNCATE TABLE membro_team",
                "TRUNCATE TABLE staff",
                "TRUNCATE TABLE team",
                "TRUNCATE TABLE hackathon",
                "TRUNCATE TABLE utenti",
                "SET FOREIGN_KEY_CHECKS=1"
        },
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD
)
abstract class BaseHttpIT {
}

