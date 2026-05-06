package com.projects.coffee.service;

import com.projects.coffee.dto.BeanDTO;
import com.projects.coffee.dto.BeanDisplayDTO;
import com.projects.coffee.entity.Bean;
import com.projects.coffee.repository.BeanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BeanService {

    private final BeanRepository beanRepository;

    public BeanService(BeanRepository beanRepository) {
        this.beanRepository = beanRepository;
    }

    public List<BeanDisplayDTO> getAllBeans() {
        List<Bean> beans = beanRepository.findAll();
        return beans.stream()
                .map(bean -> new BeanDisplayDTO(
                        bean.getId(),
                        bean.getFlavor(),
                        bean.getOrigin(),
                        bean.getRoast(),
                        bean.getCreatedBy(),
                        bean.getIsPublic()
                ))
                .collect(Collectors.toList());
    }

    public BeanDisplayDTO getBeanById(Long id) {
        Optional<Bean> beanOpt = beanRepository.findById(id);
        if (beanOpt.isPresent()) {
            Bean bean = beanOpt.get();
            return new BeanDisplayDTO(
                    bean.getId(),
                    bean.getFlavor(),
                    bean.getOrigin(),
                    bean.getRoast(),
                    bean.getCreatedBy(),
                    bean.getIsPublic()
            );
        }
        return null;
    }

    public BeanDisplayDTO createBean(BeanDTO beanDTO, String createdBy) {
        Bean bean = new Bean();
        bean.setFlavor(beanDTO.getFlavor());
        bean.setOrigin(beanDTO.getOrigin());
        bean.setRoast(beanDTO.getRoast());
        bean.setIsPublic(beanDTO.getIsPublic());
        bean.setCreatedBy(createdBy);

        Bean savedBean = beanRepository.save(bean);

        return new BeanDisplayDTO(
                savedBean.getId(),
                savedBean.getFlavor(),
                savedBean.getOrigin(),
                savedBean.getRoast(),
                savedBean.getCreatedBy(),
                savedBean.getIsPublic()
        );
    }

    public BeanDisplayDTO updateBean(Long id, BeanDTO beanDTO, String username) {
        Optional<Bean> beanOpt = beanRepository.findById(id);
        if (beanOpt.isPresent()) {
            Bean bean = beanOpt.get();

            if (!bean.getCreatedBy().equals(username)) {
                throw new SecurityException("You can only update beans you created");
            }

            bean.setFlavor(beanDTO.getFlavor());
            bean.setOrigin(beanDTO.getOrigin());
            bean.setRoast(beanDTO.getRoast());
            bean.setIsPublic(beanDTO.getIsPublic());

            Bean savedBean = beanRepository.save(bean);

            return new BeanDisplayDTO(
                    savedBean.getId(),
                    savedBean.getFlavor(),
                    savedBean.getOrigin(),
                    savedBean.getRoast(),
                    savedBean.getCreatedBy(),
                    savedBean.getIsPublic()
            );
        }
        return null;
    }

    public boolean deleteBean(Long id, String username) {
        Optional<Bean> beanOpt = beanRepository.findById(id);
        if (beanOpt.isPresent()) {
            Bean bean = beanOpt.get();

            if (!bean.getCreatedBy().equals(username)) {
                throw new SecurityException("You can only delete beans you created");
            }

            beanRepository.delete(bean);
            return true;
        }
        return false;
    }

    public List<BeanDisplayDTO> searchBeans(String flavor, String origin, String roast) {
        List<Bean> beans = beanRepository.findByFlavorAndOriginAndRoast(flavor, origin, roast);
        return beans.stream()
                .map(bean -> new BeanDisplayDTO(
                        bean.getId(),
                        bean.getFlavor(),
                        bean.getOrigin(),
                        bean.getRoast(),
                        bean.getCreatedBy(),
                        bean.getIsPublic()
                ))
                .collect(Collectors.toList());
    }

    public List<BeanDisplayDTO> getBeansByCreator(String username) {
        List<Bean> beans = beanRepository.findByCreatedBy(username);
        return beans.stream()
                .map(bean -> new BeanDisplayDTO(
                        bean.getId(),
                        bean.getFlavor(),
                        bean.getOrigin(),
                        bean.getRoast(),
                        bean.getCreatedBy(),
                        bean.getIsPublic()
                ))
                .collect(Collectors.toList());
    }
}
