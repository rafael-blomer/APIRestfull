package com.example.SpringBootCurso.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRecordDto(@NotBlank(message = "Product name cannot be blank") String name, 
		@NotNull(message = "Product value cannot be null") BigDecimal value) {

}
