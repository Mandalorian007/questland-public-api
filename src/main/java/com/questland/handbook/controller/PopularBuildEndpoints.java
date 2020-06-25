package com.questland.handbook.controller;

import com.questland.handbook.publicmodel.Build;
import com.questland.handbook.publicmodel.DisplayableBuild;
import com.questland.handbook.service.BuildDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PopularBuildEndpoints {

  private final BuildDataService buildDataService;

  @GetMapping("/build/{build}")
  public DisplayableBuild getGuild(@PathVariable("build") Build build) {
    return buildDataService.getBuildByApiName(build).orElseThrow(ResourceNotFoundException::new);
  }

}
