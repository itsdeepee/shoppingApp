package com.project.productservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.productservice.dto.ProductRequest;
import com.project.productservice.dto.ProductResponse;
import com.project.productservice.model.Product;
import com.project.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.testcontainers.shaded.org.hamcrest.Matchers.hasSize;
import static org.testcontainers.shaded.org.hamcrest.Matchers.is;

/**
 * Integration tests for ProductService using Spring Boot and Testcontainers.
 */
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {
	/**
	 * Container for MongoDB. This starts a MongoDB Docker container.
	 * The version of MongoDB is specified as 4.0.10.
	 */
	@Container
	static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

	/**
	 * MockMvc provides a powerful way to test MVC controllers without starting a full HTTP server.
	 */
	@Autowired
	private MockMvc mockMVC;

	/**
	 * ObjectMapper for JSON serialization and deserialization.
	 */
	@Autowired
	private ObjectMapper objectMapper;
	/**
	 * Autowired ProductRepository to interact with the Product database.
	 */
	@Autowired
	ProductRepository productRepository;


	static {
		mongoDBContainer.start();
	}
	/**
	 * Dynamic property source to set MongoDB URI for test environment.
	 * @param dynamicPropertyRegistry DynamicPropertyRegistry to set the properties.
	 */
	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.data.mongodb.uri",mongoDBContainer::getReplicaSetUrl);
	}

	@BeforeEach
	void setup(){
		productRepository.deleteAll();
	}

	/**
	 * Test for creating a product. This test ensures that a product can be created
	 * and appropriately stored in the MongoDB database.
	 * @throws Exception if any error occurs during the request processing.
	 */
	@Test
	void shouldCreateProduct() throws Exception {
		//mockMVC: provide us a mocked servlet environment where we can call product controllers
		//and our controller endpoints, and we can receive a response
		ProductRequest productRequest=getProductRequest();
		String productRequestString=objectMapper.writeValueAsString(productRequest);
		mockMVC.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productRequestString)
		).andExpect(status().isCreated());

		Assertions.assertEquals(1, productRepository.findAll().size());


	}

	@Test
	void shouldRetrieveProducts() throws Exception {
		String product1Name="Product 1";
		String product2Name="Product 2";

		productRepository.save(Product.builder().name(product1Name).description("processor").price(BigDecimal.valueOf(1000)).build());
		productRepository.save(Product.builder().name(product2Name).description("GC").price(BigDecimal.valueOf(1100)).build());



		MockHttpServletResponse response = mockMVC.perform(MockMvcRequestBuilders.get("/api/product"))
				.andExpectAll(
						status().isOk(),
						content().contentType("application/json")
				).andReturn().getResponse();


		List<ProductResponse> listProductResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<ProductResponse>>(){});
		Assertions.assertEquals(2,listProductResponse.size());
		Assertions.assertFalse(listProductResponse.get(0).getId().isEmpty());
		Assertions.assertFalse(listProductResponse.get(1).getId().isEmpty());
		Assertions.assertEquals(product1Name,listProductResponse.get(0).getName());
		Assertions.assertEquals(product2Name,listProductResponse.get(1).getName());



	}

	/**
	 * Helper method to create a ProductRequest object.
	 * @return ProductRequest object for testing.
	 */
	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("Lenovo")
				.description("laptop")
				.price(BigDecimal.valueOf(4500))
				.build();
	}

}
