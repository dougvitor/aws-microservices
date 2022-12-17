package br.com.home.awsmicroservices.repository;

import br.com.home.awsmicroservices.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {

    Optional<Product> findByCode(String code);

}
