package com.questland.handbook.config;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SwaggerSort {

    @ApiModelProperty(value = "Sorting criteria in the format: property(,asc|desc)." +
            " Default sort order is ascending. Multiple sort criteria are supported.")
    String sort;
}
