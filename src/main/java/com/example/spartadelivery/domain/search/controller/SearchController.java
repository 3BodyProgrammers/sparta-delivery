package com.example.spartadelivery.domain.search.controller;

import com.example.spartadelivery.domain.search.service.SearchService;
import com.example.spartadelivery.domain.store.dto.response.StoreResponseDto;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/searchs")
@Validated
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<Page<StoreResponseDto>> search(@RequestParam @NotBlank(message = "검색어는 필수 값입니다.") String name,
                                                         @RequestParam(defaultValue = "1") Integer page,
                                                         @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(searchService.search(name, page, size));
    }
}
