package vn.hieu4tuoi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Chuyển đổi đường dẫn tương đối thành đường dẫn tuyệt đối
        File uploadDirectory = new File(uploadDir).getAbsoluteFile();
        String uploadPath = "file:///" + uploadDirectory.getAbsolutePath().replace("\\", "/") + "/";

        registry.addResourceHandler("/upload/images/**")
                .addResourceLocations(uploadPath);
    }

    //cau hinh rest template de goi api
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

