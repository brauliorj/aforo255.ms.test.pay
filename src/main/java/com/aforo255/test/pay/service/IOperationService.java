package com.aforo255.test.pay.service;

import com.aforo255.test.pay.domain.Operation;

public interface IOperationService {

	public Operation save (Operation operation);

	public Iterable<Operation> finAll();
}
