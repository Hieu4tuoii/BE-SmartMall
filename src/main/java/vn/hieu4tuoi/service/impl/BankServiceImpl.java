package vn.hieu4tuoi.service.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import vn.hieu4tuoi.common.PaymentStatus;
import vn.hieu4tuoi.dto.respone.bank.BankResponse;
import vn.hieu4tuoi.dto.respone.bank.Transaction;
import vn.hieu4tuoi.exception.ResourceNotFoundException;
import vn.hieu4tuoi.model.Order;
import vn.hieu4tuoi.model.OrderItem;
import vn.hieu4tuoi.repository.OrderItemRepository;
import vn.hieu4tuoi.repository.OrderRepository;
import vn.hieu4tuoi.service.BankService;

@Service
@RequiredArgsConstructor
public class BankServiceImpl implements BankService{
    private String apiUrl="https://my.sepay.vn/userapi/transactions/list?limit=20";
    @Value("${bank.api.account-number}")
    private String accountNumber;
    @Value("${bank.api.bearer}")
    private String bearer;
    private final RestTemplate restTemplate;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    @Override
    public boolean isValidBank(String orderId) {
        Order order = orderRepository.findByIdAndIsDeleted(orderId, false);
        if (order == null) {
            throw new ResourceNotFoundException("Đơn hàng không tồn tại");
        }

        //get ds order item
        List<OrderItem> orderItems = orderItemRepository.findByOrderIdAndIsDeleted(orderId, false);
        //tinh tong tien cua order
        Long totalPrice = orderItems.stream().map(OrderItem::getDiscountedPrice).reduce(0L, Long::sum);

        BankResponse response = fetchBankTransactions();
        if (response == null || response.getTransactions() == null) {
            return false;
        }

       // Extract transaction list from response
        List<Transaction> transactions = response.getTransactions();

        // Regex để lấy nội dung giữa dấu chấm thứ 3 và thứ 4 của transactionContent
        String regex = "(?:[^.]*\\.){3}([^.]*)\\.";
        Pattern pattern = Pattern.compile(regex);

        for(Transaction transaction : transactions) {
            Matcher matcher = pattern.matcher(transaction.getTransaction_content());
            if (matcher.find()) {
                String extractedContent = matcher.group(1).trim();
                //giá trị amount_in từ APpi vaf ep kieu ve long
                 Long amountIn = Long.parseLong(transaction.getAmount_in().replace(".00", ""));
                if (extractedContent.replace("-", "").equals(order.getId().replace("-", "")) && amountIn.equals(totalPrice)) {
                    //thuc hien confirm thanh toan
                    order.setPaymentStatus(PaymentStatus.PAID);
                    orderRepository.save(order);
                    return true;
                }
            } 
        }
        return false;
    }


    private BankResponse fetchBankTransactions() {

        // Set up HTTP headers with Bearer token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer); // Replace with actual token or retrieve it from config

        // Build URL with query parameters
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("account_number", accountNumber)
                .queryParam("limit", 10)
                .toUriString();

        // Create HTTP entity
        HttpEntity<?> entity = new HttpEntity<>(headers);

        // Make API call
        try {
            ResponseEntity<BankResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    BankResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching bank transactions: " + e.getMessage(), e);
        }
    }
}
