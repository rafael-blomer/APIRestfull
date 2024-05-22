package controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dtos.ProductRecordDto;
import jakarta.validation.Valid;
import models.ProductsModel;
import repositories.ProductRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;;

@RestController
public class ProductController {

	@Autowired
	ProductRepository repo;
	
	@PostMapping("/products")
	public ResponseEntity<ProductsModel> saveProduct(@RequestBody @Valid ProductRecordDto prDto) {
		var productmodel = new ProductsModel();
		BeanUtils.copyProperties(prDto, productmodel);
		return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(productmodel));
	}
	
	@GetMapping("/products")
	public ResponseEntity<List<ProductsModel>> getAllProducts() {
		List<ProductsModel> productslist = repo.findAll();
		if (!productslist.isEmpty()) {
			for (ProductsModel product : productslist) {
				UUID id = product.getIdProduct();
				product.add(linkTo(methodOn(ProductController.class).getProduct(id)).withSelfRel());
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body(productslist);
	}
	
	@GetMapping("/products/{id}")
	public ResponseEntity<Object> getProduct(@PathVariable(value="id") UUID id) {
		Optional<ProductsModel> product0 = repo.findById(id);
		if(product0.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
		}
		product0.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withSelfRel());
		return ResponseEntity.status(HttpStatus.OK).body(product0.get());
	}
	
	@PutMapping("/products/{id}")
	public ResponseEntity<Object> updateProduct(@PathVariable(value="id") UUID id, @RequestBody @Valid ProductRecordDto prDto) {
		Optional<ProductsModel> product0 = repo.findById(id);
		if(product0.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
		}
		var productModel = product0.get();
		BeanUtils.copyProperties(prDto, productModel);
		return ResponseEntity.status(HttpStatus.OK).body(repo.save(productModel));
	}
	
	@DeleteMapping("/products/{id}")
	public ResponseEntity<Object> deleteProduct(@PathVariable(value="id") UUID id) {
		Optional<ProductsModel> product0 = repo.findById(id);
		if(product0.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
		}
		repo.delete(product0.get());
		return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully");
	}
}
