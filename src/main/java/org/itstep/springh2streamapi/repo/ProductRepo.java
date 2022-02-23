package org.itstep.springh2streamapi.repo;
import java.util.List;

import org.itstep.springh2streamapi.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends CrudRepository<Product, Long> {

    List<Product> findAll();
}
