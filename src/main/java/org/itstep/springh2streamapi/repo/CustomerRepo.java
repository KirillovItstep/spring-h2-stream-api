package org.itstep.springh2streamapi.repo;

import java.util.List;

import org.itstep.springh2streamapi.model.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomerRepo extends CrudRepository<Customer, Long> {
    List<Customer> findAll();
}
