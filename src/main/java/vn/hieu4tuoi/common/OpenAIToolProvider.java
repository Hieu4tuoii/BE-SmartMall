package vn.hieu4tuoi.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.util.*;

@Component
public class OpenAIToolProvider {

    private List<Tool> toolList;
    private final ObjectMapper objectMapper;

    public OpenAIToolProvider() {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @PostConstruct
    public void initialize() {
        this.toolList = createToolList();
    }

    public List<Tool> getToolList() {
        return Collections.unmodifiableList(toolList);
    }

    public String getToolListAsJson() throws JsonProcessingException {
        return objectMapper.writeValueAsString(toolList);
    }

    private List<Tool> createToolList() {
        // Tạo tool 1: Get_All_Food
        // Map<String, Object> getAllFoodProperties = new HashMap<>();
        // getAllFoodProperties.put("get_all_food", new PropertySchema("boolean",
        // "true: lấy toàn bộ món ăn có trong menu của cửa hàng, false: không lấy món
        // ăn"));

        // FunctionDefinition getAllFoodFunction = new FunctionDefinition(
        // "Get_All_Product",
        // "Nếu cần thông tin từ danh sách sản phẩm của cửa hàng để đưa ra tư vấn, hãy
        // gọi hàm này",
        // null
        // );

        // tool 1: tạo query kèm câu filter truy vấn ( tham số sẽ là query ( bắt buộc ),
        // min_price ( tùy chọn ), max_price ( tùy chọn ), category_id ( tùy chọn ),
        // brand_id ( tùy chọn ))
        Map<String, Object> createQueryProperties = new HashMap<>();
        createQueryProperties.put("query", new PropertySchema("string",
                "Câu query dựa trên yêu cầu của khách hàng"));
        createQueryProperties.put("min_price", new PropertySchema("number",
                "Giá tối thiểu"));
        createQueryProperties.put("max_price", new PropertySchema("number",
                "Giá tối đa (cần có nếu như khách hàng chỉ đưa ra yêu cầu chung chung, không nhắm đến sản phẩm cụ thể)"));
        // createQueryProperties.put("brand_id", new PropertySchema("string",
        // "ID thương hiệu"));

        FunctionDefinition createQueryFunction = new FunctionDefinition(
                "Search_Product",
                "Nếu khách hàng chỉ đưa ra các nhu cầu chung, không nhắm đến sản phẩm cụ thể thì cần hỏi thêm về mức giá và nhu cầu sử dụng. Nếu khách hàng đã nhắm đến sản phẩm cụ thể thì gọi hàm này luôn. Sau đó gọi hàm này để lấy ra các sản phẩm phù hợp nhất với nhu cầu của khách hàng.",
                new ParameterSchema("object", createQueryProperties, Arrays.asList("query")));

        // tool 2: Tạo message tư vấn kèm danh sách product_version_id phù hợp nhất (
        // tham số: message ( bắt buộc ), mảng product_version_id ( tùy chọn ))
        Map<String, Object> createMessageProperties = new HashMap<>();
        createMessageProperties.put("message", new PropertySchema("string",
                "Tin nhắn tư vấn (Đây là tin nhắn sẽ được gửi trực tiếp cho khách hàng)"));
        createMessageProperties.put("productIds", new PropertySchema("array",
                "Danh sách ID sản phẩm phù hợp nhất",
                new NestedObjectSchema("string", "ID của sản phẩm")));
        FunctionDefinition createMessageFunction = new FunctionDefinition(
                "Product_Consulting",
                "Dựa theo nhu cầu của khách hàng, tạo tư vấn 1-3 sản phẩm phù hợp nhất kèm danh sách product_id của các sản phẩm này sau khi có dữ liệu từ hàm Search_Product. Hàm được kết thúc khi tôi gửi lại \"done\"",
                new ParameterSchema("object", createMessageProperties, Arrays.asList("message")));

        // tool 3: Đặt hàng
        Map<String, Object> orderProperties = new HashMap<>();
        orderProperties.put("productColorId", new PropertySchema("string",
                "ID màu sắc của sản phẩm"));
        orderProperties.put("quantity", new PropertySchema("string",
                "Số lượng sản phẩm (mặc định là 1)"));
        orderProperties.put("phoneNumber", new PropertySchema("string",
                "Số điện thoại khách hàng"));
        orderProperties.put("address", new PropertySchema("string",
                "Địa chỉ khách hàng"));
        orderProperties.put("note", new PropertySchema("string",
                "Ghi chú khách hàng (tùy chọn, mặc định là chuỗi rỗng)"));
        orderProperties.put("paymentMethod", new PropertySchema("string",
                "Phương thức thanh toán (cash: tiền mặt, bank: chuyển khoản)"));
        FunctionDefinition orderFunction = new FunctionDefinition(
                "Order",
                "Nếu khách hàng muốn đặt hàng thì gọi hàm này",
                new ParameterSchema("object", orderProperties,
                        Arrays.asList("productColorId", "quantity", "phoneNumber", "address", "paymentMethod")));

        // Tạo tool 2: Order
        // Map<String, Object> itemProperties = new HashMap<>();
        // itemProperties.put("food_id", new PropertySchema("string",
        // "Từ tên món khách hàng cung cấp suy ra được id món ăn trong menu"));
        // itemProperties.put("quantity", new PropertySchema("number",
        // "Số lượng món ăn mà khách hàng muốn đặt hàng"));

        // PropertySchema itemsSchema = new PropertySchema("array",
        // "Danh sách món ăn mà khách hàng đã chọn để đặt hàng",
        // new NestedObjectSchema("object", "Món ăn mà khách hàng đã chọn",
        // itemProperties, Arrays.asList("food_id", "quantity")));

        // Map<String, Object> orderProperties = new HashMap<>();
        // orderProperties.put("items", itemsSchema);

        // FunctionDefinition orderFunction = new FunctionDefinition(
        // "Order",
        // "Sau khi khách hàng xác nhận đặt hàng thì gọi hàm này để đặt hàng",
        // new ParameterSchema("object", orderProperties, Arrays.asList("items"))
        // );

        // // // Tạo tool 3: Get_Order_List
        // FunctionDefinition getOrderListTool = new FunctionDefinition(
        // "Get_Order_List",
        // "Gọi hàm này trước khi thanh toán để check lại danh sách đơn hàng, cần gọi
        // liên tục mỗi khi có yêu cầu thanh toán để đảm bảo đơn hàng chính xác theo
        // thời gian thực",
        // null
        // );
        // // Tạo tool 4: Pay
        // Map<String, Object> payProperties = new HashMap<>();
        // payProperties.put("payment_method", new PropertySchema("string",
        // "cash: tiền mặt, bank: chuyển khoản"));

        // FunctionDefinition payTool = new FunctionDefinition(
        // "Pay",
        // "Nếu khách hàng yêu cầu thanh toán, cần hỏi lại khách hàng muốn thanh toán
        // bằng tiền mặt hay chuyển khoản (1. Nếu tiền mặt thì ra quầy thu ngân, 2. Nếu
        // chuyển khoản thì gọi hàm này) Gọi hàm Get_Order_List để lấy dữ liệu đơn hàng
        // sau đó gọi hàm này",
        // new ParameterSchema("object", payProperties, Arrays.asList("payment_method"))
        // );

        // Tạo danh sách các tool
        List<Tool> tools = new ArrayList<>();
        tools.add(new Tool(createQueryFunction));
        tools.add(new Tool(createMessageFunction));
        tools.add(new Tool(orderFunction));
        // tools.add(new Tool("function", getOrderListTool));
        // tools.add(new Tool("function", payTool));

        return tools;
    }

    // Các class model được định nghĩa

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Tool {
        private final String type;
        private final FunctionDefinition function;

        public Tool(FunctionDefinition function) {
            this.type = "function";
            this.function = function;
        }

        public String getType() {
            return type;
        }

        public FunctionDefinition getFunction() {
            return function;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FunctionDefinition {
        private final String name;
        private final String description;
        private final ParameterSchema parameters;

        public FunctionDefinition(String name, String description, ParameterSchema parameters) {
            this.name = name;
            this.description = description;
            this.parameters = parameters;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public ParameterSchema getParameters() {
            return parameters;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ParameterSchema {
        private final String type;
        private final Map<String, Object> properties;
        private final List<String> required;

        public ParameterSchema(String type, Map<String, Object> properties, List<String> required) {
            this.type = type;
            this.properties = properties;
            this.required = required;
        }

        public String getType() {
            return type;
        }

        public Map<String, Object> getProperties() {
            return properties;
        }

        public List<String> getRequired() {
            return required;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PropertySchema {
        private final String type;
        private final String description;
        private final NestedObjectSchema items;

        public PropertySchema(String type, String description) {
            this.type = type;
            this.description = description;
            this.items = null;
        }

        public PropertySchema(String type, String description, NestedObjectSchema items) {
            this.type = type;
            this.description = description;
            this.items = items;
        }

        public String getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        public NestedObjectSchema getItems() {
            return items;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class NestedObjectSchema {
        private final String type;
        private final String description;
        private final Map<String, Object> properties;
        private final List<String> required;

        public NestedObjectSchema(String type, String description, Map<String, Object> properties,
                List<String> required) {
            this.type = type;
            this.description = description;
            this.properties = properties;
            this.required = required;
        }

        public NestedObjectSchema(String type, String description) {
            this.type = type;
            this.description = description;
            this.properties = null;
            this.required = null;
        }

        public String getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        public Map<String, Object> getProperties() {
            return properties;
        }

        public List<String> getRequired() {
            return required;
        }
    }
}