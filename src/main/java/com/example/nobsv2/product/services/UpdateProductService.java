package com.example.nobsv2.product.services;

import com.example.nobsv2.Command;
import com.example.nobsv2.exceptions.ProductNotFoundException;
import com.example.nobsv2.product.ProductRepository;
import com.example.nobsv2.product.model.Product;
import com.example.nobsv2.product.model.ProductDTO;
import com.example.nobsv2.product.model.UpdateProductCommand;
import com.example.nobsv2.product.validators.ProductValidator;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpdateProductService implements Command<UpdateProductCommand, ProductDTO> {

    private final ProductRepository productRepository;

    public UpdateProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @CachePut(value = "productCache", key = "#command.getId()")
    //CacheEvict -> throws it away only
    //CachePut -> throws away then puts return value of method into cache
    public ResponseEntity<ProductDTO> execute(UpdateProductCommand command) {
        Optional<Product> productOptional = productRepository.findById(command.getId());
        if (productOptional.isPresent()) {
            Product product = command.getProduct();
            product.setId(command.getId());
//            ProductValidator.execute(product);
            productRepository.save(product);
            return ResponseEntity.ok(new ProductDTO(product));
        }

        //want to throw product not found exception here in the future
        throw new ProductNotFoundException();
    }
}
