package com.aforo255.test.pay.dao;

import org.springframework.data.repository.CrudRepository;

import com.aforo255.test.pay.domain.Operation;

public interface IOperationDao extends CrudRepository<Operation, Integer>{

}
