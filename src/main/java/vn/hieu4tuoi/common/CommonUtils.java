package vn.hieu4tuoi.common;

import static vn.hieu4tuoi.common.StringUtils.toFullTextSearch;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

/**
 * Utility class để xử lý các chức năng dùng chung
 */
public final class CommonUtils {

    private CommonUtils() {
        // Utility class - không cho phép khởi tạo
    }

    /**
     * Tạo Pageable từ các tham số phân trang và sắp xếp
     * Mặc định sắp xếp theo modifiedAt desc nếu sort string rỗng hoặc không hợp lệ
     * Format sort string: "columnName:asc" hoặc "columnName:desc"
     * 
     * @param page Số trang (bắt đầu từ 0)
     * @param size Số lượng phần tử trên mỗi trang
     * @param sort Chuỗi sort theo format "columnName:direction" (ví dụ: "createdAt:asc", "price:desc")
     * @return Pageable object
     */
    public static Pageable createPageable(int page, int size, String sort) {
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "modifiedAt"); // Mặc định sắp xếp theo modifiedAt desc
        if (StringUtils.hasLength(sort)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                String direction = matcher.group(3);
                if (columnName != null && direction != null) {
                    order = direction.equalsIgnoreCase("asc")
                            ? new Sort.Order(Sort.Direction.ASC, columnName)
                            : new Sort.Order(Sort.Direction.DESC, columnName);
                }
            }
        }
        return PageRequest.of(page, size, Sort.by(order));
    }


    //hàm tạo keyword cho full text search
    public static String createKeywordSearch(String keyword) {
        if (StringUtils.hasLength(keyword)) {
            return "%" + toFullTextSearch(keyword) + "%";
        } else {
            return "%%";
        }
    }
}
