package com.aforo255.test.pay.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aforo255.test.pay.domain.Operation;
import com.aforo255.test.pay.kafka.producer.PaymentEventProducer;
import com.aforo255.test.pay.service.IOperationService;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
public class PaymentEventController {

	private Logger logger = LoggerFactory.getLogger(PaymentEventController.class);
	
	@Autowired
	private PaymentEventProducer paymentProducer;
	
	@Autowired
	private IOperationService operationService;
	
	@PostMapping("/paymentevent")
	public ResponseEntity<Operation> postPaymentEvent(@RequestBody Operation operationRequest) throws JsonProcessingException {
		logger.info("Inicio save to MySql");
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss.SSS");
		operationRequest.setDatetime(sdf.format(new Date()));
		Operation operationSql = operationService.save(operationRequest);
		logger.info("Fin save to MySql");
		
		logger.info("Inicio sendPaymentEvent");
		paymentProducer.sendPaymentEvent(operationSql);
		logger.info("Fin sendPaymentEvent");
		
		return ResponseEntity.status(HttpStatus.CREATED).body(operationSql);
	}
	
	@GetMapping("/payments")
	public Iterable<Operation> operations() {
		return operationService.finAll();
	}
}
