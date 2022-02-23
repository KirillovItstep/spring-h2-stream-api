package org.itstep.springh2streamapi.repo;

import java.util.List;

import org.itstep.springh2streamapi.model.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface OrderRepo extends CrudRepository<Order, Long> {

    List<Order> findAll();
}