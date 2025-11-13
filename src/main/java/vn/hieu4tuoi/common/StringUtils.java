package vn.hieu4tuoi.common;

import java.text.Normalizer;

public final class StringUtils {

    private StringUtils() {}

    // Bỏ dấu tiếng Việt, chuẩn hóa ký tự Đ/đ, sau đó loại bỏ dấu kết hợp
    public static String removeAccents(String value) {
        if (value == null) {
            return null;
        }
        value = value.replace("\u0110", "D").replace("\u0111", "d");
        String temp = Normalizer.normalize(value, Normalizer.Form.NFD);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }

    // Giữ lại chỉ ký tự chữ và số (loại bỏ khoảng trắng và ký tự đặc biệt)
    public static String keepAlphaNumeric(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("[^A-Za-z0-9]", "");
    }

    // Giữ lại ký tự chữ, số và xuống dòng (loại bỏ ký tự đặc biệt khác và khoảng trắng)
    public static String keepAlphaNumericNewLine(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("[^A-Za-z0-9\n]", "");
    }

    // Biến chuỗi thành dạng phục vụ full text search: lower, bỏ dấu, chỉ còn chữ/số và newline
    public static String toFullTextSearch(String value) {
        if (value == null) {
            return null;
        }
        return keepAlphaNumericNewLine(removeAccents(value.toLowerCase().trim()));
    }
}


