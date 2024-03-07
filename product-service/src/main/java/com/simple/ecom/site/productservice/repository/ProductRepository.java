package com.simple.ecom.site.productservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.simple.ecom.site.productservice.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {

}
