package com.simple.ecom.site.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.ecom.site.productservice.model.Product;
import com.simple.ecom.site.productservice.repository.ProductRepository;
import com.simple.ecom.site.productservice.dto.ProductRequest;
import com.simple.ecom.site.productservice.dto.ProductResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ProductRepository productRepository;

	private ProductResponse productResponse;

	static {
		mongoDBContainer.start();
	}

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Test
	void shouldCreateProduct() throws Exception {
		ProductRequest productRequest = getProductRequest();
		String productRequestString = objectMapper.writeValueAsString(productRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productRequestString))
				.andExpect(status().isCreated());
		Assertions.assertEquals(productRepository.findAll().size(), 1);
	}

	@Test
	void shouldGetListOfEmptyProduct() throws Exception {
		ProductRequest productRequest = getProductRequest();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/products"))
				.andExpect(status().is4xxClientError());
        Assertions.assertTrue(productRepository.findAll().isEmpty());
	}

	@Test
	void shouldGetListOfProduct() throws Exception {
		Product one = Product.builder()
				.name("one")
				.description("one")
				.price(BigDecimal.valueOf(10))
				.build();

		Product two = Product.builder()
				.name("two")
				.description("two")
				.price(BigDecimal.valueOf(20))
				.build();

		Product[] products = {two, one};

		List<Product> allProducts = createProductInDB(products);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/products"))
				.andExpect(status().is4xxClientError());
		Assertions.assertFalse(productRepository.findAll().isEmpty());
		Assertions.assertEquals(productRepository.findAll().size(), 2);
	}

	private List<Product> createProductInDB(Product[] products){
		if (products == null) return Collections.emptyList();;
		return Arrays.stream(products).map(product -> productRepository.save(product)).toList();
	}
	private ProductRequest getProductRequest(){
		return ProductRequest.builder()
				.name("some product")
				.description("some product")
				.price(BigDecimal.valueOf(1200))
				.build();
	}
}
