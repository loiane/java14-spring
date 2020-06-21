package com.loiane;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository repository;

    private final ProductService service;

    record Response(@JsonProperty List<Product> list, @JsonProperty int total){}

    public ProductController(ProductRepository repository, ProductService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping
    public @ResponseBody Response findAll() {
        var list = repository.findAll();
        return new Response(list, list.size());
    }

    @GetMapping("{id}")
    public ProductRecord findById(@PathVariable Integer id) {
        return service.findById(id);
    }

    @PostMapping
    public ProductRecord create(@RequestBody @Valid ProductDTO product) {
        // return repository.save(product.toEntity());
        var statusCode = switch (product.status()) {
            case 1 -> ProductStatus.ACTIVE;
            case 0 -> ProductStatus.INACTIVE;
            default -> throw new IllegalArgumentException("incorrect status.");
        };
        return service.create(product.name(), statusCode);
    }
}
