package vn.hieu4tuoi.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.hieu4tuoi.dto.request.hybrid.HybridRagProductRequest;
import vn.hieu4tuoi.dto.request.hybrid.HybridRagSearchRequest;
import vn.hieu4tuoi.dto.respone.hybrid.HybridRagSearchResponse;
import vn.hieu4tuoi.model.Product;
import vn.hieu4tuoi.model.ProductVersion;
import vn.hieu4tuoi.service.HybridRagService;

@Slf4j
@Service
@RequiredArgsConstructor
public class HybridRagServiceImpl implements HybridRagService {

    private static final String ADD_PRODUCT_PATH = "/add_product";
    private static final String SEARCH_PRODUCTS_PATH = "/search_products";

    @Value("${rag.api.base-url}")
    private String ragBaseUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void syncProductVersion(Product product, ProductVersion productVersion) {
        HybridRagProductRequest requestPayload = HybridRagProductRequest.builder()
                .id(productVersion.getId())
                .name(buildDisplayName(product, productVersion))
                .description(buildDescription(product))
                .metadata(buildMetadata(product, productVersion))
                .build();

        try {
            restTemplate.postForEntity(ragBaseUrl + ADD_PRODUCT_PATH, requestPayload, Void.class);
        } catch (Exception ex) {
            log.error("Không thể đồng bộ phiên bản sản phẩm {} tới Hybrid RAG: {}", productVersion.getId(),
                    ex.getMessage(), ex);
        }
    }

    @Override
    public List<HybridRagSearchResponse> searchProducts(HybridRagSearchRequest request) {
        if (request == null || !StringUtils.hasText(request.getQuery())) {
            log.warn("Query không được để trống");
            return new ArrayList<>();
        }

        try {
            // Xây dựng request body cho API Hybrid RAG
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("query", request.getQuery());

            // Xây dựng filters map nếu có các filter
            Map<String, Object> filters = buildFilters(request);
            if (!filters.isEmpty()) {
                requestBody.put("filters", filters);
            }

            // Gọi API Hybrid RAG
            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody);
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    ragBaseUrl + SEARCH_PRODUCTS_PATH,
                    HttpMethod.POST,
                    httpEntity,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    });

            if (response.getBody() != null) {
                return mapToSearchResponse(response.getBody());
            }
            return new ArrayList<>();
        } catch (Exception ex) {
            log.error("Lỗi khi tìm kiếm sản phẩm trong Hybrid RAG: {}", ex.getMessage(), ex);
            return new ArrayList<>();
        }
    }

    /**
     * Xây dựng filters map từ các tham số tùy chọn
     */
    private Map<String, Object> buildFilters(HybridRagSearchRequest request) {
        Map<String, Object> filters = new HashMap<>();

        // Filter theo brand_id
        if (StringUtils.hasText(request.getBrandId())) {
            filters.put("metadata.brand_id", request.getBrandId());
        }

        // Filter theo category_id
        if (StringUtils.hasText(request.getCategoryId())) {
            filters.put("metadata.category_id", request.getCategoryId());
        }

        // Filter theo khoảng giá
        Map<String, Object> priceRange = new HashMap<>();
        boolean hasPriceFilter = false;

        if (request.getMinPrice() != null && request.getMinPrice() > 0) {
            priceRange.put("gte", request.getMinPrice());
            hasPriceFilter = true;
        }

        if (request.getMaxPrice() != null && request.getMaxPrice() > 0) {
            priceRange.put("lte", request.getMaxPrice());
            hasPriceFilter = true;
        }

        if (hasPriceFilter) {
            filters.put("metadata.price", priceRange);
        }

        return filters;
    }

    /**
     * Map response từ Hybrid RAG API sang DTO response
     */
    @SuppressWarnings("unchecked")
    private List<HybridRagSearchResponse> mapToSearchResponse(List<Map<String, Object>> rawResponse) {
        return rawResponse.stream().map(item -> {
            HybridRagSearchResponse response = new HybridRagSearchResponse();
            response.setName((String) item.get("name"));
            response.setDescription((String) item.get("description"));
            response.setMetadata((Map<String, Object>) item.get("metadata"));

            // Xử lý score có thể là Double hoặc Number
            Object scoreObj = item.get("score");
            if (scoreObj != null) {
                response.setScore((Double) scoreObj);
            }

            return response;
        }).collect(Collectors.toList());
    }

    private String buildDisplayName(Product product, ProductVersion version) {
        String productName = product.getName() != null ? product.getName().trim() : "";
        String versionName = version.getName() != null ? version.getName().trim() : "";
        return (productName + " " + versionName).trim();
    }

    // xây dựng description từ description và specifications
    private String buildDescription(Product product) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.hasText(product.getDescription())) {
            builder.append(sanitizeText(product.getDescription()));
        }
        String formattedSpecs = formatSpecifications(product.getSpecifications());
        if (StringUtils.hasText(formattedSpecs)) {
            if (builder.length() > 0) {
                builder.append(System.lineSeparator());
            }
            builder.append(formattedSpecs);
        }
        return builder.length() > 0 ? builder.toString().replaceAll("\r\n", ". ").trim() : product.getName();
    }

    private Map<String, Object> buildMetadata(Product product, ProductVersion version) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("brand_id", product.getBrandId());
        metadata.put("category_id", product.getCategoryId());
        metadata.put("price", version.getPrice());
        metadata.entrySet().removeIf(entry -> entry.getValue() == null);
        return metadata;
    }

    // làm sạch và format specifications
    private String formatSpecifications(String rawSpecifications) {
        if (!StringUtils.hasText(rawSpecifications)) {
            return "";
        }
        try {
            Map<String, Object> specs = objectMapper.readValue(rawSpecifications,
                    new TypeReference<Map<String, Object>>() {
                    });
            return specs.entrySet().stream()
                    .map(entry -> sanitizeText(entry.getKey()) + " " + sanitizeText(String.valueOf(entry.getValue())))
                    .collect(Collectors.joining(". "));
        } catch (Exception ex) {
            return stripSpecialCharacters(rawSpecifications);
        }
    }

    private String stripSpecialCharacters(String text) {
        if (text == null) {
            return "";
        }
        return text.replaceAll("[\\[\\]{}\"]", " ")
                .replace(":", " ")
                .replaceAll(",", ", ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String sanitizeText(String text) {
        if (text == null) {
            return "";
        }
        return text.replaceAll("[\\[\\]{}\"]", " ").replaceAll("\\s+", " ").trim();
    }
}
