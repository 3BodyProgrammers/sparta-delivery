package com.example.spartadelivery.domain.menu.service;

import com.example.spartadelivery.domain.menu.entity.Menu;
import com.example.spartadelivery.domain.menu.repository.MenuRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuGetService {

    private final MenuRepository menuRepository;

    public List<Menu> findAllByStoreIdAndDeletedAtIsNull(Long id) {
        return menuRepository.findAllByStoreIdAndDeletedAtIsNull(id);
    }

    public Page<Menu> findAllByNameContainingAndDeletedAtIsNull(String name, Pageable pageable) {
        return menuRepository.findAllByNameContainingAndDeletedAtIsNull(name, pageable);
    }
}
