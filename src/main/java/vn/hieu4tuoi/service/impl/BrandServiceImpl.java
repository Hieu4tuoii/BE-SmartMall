package vn.hieu4tuoi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.hieu4tuoi.dto.request.category.BrandRequest;
import vn.hieu4tuoi.dto.respone.BrandResponse;
import vn.hieu4tuoi.dto.respone.PageResponse;
import vn.hieu4tuoi.exception.ResourceNotFoundException;
import vn.hieu4tuoi.mapper.BrandMapper;
import vn.hieu4tuoi.model.Brand;
import vn.hieu4tuoi.repository.BrandRepository;
import vn.hieu4tuoi.service.BrandService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    public String create(BrandRequest request) {
        Brand brand = brandMapper.toEntity(request);
        brand = brandRepository.save(brand);
        return brand.getId();
    }

    @Override
    public String update(String id, BrandRequest request) {
        Brand brand = brandRepository.findByIdAndIsDeleted(id, false);
        if (brand == null) {
            throw new ResourceNotFoundException("Không tìm thấy thương hiệu");
        }
        // brand.setName(request.getName());
        // brand.setSlug(request.getSlug());
        // brand.setImageUrl(request.getImageUrl());
        brand = brandMapper.toEntity(request);
        brand = brandRepository.save(brand);
        return brand.getId();
    }

    @Override
    public void delete(String id) {
        Brand brand = brandRepository.findByIdAndIsDeleted(id, false);
        if (brand == null) {
            throw new ResourceNotFoundException("Không tìm thấy thương hiệu");
        }
        brand.setIsDeleted(true);
        brandRepository.save(brand);
    }

    @Override
    public BrandResponse findById(String id) {
        Brand brand = brandRepository.findByIdAndIsDeleted(id, false);
        if (brand == null) {
            throw new ResourceNotFoundException("Không tìm thấy thương hiệu");
        }
        return brandMapper.toResponse(brand);
    }

    @Override
    public PageResponse<List<BrandResponse>> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Brand> brandPage = brandRepository.findAllByIsDeleted(false, pageable);
        List<BrandResponse> responses = brandPage.getContent()
                .stream()
                .map(brandMapper::toResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<List<BrandResponse>>builder()
                .pageNo(page)
                .pageSize(size)
                .totalPage(brandPage.getTotalPages())
                .items(responses)
                .build();
    }

    @Override
    public List<BrandResponse> findAllWithoutPagination() {
        List<Brand> brands = brandRepository.findAllByIsDeleted(false);
        return brands.stream()
                .map(brandMapper::toResponse)
                .collect(Collectors.toList());
    }
}

