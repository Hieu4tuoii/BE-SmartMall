package vn.hieu4tuoi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.hieu4tuoi.dto.request.banner.BannerRequest;
import vn.hieu4tuoi.dto.respone.BannerResponse;
import vn.hieu4tuoi.exception.ResourceNotFoundException;
import vn.hieu4tuoi.mapper.BannerMapper;
import vn.hieu4tuoi.model.Banner;
import vn.hieu4tuoi.repository.BannerRepository;
import vn.hieu4tuoi.service.BannerService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {
    private final BannerRepository bannerRepository;
    private final BannerMapper bannerMapper;

    @Override
    public String create(BannerRequest request) {
        Banner banner = bannerMapper.toEntity(request);
        banner = bannerRepository.save(banner);
        return banner.getId();
    }

    @Override
    public String update(String id, BannerRequest request) {
        Banner banner = bannerRepository.findByIdAndIsDeleted(id, false);
        if (banner == null) {
            throw new ResourceNotFoundException("Không tìm thấy banner");
        }
        banner.setImageUrl(request.getImageUrl());
        banner.setLink(request.getLink());
        banner = bannerRepository.save(banner);
        return banner.getId();
    }

    @Override
    public void delete(String id) {
        Banner banner = bannerRepository.findByIdAndIsDeleted(id, false);
        if (banner == null) {
            throw new ResourceNotFoundException("Không tìm thấy banner");
        }
        banner.setIsDeleted(true);
        bannerRepository.save(banner);
    }

    @Override
    public List<BannerResponse> findAllWithoutPagination() {
        return bannerRepository.findAllByIsDeleted(false)
                .stream()
                .map(bannerMapper::toResponse)
                .collect(Collectors.toList());
    }
}


