package com.example.model;

import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Introspected
public class Review {

    @EqualsAndHashCode.Include
    @NotNull
    private UUID id;

    @NotEmpty
    private String text;

    @NotNull
    private Float mark;

    @NotNull
    private String productSku;
}
