package com.projects.coffee.controller;

import com.projects.coffee.dto.BeanDTO;
import com.projects.coffee.dto.BeanDisplayDTO;
import com.projects.coffee.service.BeanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/beans")
public class BeanController {

    private final BeanService beanService;

    public BeanController(BeanService beanService) {
        this.beanService = beanService;
    }

    @GetMapping
    public ResponseEntity<List<BeanDisplayDTO>> getAllBeans() {
        try {
            List<BeanDisplayDTO> beans = beanService.getAllBeans();
            return ResponseEntity.ok(beans);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeanDisplayDTO> getBeanById(@PathVariable Long id) {
        try {
            BeanDisplayDTO bean = beanService.getBeanById(id);
            if (bean != null) {
                return ResponseEntity.ok(bean);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<BeanDisplayDTO> createBean(@Valid @RequestBody BeanDTO beanDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            BeanDisplayDTO createdBean = beanService.createBean(beanDTO, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBean);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeanDisplayDTO> updateBean(@PathVariable Long id, @Valid @RequestBody BeanDTO beanDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            BeanDisplayDTO updatedBean = beanService.updateBean(id, beanDTO, username);
            if (updatedBean != null) {
                return ResponseEntity.ok(updatedBean);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBean(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            boolean deleted = beanService.deleteBean(id, username);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<BeanDisplayDTO>> searchBeans(
            @RequestParam(required = false) String flavor,
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String roast) {
        try {
            List<BeanDisplayDTO> beans = beanService.searchBeans(flavor, origin, roast);
            return ResponseEntity.ok(beans);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<BeanDisplayDTO>> getMyBeans() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            List<BeanDisplayDTO> beans = beanService.getBeansByCreator(username);
            return ResponseEntity.ok(beans);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
