package br.stark.creditedconsumer;

import br.stark.creditedconsumer.http.StarkApi;
import br.stark.creditedconsumer.interactors.TransferUseCase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import br.stark.creditedconsumer.model.Transfer;
import br.stark.creditedconsumer.repository.TransferRepository;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

@SpringBootApplication
public class CreditedConsumerApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CreditedConsumerApplication.class, args);

        CreditWorker.main(null);

    }

}
