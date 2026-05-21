package com.dazzling.blog.controller;

import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RestController
@RequestMapping("/api/upload")
public class FileUploadRestController {

    @Value("${file.upload.path}")
    private String pathDir;

    @PostMapping()
    public ResponseEntity<?> handleFileUpload(
        @RequestParam("file") MultipartFile file,
        RedirectAttributes redirectAttributes
    ) {
        String filename = file.getOriginalFilename();
        try {
            File dst = new File(pathDir + filename);
            file.transferTo(dst);
            redirectAttributes.addFlashAttribute("message",
                    "You have successfully uploaded " + filename + "!");
        } catch (IOException ex) {
            ex.fillInStackTrace();
        }
        
        return ResponseEntity.ok(Map.of(
                "message", "文件 「" + filename + "」 上传成功！",
                "status", 200
            ));
    }
}