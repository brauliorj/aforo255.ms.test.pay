package com.aforo255.test.pay.kafka.producer;

import java.util.List;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.aforo255.test.pay.domain.Operation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class PaymentEventProducer {

	String topic = "operation-events";
	private Logger logger = LoggerFactory.getLogger(PaymentEventProducer.class);
	
	@Autowired
	private KafkaTemplate<Integer, String> kafkaTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	public ListenableFuture<SendResult<Integer, String>> sendPaymentEvent(Operation operationEvent) throws JsonProcessingException {
		Integer key = operationEvent.getId();
		String value = objectMapper.writeValueAsString(operationEvent);
		
		ProducerRecord<Integer, String> producerRecord = buildProducerRecord(key, value, topic);
		
		ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.send(producerRecord);
		
		listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {

			@Override
			public void onSuccess(SendResult<Integer, String> result) {
				try {
					handleSuccess(key, value, result);
				} catch (Exception e) {
					e.printStackTrace();
				}					
			}

			@Override
			public void onFailure(Throwable ex) {
				handleFailure(key, value, ex);
			}
		
		});
		
		return listenableFuture;
	}
	
	private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic) {
		List<Header> recordHeaders = 
				List.of(new RecordHeader("payment-event-source", "scanner".getBytes()));
		return new ProducerRecord<>(topic, null, key, value, recordHeaders);
	}
	
	private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
		logger.info("Message sent SuccessFully for the key: {} and the value is {}, partition is {}", 
				key, value,	result.getRecordMetadata().partition());
	}
	
	private void handleFailure(Integer key, String value, Throwable e) {
		logger.error("Error Sending the Message and the exception is {}", e.getMessage());
		try {
			throw e;
		} catch (Throwable throwable) {
			logger.error("Error in handleFailure: {}", throwable.getMessage());
		}
	}
}
