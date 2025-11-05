package vn.hieu4tuoi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hieu4tuoi.dto.request.promotion.PromotionRequest;
import vn.hieu4tuoi.dto.respone.PromotionResponse;
import vn.hieu4tuoi.dto.respone.product.ProductAdminResponse;
import vn.hieu4tuoi.exception.ResourceNotFoundException;
import vn.hieu4tuoi.mapper.PromotionMapper;
import vn.hieu4tuoi.mapper.ProductMapper;
import vn.hieu4tuoi.model.Image;
import vn.hieu4tuoi.model.Product;
import vn.hieu4tuoi.model.ProductVersion;
import vn.hieu4tuoi.model.Promotion;
import vn.hieu4tuoi.repository.ImageRepository;
import vn.hieu4tuoi.repository.ProductRepository;
import vn.hieu4tuoi.repository.ProductVersionRepository;
import vn.hieu4tuoi.repository.PromotionRepository;
import vn.hieu4tuoi.service.PromotionService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    
    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;
    private final ProductRepository productRepository;
    private final ProductVersionRepository productVersionRepository;
    private final ProductMapper productMapper;
    private final ImageRepository imageRepository;
    
    @Override
    @Transactional
    public String create(PromotionRequest request) {
        Promotion promotion = promotionMapper.toEntity(request);
        Promotion savedPromotion = promotionRepository.save(promotion);
        
        // Áp dụng khuyến mãi cho các phiên bản sản phẩm được chọn
        if (request.getProductIds() != null && !request.getProductIds().isEmpty()) {
            List<ProductVersion> productVersions = productVersionRepository.findByIdInAndIsDeleted(request.getProductIds(), false);
            String promotionId = savedPromotion.getId();
            productVersions.forEach(pv -> pv.setPromotionId(promotionId));
            productVersionRepository.saveAll(productVersions);
        }
        
        return savedPromotion.getId();
    }
    
    @Override
    @Transactional
    public String update(String id, PromotionRequest request) {
        Promotion promotion = promotionRepository.findByIdAndIsDeleted(id, false);
        if (promotion == null) {
            throw new ResourceNotFoundException("Không tìm thấy chương trình giảm giá");
        }
    
        // --- Cập nhật thông tin chương trình ---
        promotion.setStartAt(request.getStartAt());
        promotion.setEndAt(request.getEndAt());
        promotion.setDiscount(request.getDiscount());
        promotion.setMaximumDiscountAmount(request.getMaximumDiscountAmount());
        promotionRepository.save(promotion);
    
        // --- 1. Xóa promotion_id cũ từ các product versions ---
        List<ProductVersion> oldVersions = productVersionRepository.findByPromotionIdAndIsDeleted(id, false);
        if (!oldVersions.isEmpty()) {
            oldVersions.forEach(pv -> pv.setPromotionId(null));
            productVersionRepository.saveAll(oldVersions);
        }
    
        // --- 2. Áp dụng cho product versions mới ---
        if (request.getProductIds() != null && !request.getProductIds().isEmpty()) {
            List<ProductVersion> newVersions = productVersionRepository.findByIdInAndIsDeleted(request.getProductIds(), false);
            newVersions.forEach(pv -> pv.setPromotionId(promotion.getId()));
            productVersionRepository.saveAll(newVersions);
        }
    
        return promotion.getId();
    }
    
    @Override
    @Transactional
    public void delete(String id) {
        Promotion promotion = promotionRepository.findByIdAndIsDeleted(id, false);
        if (promotion == null) {
            throw new ResourceNotFoundException("Không tìm thấy chương trình giảm giá");
        }
        
        // Xóa promotion_id của các product versions đang áp dụng promotion này
        List<ProductVersion> productVersions = productVersionRepository.findByPromotionIdAndIsDeleted(id, false);
        if (!productVersions.isEmpty()) {
            productVersions.forEach(pv -> pv.setPromotionId(null));
            productVersionRepository.saveAll(productVersions);
        }
        
        promotion.setIsDeleted(true);
        promotionRepository.save(promotion);
    }
    
    @Override
    public PromotionResponse findById(String id) {
        Promotion promotion = promotionRepository.findByIdAndIsDeleted(id, false);
        if (promotion == null) {
            throw new ResourceNotFoundException("Không tìm thấy chương trình giảm giá");
        }
        PromotionResponse response = promotionMapper.toResponse(promotion);
        
        // Lấy danh sách product versions đang áp dụng promotion này
        List<ProductVersion> productVersions = productVersionRepository.findByPromotionIdAndIsDeleted(id, false);
        if (!productVersions.isEmpty()) {
            // Lấy danh sách product IDs để load thông tin products và ảnh
            List<String> productIds = productVersions.stream()
                    .map(ProductVersion::getProductId)
                    .distinct()
                    .collect(Collectors.toList());
            
            // Load products để lấy thông tin
            List<Product> products = productRepository.findAllById(productIds);
            Map<String, Product> productMap = products.stream()
                    .collect(Collectors.toMap(Product::getId, p -> p));
            
            // Lấy danh sách ảnh default của các sản phẩm
            List<Image> imageList = imageRepository.findAllByIsDefaultAndProductIdInAndIsDeleted(true, productIds, false);
            Map<String, String> imageMap = imageList.stream()
                    .collect(Collectors.toMap(Image::getProductId, Image::getUrl));
            
            // Map ProductVersion thành ProductResponse (với tên là product name + version name)
            List<ProductAdminResponse> productResponses = productVersions.stream()
                    .map(pv -> {
                        Product product = productMap.get(pv.getProductId());
                        if (product != null) {
                            ProductAdminResponse productResponse = productMapper.entityToResponse(product);
                            productResponse.setId(pv.getId()); // Dùng ID của version
                            productResponse.setName(product.getName() + " - " + pv.getName()); // Ghép tên
                            productResponse.setImageUrl(imageMap.get(product.getId()));
                            return productResponse;
                        }
                        return null;
                    })
                    .filter(pr -> pr != null)
                    .collect(Collectors.toList());
            
            response.setProducts(productResponses);
        } else {
            response.setProducts(List.of());
        }
        
        return response;
    }
    
    @Override
    public List<PromotionResponse> findAll() {
        List<Promotion> promotions = promotionRepository.findAllByIsDeletedOrderByCreatedAtDesc(false);
        return promotions.stream()
                .map(promotionMapper::toResponse)
                .collect(Collectors.toList());
    }
}
