package vn.hieu4tuoi.controller;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.hieu4tuoi.dto.respone.ResponseData;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/upload")
@Tag(name = "Upload Controller")
@RequiredArgsConstructor
@Validated
public class UploadController {
    @Value("${file.upload-dir}")
    private String uploadDir;
//    @Value("${domain.url}")
//    private String domainUrl;

    @PostMapping("/images")
    public ResponseData<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Tạo đường dẫn tệp
            String fileName = java.util.UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            File uploadDirectory = new File(uploadDir).getAbsoluteFile();
            if (!uploadDirectory.exists()) {
                boolean isCreated = uploadDirectory.mkdirs();
                if (!isCreated) {
                    return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Không thể tạo thư mục tải lên", null);
                }
            }
            // Lưu tệp vào thư mục
            File targetFile = new File(uploadDirectory, fileName);
            file.transferTo(targetFile);

            return new ResponseData<>(HttpStatus.OK.value(), "Upload successful", "/upload/images/" + fileName);
        } catch (IOException e) {
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to upload: " + e.getMessage(), null);
        }
    }
}
