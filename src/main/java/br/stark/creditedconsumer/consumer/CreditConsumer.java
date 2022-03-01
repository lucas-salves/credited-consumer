/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.stark.creditedconsumer.consumer;

import br.stark.creditedconsumer.dto.InvoiceDto;
import br.stark.creditedconsumer.http.StarkApi;
import br.stark.creditedconsumer.interactors.TransferUseCase;
import br.stark.creditedconsumer.model.Transfer;
import br.stark.creditedconsumer.service.AMQPPublisher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucas
 */
public class CreditConsumer extends DefaultConsumer {

    private final Channel channel;

    private final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    public CreditConsumer(Channel channel) {
        super(channel);
        this.channel = channel;
    }

    @Override
    public void handleDelivery(String csTag, Envelope env, AMQP.BasicProperties props, byte[] body) {
        try {

            var message = new String(body, "UTF-8");

            this.processDelivery(message);

            //tag, ack all messages
            channel.basicAck(env.getDeliveryTag(), false);

        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(0);

        } catch (Exception ex) {
            Logger.getLogger(CreditConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processDelivery(String message) throws Exception {
        System.out.println(message);

        var dto = gson.fromJson(message, InvoiceDto.class);

        if ("credited".equals(dto.getEvent().getLog().getInvoiceEventType())) {

            //façade
            var transferCreator = new TransferUseCase(new StarkApi());

            Transfer transfer = new Transfer();
            transfer.setAmount(dto.getEvent().getLog().getInvoice().getAmount());
            transfer.setFee(dto.getEvent().getLog().getInvoice().getFee());

            transferCreator.execute(transfer);

            var publisher = new AMQPPublisher();

            publisher.sendToQueue("credited_invoices", gson.toJson(dto));

            publisher.close();
        }
    }

}
