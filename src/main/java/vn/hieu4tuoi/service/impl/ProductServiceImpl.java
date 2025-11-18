package vn.hieu4tuoi.service.impl;

import static vn.hieu4tuoi.common.StringUtils.toFullTextSearch;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import vn.hieu4tuoi.common.CommonUtils;
import vn.hieu4tuoi.common.ProductItemStatus;
import vn.hieu4tuoi.dto.request.product.ImageRequest;
import vn.hieu4tuoi.dto.request.product.ProductColorVersionRequest;
import vn.hieu4tuoi.dto.request.product.ProductCreateRequest;
import vn.hieu4tuoi.dto.request.product.ProductVersionRequest;
import vn.hieu4tuoi.dto.request.product.ProductVersionUpdateRequest;
import vn.hieu4tuoi.dto.respone.PageResponse;
import vn.hieu4tuoi.dto.respone.product.ImageResponse;
import vn.hieu4tuoi.dto.respone.product.ProductColorVersionResponse;
import vn.hieu4tuoi.dto.respone.product.ProductForUpdateResponse;
import vn.hieu4tuoi.dto.respone.product.ProductItemAdminResponse;
import vn.hieu4tuoi.dto.respone.product.ProductAdminResponse;
import vn.hieu4tuoi.dto.respone.product.ProductVersionAdminResponse;
import vn.hieu4tuoi.dto.respone.product.ProductVersionDetailResponse;
import vn.hieu4tuoi.dto.respone.product.ProductVersionNameResponse;
import vn.hieu4tuoi.dto.respone.product.ProductVersionResponse;
import vn.hieu4tuoi.exception.ResourceNotFoundException;
import vn.hieu4tuoi.mapper.ImageMapper;
import vn.hieu4tuoi.mapper.ProductItemMapper;
import vn.hieu4tuoi.mapper.BrandMapper;
import vn.hieu4tuoi.mapper.CategoryMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import vn.hieu4tuoi.mapper.ProductColorVersionMapper;
import vn.hieu4tuoi.mapper.ProductMapper;
import vn.hieu4tuoi.mapper.ProductVersionMapper;
import vn.hieu4tuoi.model.Brand;
import vn.hieu4tuoi.model.Category;
import vn.hieu4tuoi.model.Image;
import vn.hieu4tuoi.model.Product;
import vn.hieu4tuoi.model.ProductColorVersion;
import vn.hieu4tuoi.model.ProductItem;
import vn.hieu4tuoi.model.ProductVersion;
import vn.hieu4tuoi.model.Promotion;
import vn.hieu4tuoi.repository.BrandRepository;
import vn.hieu4tuoi.repository.CategoryRepository;
import vn.hieu4tuoi.repository.ImageRepository;
import vn.hieu4tuoi.repository.ProductColorVersionRepository;
import vn.hieu4tuoi.repository.ProductItemRepository;
import vn.hieu4tuoi.repository.ProductRepository;
import vn.hieu4tuoi.repository.ProductVersionRepository;
import vn.hieu4tuoi.repository.PromotionRepository;
import vn.hieu4tuoi.service.ProductService;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductVersionMapper productVersionMapper;
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final ProductVersionRepository productVersionRepository;
    private final ProductColorVersionMapper productColorVersionMapper;
    private final ProductColorVersionRepository productColorVersionRepository;
    private final ImageMapper imageMapper;
    private final ImageRepository imageRepository;
    private final PromotionRepository promotionRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final BrandMapper brandMapper;
    private final CategoryMapper categoryMapper;
    private final ProductItemRepository productItemRepository;
    private final ProductItemMapper productItemMapper;
    @Override
    @Transactional
    public String create(ProductCreateRequest request) {
        Product product = productMapper.createRequestToEntity(request);
        // product.setAverageRating(0.0);
        // product.setTotalRating(0);
        // product.setTotalSold(0L);
        // product.setTotalStock(0L);
        product = productRepository.save(product);
        // for (ProductVersionRequest item : request.getProductVersions()) {
        // ProductVersion productVersion = productVersionMapper.requestToEntity(item);
        // productVersion.setProductId(product.getId());
        // productVersionRepository.save(productVersion);
        // for (ProductColorVersionRequest colorVersionItem :
        // item.getProductColorVersions()) {
        // ProductColorVersion productColorVersion =
        // productColorVersionMapper.requestToEntity(colorVersionItem);
        // productColorVersion.setProductVersionId(productVersion.getId());
        // productColorVersionRepository.save(productColorVersion);
        // }
        // }

        // Kiểm tra xem có ảnh nào là default không
        boolean hasDefault = request.getImageList().stream()
                .anyMatch(img -> img.getIsDefault() != null && img.getIsDefault());

        for (int i = 0; i < request.getImageList().size(); i++) {
            ImageRequest item = request.getImageList().get(i);
            Image image = imageMapper.requestToEntity(item);
            image.setProductId(product.getId());

            // Nếu không có ảnh nào là default, set ảnh đầu tiên làm default
            if (!hasDefault && i == 0) {
                image.setIsDefault(true);
            } else if (image.getIsDefault() == null) {
                image.setIsDefault(false);
            }

            imageRepository.save(image);
        }
        return product.getId();
    }

    @Override
    @Transactional
    public void update(String id, ProductCreateRequest request) {
        Product product = productRepository.findByIdAndIsDeleted(id, false);
        if (product == null) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm");
        }
        product.setName(request.getName());
        product.setModel(request.getModel());
        product.setWarrantyPeriod(request.getWarrantyPeriod());
        product.setDescription(request.getDescription());
        product.setSpecifications(request.getSpecifications());
        // product.setStatus(request.getStatus());
        product.setBrandId(request.getBrandId());
        product.setCategoryId(request.getCategoryId());
        product = productRepository.save(product);

        // list ảnh ban đầu
        List<Image> imageList = imageRepository.findAllByProductIdAndIsDeleted(product.getId(), false);
        // xóa list cũ sau đó add list mới
        imageRepository.deleteAll(imageList);

        // Kiểm tra xem có ảnh nào là default không
        boolean hasDefault = request.getImageList().stream()
                .anyMatch(img -> img.getIsDefault() != null && img.getIsDefault());

        for (int i = 0; i < request.getImageList().size(); i++) {
            ImageRequest item = request.getImageList().get(i);
            Image image = imageMapper.requestToEntity(item);
            image.setProductId(product.getId());

            // Nếu không có ảnh nào là default, set ảnh đầu tiên làm default
            if (!hasDefault && i == 0) {
                image.setIsDefault(true);
            } else if (image.getIsDefault() == null) {
                image.setIsDefault(false);
            }

            imageRepository.save(image);
        }
    }

    @Override
    public PageResponse<List<ProductAdminResponse>> findAllInAdmin(int page, int size, String sort, String keyword) {
        // Xử lý sắp xếp
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "modifiedAt"); // Mặc định sắp xếp theo modifiedAt desc
        if (StringUtils.hasLength(sort)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                order = matcher.group(3).equalsIgnoreCase("asc")
                        ? new Sort.Order(Sort.Direction.ASC, columnName)
                        : new Sort.Order(Sort.Direction.DESC, columnName);
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(order));

        // Tìm kiếm theo keyword hoặc lấy tất cả
        if (StringUtils.hasLength(keyword)) {
            keyword = "%" + keyword.toLowerCase() + "%";
        } else {
            keyword = "%%";
        }

        Page<Product> productPage = productRepository.searchProductByKeyword(keyword, pageable);

        // lấy ds ảnh default của các sản phẩm
        List<String> productIds = productPage.getContent()
                .stream()
                .map(Product::getId)
                .toList();
        List<Image> imageList = imageRepository.findAllByIsDefaultAndProductIdInAndIsDeleted(true, productIds, false);
        Map<String, String> imageMap = imageList.stream()
                .collect(Collectors.toMap(Image::getProductId, Image::getUrl));
        List<ProductAdminResponse> productList = productPage.getContent()
                .stream()
                .map(product -> {
                    ProductAdminResponse productResponse = productMapper.entityToResponse(product);
                    productResponse.setImageUrl(imageMap.get(product.getId()));
                    return productResponse;
                })
                .toList();
        return PageResponse.<List<ProductAdminResponse>>builder()
                .pageNo(page)
                .pageSize(size)
                .totalPage(productPage.getTotalPages())
                .items(productList)
                .build();
    }

    // note: cần xóa các bảng con liên kết
    @Override
    public void delete(String id) {
        Product product = productRepository.findByIdAndIsDeleted(id, false);
        if (product == null) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm");
        }
        product.setIsDeleted(true);
        productRepository.save(product);
    }

    @Override
    public String createVersion(ProductVersionRequest request) {
        Product product = productRepository.findByIdAndIsDeleted(request.getProductId(), false);
        if (product == null) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm");
        }
        ProductVersion productVersion = productVersionMapper.requestToEntity(request);
        productVersion.setAverageRating(0.0);
        productVersion.setTotalRating(0);
        productVersion.setPrice(request.getPrice());
        productVersion.setSlug(request.getSlug());
        // fullTextSearch theo utils toFullTextSearch
        productVersion.setFullTextSearch(toFullTextSearch(((product.getName() != null ? product.getName() : "") + " "
                + (request.getName() != null ? request.getName() : "")).trim()));
        productVersion = productVersionRepository.save(productVersion);
        return productVersion.getId();
    }

    @Override
    public void updateVersion(String id, ProductVersionUpdateRequest request) {
        ProductVersion productVersion = productVersionRepository.findByIdAndIsDeleted(id, false);
        if (productVersion == null) {
            throw new ResourceNotFoundException("Không tìm thấy phiên bản sản phẩm");
        }
        Product product = productRepository.findByIdAndIsDeleted(productVersion.getProductId(), false);
        if (product == null) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm");
        }
        productVersion.setName(request.getName());
        productVersion.setPrice(request.getPrice());
        productVersion.setSlug(request.getSlug());
        // cập nhật fullTextSearch theo utils toFullTextSearch
        productVersion.setFullTextSearch(toFullTextSearch(((product.getName() != null ? product.getName() : "") + " "
                + (request.getName() != null ? request.getName() : "")).trim()));
        // productVersion.setDetailedPecifications(request.getDetailedPecifications());
        productVersionRepository.save(productVersion);
    }

    @Override
    public void deleteVersion(String id) {
        ProductVersion productVersion = productVersionRepository.findByIdAndIsDeleted(id, false);
        if (productVersion == null) {
            throw new ResourceNotFoundException("Không tìm thấy phiên bản sản phẩm");
        }
        productVersion.setIsDeleted(true);
        productVersionRepository.save(productVersion);
    }

    @Override
    public String createColorVersion(ProductColorVersionRequest request) {
        ProductVersion productVersion = productVersionRepository.findByIdAndIsDeleted(request.getProductVersionId(),
                false);
        if (productVersion == null) {
            throw new ResourceNotFoundException("Không tìm thấy phiên bản sản phẩm");
        }
        ProductColorVersion productColorVersion = productColorVersionMapper.requestToEntity(request);
        productColorVersion = productColorVersionRepository.save(productColorVersion);
        return productColorVersion.getId();
    }

    @Override
    public void updateColorVersion(String id, ProductColorVersionRequest request) {
        ProductColorVersion productColorVersion = productColorVersionRepository.findByIdAndIsDeleted(id, false);
        if (productColorVersion == null) {
            throw new ResourceNotFoundException("Không tìm thấy phiên bản sản phẩm");
        }
        productColorVersion.setColor(request.getColor());
        // productColorVersion.setImage(request.getImage());
        productColorVersion.setSku(request.getSku());
        productColorVersionRepository.save(productColorVersion);
    }

    @Override
    public void deleteColorVersion(String id) {
        ProductColorVersion productColorVersion = productColorVersionRepository.findByIdAndIsDeleted(id, false);
        if (productColorVersion == null) {
            throw new ResourceNotFoundException("Không tìm thấy phiên bản sản phẩm");
        }
        productColorVersion.setIsDeleted(true);
        productColorVersionRepository.save(productColorVersion);
    }

    @Override
    public List<ProductVersionAdminResponse> getVersionsByProductId(String productId) {
        List<ProductVersion> versions = productVersionRepository.findByProductIdAndIsDeletedOrderByCreatedAtAsc(productId, false);
        List<String> versionIds = versions.stream().map(ProductVersion::getId).toList();
        List<ProductColorVersion> colorVersions = productColorVersionRepository
                .findByProductVersionIdInAndIsDeletedOrderByCreatedAtAsc(versionIds, false);
        return versions.stream().map(version -> {
            ProductVersionAdminResponse response = productVersionMapper.entityToResponse(version);
            List<ProductColorVersionResponse> colorResponses = colorVersions.stream()
                    .filter(colorVersion -> colorVersion.getProductVersionId().equals(version.getId()))
                    .map(productColorVersionMapper::entityToResponse)
                    .collect(Collectors.toList());
            response.setColorVersions(colorResponses);
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public ProductForUpdateResponse findById(String id) {
        Product product = productRepository.findByIdAndIsDeleted(id, false);
        if (product == null) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm");
        }
        ProductForUpdateResponse productForUpdateResponse = productMapper.entityToUpdateResponse(product);
        // set image list
        List<Image> imageList = imageRepository.findAllByProductIdAndIsDeleted(product.getId(), false);
        List<ImageResponse> imageResponses = imageList.stream()
                .map(imageMapper::entityToResponse)
                .toList();
        productForUpdateResponse.setImageList(imageResponses);
        return productForUpdateResponse;
    }

    @Override
    public List<ProductVersionAdminResponse> getAllVersions() {
        // Lấy tất cả products không bị xóa
        List<Product> products = productRepository.findAllByIsDeleted(false);
        List<String> productIds = products.stream().map(Product::getId).collect(Collectors.toList());

        // Lấy tất cả versions của các products này
        List<ProductVersion> allVersions = productVersionRepository.findAll().stream()
                .filter(v -> !v.getIsDeleted() && productIds.contains(v.getProductId()))
                .collect(Collectors.toList());

        // Map product ID -> Product để lấy thông tin
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // Map thành ProductVersionAdminResponse với tên đầy đủ
        return allVersions.stream().map(version -> {
            ProductVersionAdminResponse response = productVersionMapper.entityToResponse(version);
            Product product = productMap.get(version.getProductId());
            if (product != null) {
                // Ghép tên product và version
                response.setName(product.getName() + " " + version.getName());
            }
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public PageResponse<List<ProductVersionResponse>> searchPublicProductVersion(List<String> brandIds,
            List<String> categoryIds, Boolean hasPromotion, Long minPrice, Long maxPrice, String keyword, int page, int size, String sort) {

        // Xử lý sắp xếp
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "modifiedAt"); // Mặc định sắp xếp theo modifiedAt desc
        if (StringUtils.hasLength(sort)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                order = matcher.group(3).equalsIgnoreCase("asc")
                        ? new Sort.Order(Sort.Direction.ASC, columnName)
                        : new Sort.Order(Sort.Direction.DESC, columnName);
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(order));

        // Tìm kiếm theo keyword hoặc lấy tất cả
        if (StringUtils.hasLength(keyword)) {
            keyword = "%" + keyword.toLowerCase().trim() + "%";
        } else {
            keyword = "%%";
        }
        
        //nếu hasPromotion là true thì lấy ds product version có promotion còn hiệu lực
        boolean filterByPromotion = Boolean.TRUE.equals(hasPromotion);
        LocalDateTime now = filterByPromotion ? LocalDateTime.now() : null;
        Page<ProductVersion> productVersionPage = productVersionRepository.searchProductVersion(now, brandIds,
                categoryIds, minPrice, maxPrice, keyword, pageable);

        //làm giàu thông tin cho product version response (image, product name, promotion)
        List<ProductVersionResponse> productVersionResponses = enrichProductVersionResponses(productVersionPage.getContent());

        return PageResponse.<List<ProductVersionResponse>>builder()
                .pageNo(page)
                .pageSize(size)
                .totalPage(productVersionPage.getTotalPages())
                .items(productVersionResponses)
                .build();
    }

    //lấy ds product version liên quan theo product version id
    @Override
    public List<ProductVersionResponse> getRelatedProductVersions(String productVersionSlug) {
        //lấy product version theo slug
        ProductVersion productVersion = productVersionRepository.findBySlugAndIsDeleted(productVersionSlug, false);
        if (productVersion == null) {
            throw new ResourceNotFoundException("Không tìm thấy phiên bản sản phẩm");
        }

        //lấy 5 product version có cùng brandIds và categoryIds với product version đó, đồng thời có giá chênh lệch không quá 30%
        //lấy brandId và categoryId từ product
        Product product = productRepository.findByIdAndIsDeleted(productVersion.getProductId(), false);
        if (product == null) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm");
        }
        
        String brandId = product.getBrandId();
        String categoryId = product.getCategoryId();

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "totalSold"));
        Page<ProductVersion> productVersionPage = productVersionRepository.findByBrandIdAndCategoryIdAndPriceLessThanEqualAndPriceGreaterThanEqual(
                brandId, categoryId, (long) (productVersion.getPrice() * 0.7), (long) (productVersion.getPrice() * 1.3), productVersion.getId(), pageable);

        //làm giàu thông tin cho product version response (image, product name, promotion)
        return enrichProductVersionResponses(productVersionPage.getContent());
    }


    @Override
    public ProductVersionDetailResponse findVersionDetailBySlug(String slug) {
        ProductVersion productVersion = productVersionRepository.findBySlugAndIsDeleted(slug, false);
        if (productVersion == null) {
            throw new ResourceNotFoundException("Không tìm thấy phiên bản sản phẩm");
        }

       //mapepr
       ProductVersionDetailResponse response = productVersionMapper.entityToDetailResponse(productVersion);

        //set list image của product sang cho response, sắp xếp ảnh mặc định lên đầu danh sách
        List<Image> imageList = imageRepository.findAllByProductIdAndIsDeleted(productVersion.getProductId(), false);
        List<String> imageUrls = imageList.stream()
                .sorted(Comparator.comparing(Image::getIsDefault).reversed()) //sắp xếp ảnh mặc định lên đầu danh sách
                .map(Image::getUrl)
                .toList();
        response.setImageUrls(imageUrls);

        //set name của product 
        Product product = productRepository.findByIdAndIsDeleted(productVersion.getProductId(), false);
        if (product != null) {
            response.setName(product.getName() + " " + productVersion.getName());
            response.setSpecifications(product.getSpecifications());
            response.setModel(product.getModel());
            response.setWarrantyPeriod(product.getWarrantyPeriod());
            response.setDescription(product.getDescription());
            
            //set brand và category 
            Brand brand = brandRepository.findByIdAndIsDeleted(product.getBrandId(), false);
            if (brand != null) {
                response.setBrand(brandMapper.toResponse(brand));
            }
            Category category = categoryRepository.findByIdAndIsDeleted(product.getCategoryId(), false);
            if (category != null) {
                response.setCategory(categoryMapper.toResponse(category));
            }
        }

        //set ds version name 
        List<ProductVersion> productVersions = productVersionRepository.findByProductIdAndIsDeletedOrderByCreatedAtAsc(productVersion.getProductId(), false);
        List<ProductVersionNameResponse> productVersionNames = productVersions.stream().map(
            versionNameItem -> {
                ProductVersionNameResponse productVersionName = new ProductVersionNameResponse();
                productVersionName.setId(versionNameItem.getId());
                productVersionName.setName(versionNameItem.getName());
                productVersionName.setSlug(versionNameItem.getSlug());
                return productVersionName;
            })
            .toList();
        response.setProductVersionNames(productVersionNames);

        //set ds color version
        List<ProductColorVersionResponse> productColorVersions = productColorVersionRepository.findByProductVersionIdAndIsDeletedOrderByCreatedAtAsc(productVersion.getId(), false).stream().map(
            colorVersionItem -> {
                ProductColorVersionResponse colorVersionResponse = new ProductColorVersionResponse();
                colorVersionResponse.setId(colorVersionItem.getId());
                colorVersionResponse.setColor(colorVersionItem.getColor());
                colorVersionResponse.setSku(colorVersionItem.getSku());
                colorVersionResponse.setProductVersionId(productVersion.getId());
                colorVersionResponse.setTotalStock(colorVersionItem.getTotalStock());
                colorVersionResponse.setTotalSold(colorVersionItem.getTotalSold());
                colorVersionResponse.setCreatedAt(colorVersionItem.getCreatedAt());
                colorVersionResponse.setModifiedAt(colorVersionItem.getModifiedAt());
                return colorVersionResponse;
            })
            .toList();
        response.setProductColorVersions(productColorVersions);

        //set promotion 
        Promotion promotion = promotionRepository.findByIdAndIsDeleted(productVersion.getPromotionId(), false);
        if (promotion != null) {
            // % discount
            double discountPercent = promotion.getDiscount();
            //giá dc giảm
            double discountAmount = productVersion.getPrice() * discountPercent / 100;

            // double discountedPrice = productVersion.getPrice() - discountAmount;
            // nếu lớn hơn max discount thì cần set lại và tính lại % discount
            if (discountAmount > promotion.getMaximumDiscountAmount()) {
                discountAmount = promotion.getMaximumDiscountAmount();
                discountPercent = discountAmount * 100 / productVersion.getPrice();
            }
            response.setDiscount(Math.round(discountPercent));
            // Làm tròn đến nghìn đồng
            long discountedPrice = (long) Math.round((productVersion.getPrice() - discountAmount) / 1000.0) * 1000;
            response.setDiscountedPrice(discountedPrice);
        }
        return response;
    }

    /**
     * Làm giàu thông tin cho danh sách ProductVersionResponse
     * Bao gồm: image url default, tên đầy đủ (product name + version name), và promotion
     * 
     * @param productVersions Danh sách ProductVersion cần xử lý
     * @return Danh sách ProductVersionResponse đã được làm giàu thông tin
     */
    public  List<ProductVersionResponse> enrichProductVersionResponses(List<ProductVersion> productVersions) {
        if (productVersions == null || productVersions.isEmpty()) {
            return List.of();
        }

        //list productId
        List<String> productIds = productVersions.stream()
                .map(ProductVersion::getProductId)
                .toList();
        
        //list image url default
        List<Image> imageList = imageRepository.findAllByIsDefaultAndProductIdInAndIsDeleted(true, productIds, false);
        Map<String, String> imageMap = imageList.stream()
                .collect(Collectors.toMap(Image::getProductId, Image::getUrl));

        //list product để lấy tên đầy đủ
        List<Product> products = productRepository.findAllByIdInAndIsDeleted(productIds, false);
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        //list promotion id
        List<String> promotionIds = productVersions.stream()
                .map(ProductVersion::getPromotionId)
                .toList();
        
        //list promotion còn hiệu lực
        List<Promotion> promotions = promotionRepository.findAllByIdInAndStartAtLessThanEqualAndEndAtGreaterThanEqual(
                promotionIds, LocalDateTime.now(), false);
        Map<String, Promotion> promotionMap = promotions.stream()
                .collect(Collectors.toMap(Promotion::getId, p -> p));

        //set image url và promotion cho product version response
        return productVersions.stream().map(productVersion -> {
            ProductVersionResponse response = productVersionMapper.entityToPublicResponse(productVersion);
            response.setImageUrl(imageMap.get(productVersion.getProductId()));
            
            Promotion promotion = promotionMap.get(productVersion.getPromotionId());
            applyPromotionToProductVersionResponse(productVersion, response, promotion);
            
            Product product = productMap.get(productVersion.getProductId());
            if (product != null) {
                response.setName(product.getName() + " " + productVersion.getName());
            }
            return response;
        }).toList();
    }

    private void applyPromotionToProductVersionResponse(ProductVersion productVersion, ProductVersionResponse response, Promotion promotion) {
        if (promotion != null) {
            // % discount
            double discountPercent = promotion.getDiscount();
            //giá dc giảm
            double discountAmount = productVersion.getPrice() * discountPercent / 100;

            // double discountedPrice = productVersion.getPrice() - discountAmount;
            // nếu lớn hơn max discount thì cần set lại và tính lại % discount
            if (discountAmount > promotion.getMaximumDiscountAmount()) {
                discountAmount = promotion.getMaximumDiscountAmount();
                discountPercent = discountAmount * 100 / productVersion.getPrice();
            }
            response.setDiscount(Math.round(discountPercent));
            // Làm tròn đến nghìn đồng
            long discountedPrice = (long) Math.round((productVersion.getPrice() - discountAmount) / 1000.0) * 1000;
            response.setDiscountedPrice(discountedPrice);
        }
    }


    //ds product item của sản product version color
    @Override
    public PageResponse<List<ProductItemAdminResponse>> getProductItemsByProductVersionColorId(String productVersionColorId, ProductItemStatus status, int page, int size, String sort, String imeiOrSerial) {
        Pageable pageable = CommonUtils.createPageable(page, size, sort);
        String imeiOrSerialSearch = CommonUtils.createKeywordSearch(imeiOrSerial);
        Page<ProductItem> productItems = productItemRepository.findByProductColorVersionIdAndStatusAndImeiOrSerial(productVersionColorId, status, imeiOrSerialSearch, false, pageable);
        return PageResponse.<List<ProductItemAdminResponse>>builder()
                .pageNo(page)
                .pageSize(size)
                .totalPage(productItems.getTotalPages())
                .items(productItems.getContent().stream().map(productItemMapper::entityToResponse).toList())
                .build();
    }

    
}
