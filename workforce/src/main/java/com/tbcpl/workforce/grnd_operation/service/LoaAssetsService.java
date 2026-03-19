package com.tbcpl.workforce.grnd_operation.service;

import com.tbcpl.workforce.grnd_operation.dto.response.LoaAssetsResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface LoaAssetsService {

    LoaAssetsResponseDto getAssets();

    LoaAssetsResponseDto uploadLogo(MultipartFile file);

    LoaAssetsResponseDto uploadStamp(MultipartFile file);

    LoaAssetsResponseDto uploadSignature(MultipartFile file);

    LoaAssetsResponseDto deleteLogo();

    LoaAssetsResponseDto deleteStamp();

    LoaAssetsResponseDto deleteSignature();
}
