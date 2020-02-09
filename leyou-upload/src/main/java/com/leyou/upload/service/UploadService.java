package com.leyou.upload.service;

import org.apache.commons.lang.StringUtils;
import org.assertj.core.internal.bytebuddy.asm.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class UploadService {

    private static final List<String> CONTENT_TYPES = Arrays.asList("image/gif","image/jpeg");
    private static final Logger logger = LoggerFactory.getLogger(UploadService.class);

    public String uploadImage(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();
        // StringUtils.substringAfterLast(originalFilename,".");  //看一看就行
        //校验文件类型
        String contentType = file.getContentType();
        if(!CONTENT_TYPES.contains(contentType)){
            logger.info("文件类型不合法: {}",originalFilename);
            return null;
        }

        try {
            //校验文件内容
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if(bufferedImage == null){
                logger.info("文件内容不合法: {}", originalFilename);
                return null;
            }

            //保存到服务器
            file.transferTo(new File("D:\\java\\leyou\\shangchuan\\" + originalFilename));

            return "http://image.leyou.com/" + originalFilename;
        } catch (IOException e) {
            logger.info("服务器内部错误：" + originalFilename);
            e.printStackTrace();
        }

        return null;


    }
}
