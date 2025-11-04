package vn.hieu4tuoi.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import vn.hieu4tuoi.dto.request.product.ImageRequest;
import vn.hieu4tuoi.dto.request.product.ProductColorVersionRequest;
import vn.hieu4tuoi.dto.request.product.ProductCreateRequest;
import vn.hieu4tuoi.dto.request.product.ProductVersionRequest;
import vn.hieu4tuoi.dto.request.product.ProductVersionUpdateRequest;
import vn.hieu4tuoi.dto.respone.PageResponse;
import vn.hieu4tuoi.dto.respone.product.ImageResponse;
import vn.hieu4tuoi.dto.respone.product.ProductColorVersionResponse;
import vn.hieu4tuoi.dto.respone.product.ProductForUpdateResponse;
import vn.hieu4tuoi.dto.respone.product.ProductResponse;
import vn.hieu4tuoi.dto.respone.product.ProductVersionResponse;
import vn.hieu4tuoi.exception.ResourceNotFoundException;
import vn.hieu4tuoi.mapper.ImageMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import vn.hieu4tuoi.mapper.ProductColorVersionMapper;
import vn.hieu4tuoi.mapper.ProductMapper;
import vn.hieu4tuoi.mapper.ProductVersionMapper;
import vn.hieu4tuoi.model.Image;
import vn.hieu4tuoi.model.Product;
import vn.hieu4tuoi.model.ProductColorVersion;
import vn.hieu4tuoi.model.ProductVersion;
import vn.hieu4tuoi.repository.ImageRepository;
import vn.hieu4tuoi.repository.ProductColorVersionRepository;
import vn.hieu4tuoi.repository.ProductRepository;
import vn.hieu4tuoi.repository.ProductVersionRepository;
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

    @Override
    @Transactional
    public String create(ProductCreateRequest request) {
        Product product = productMapper.createRequestToEntity(request);
        product.setAverageRating(0.0);
        product.setTotalRating(0);
        product.setTotalSold(0L);
        product.setTotalStock(0L);
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
        product.setSlug(request.getSlug());
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
    public PageResponse<List<ProductResponse>> findAllInAdmin(int page, int size, String sort, String keyword) {
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

        //lấy ds ảnh default của các sản phẩm
        List<String> productIds = productPage.getContent()
                .stream()
                .map(Product::getId)
                .toList();
        List<Image> imageList = imageRepository.findAllByIsDefaultAndProductIdInAndIsDeleted(true, productIds, false);
        Map<String, String> imageMap = imageList.stream()
                .collect(Collectors.toMap(Image::getProductId, Image::getUrl));
        List<ProductResponse> productList = productPage.getContent()
                .stream()
                .map( product -> {
                    ProductResponse productResponse = productMapper.entityToResponse(product);
                    productResponse.setImageUrl(imageMap.get(product.getId()));
                    return productResponse;
                })
                .toList();
        return PageResponse.<List<ProductResponse>>builder()
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
        productVersion = productVersionRepository.save(productVersion);
        return productVersion.getId();
    }

    @Override
    public void updateVersion(String id, ProductVersionUpdateRequest request) {
        ProductVersion productVersion = productVersionRepository.findByIdAndIsDeleted(id, false);
        if (productVersion == null) {
            throw new ResourceNotFoundException("Không tìm thấy phiên bản sản phẩm");
        }
        productVersion.setName(request.getName());
//        productVersion.setDetailedPecifications(request.getDetailedPecifications());
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
        ProductVersion productVersion = productVersionRepository.findByIdAndIsDeleted(request.getProductVersionId(), false);
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
//        productColorVersion.setImage(request.getImage());
        productColorVersion.setPrice(request.getPrice());
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
    public List<ProductVersionResponse> getVersionsByProductId(String productId) {
        List<ProductVersion> versions = productVersionRepository.findByProductIdAndIsDeleted(productId, false);
        List<String> versionIds = versions.stream().map(ProductVersion::getId).toList();
        List<ProductColorVersion> colorVersions = productColorVersionRepository.findByProductVersionIdInAndIsDeleted(versionIds, false);
        return versions.stream().map(version -> {
            ProductVersionResponse response = productVersionMapper.entityToResponse(version);
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
        //set image list
        List<Image> imageList = imageRepository.findAllByProductIdAndIsDeleted(product.getId(), false);
        List<ImageResponse> imageResponses = imageList.stream()
                .map(imageMapper::entityToResponse)
                .toList();
        productForUpdateResponse.setImageList(imageResponses);
        return productForUpdateResponse;
    }

    @Override
    public List<ProductVersionResponse> getAllVersions() {
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
        
        // Map thành ProductVersionResponse với tên đầy đủ
        return allVersions.stream().map(version -> {
            ProductVersionResponse response = productVersionMapper.entityToResponse(version);
            Product product = productMap.get(version.getProductId());
            if (product != null) {
                // Ghép tên product và version
                response.setName(product.getName() + " " + version.getName());
            }
            return response;
        }).collect(Collectors.toList());
    }
}
