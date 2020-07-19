package com.aforo255.test.pay.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aforo255.test.pay.dao.IOperationDao;
import com.aforo255.test.pay.domain.Operation;

@Service
public class OperationServiceImpl implements IOperationService {

	@Autowired
	private IOperationDao operationDao;	
	
	@Override
	@Transactional
	public Operation save(Operation operation) {
		return operationDao.save(operation);
	}

	@Override
	public Iterable<Operation> finAll() {
		return operationDao.findAll();
	}
}
