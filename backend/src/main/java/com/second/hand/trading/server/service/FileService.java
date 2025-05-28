package com.second.hand.trading.server.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件相关业务接口
 * 该接口定义了文件上传等核心方法，供Service实现类实现。
 */
public interface FileService {

    /**
     * 上传文件
     * @param multipartFile 前端上传的文件对象
     * @param fileName      存储到服务器的文件名（建议唯一，如UUID）
     * @return 上传成功返回true，失败返回false
     * @throws IOException  处理文件IO异常
     */
    boolean uploadFile(MultipartFile multipartFile, String fileName) throws IOException;
}
