package com.questland.handbook.config;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SwaggerPageable {
    @ApiModelProperty(value = "Number of records per page", example = "20")
    int size;

    @ApiModelProperty(value = "Results page you want to retrieve (0..N)", example = "0")
    int page;

    @ApiModelProperty(value = "Sorting criteria in the format: property(,asc|desc)." +
            "Default sort order is ascending. Multiple sort criteria are supported.")
    String sort;
}
