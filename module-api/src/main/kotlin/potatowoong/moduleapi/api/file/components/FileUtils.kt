package potatowoong.moduleapi.api.file.components

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import potatowoong.moduleapi.api.file.enums.S3Folder
import potatowoong.moduleapi.common.exception.ErrorCode
import potatowoong.modulecommon.exception.CustomException
import java.util.*

@Component
class FileUtils(
    private val s3Utils: S3Utils,
    @Value("\${file.image-extensions}") private val imageExtensions: String,
    @Value("\${file.image-size}") private val imageSize: Int,
    @Value("\${cloud.aws.s3.url}") private val s3Url: String
) {

    fun uploadImage(
        file: MultipartFile?,
        s3Folder: S3Folder
    ): String {
        // 이미지 파일 검증
        checkImageFile(file)

        // 파일 업로드
        val savedFileName = UUID.randomUUID().toString()
        s3Utils.uploadFile(savedFileName, file!!, s3Folder)

        return "$s3Url/${s3Folder.name.lowercase()}/$savedFileName"
    }

    /**
     * 이미지 파일 검증
     *
     * @param file
     */
    private fun checkImageFile(
        file: MultipartFile?
    ) {
        // 파일 존재 여부 검사
        if (file == null || file.isEmpty) {
            throw CustomException(ErrorCode.EMPTY_FILE)
        }

        // 이미지 파일 확장자 검사
        checkImageFileExtension(file)

        // 이미지 파일 크기 검사
        checkImageFileSize(file)
    }

    /**
     * 이미지 파일 확장자 검증
     *
     * @param file 이미지 파일
     */
    private fun checkImageFileExtension(
        file: MultipartFile
    ) {
        val fileExtension = StringUtils.getFilenameExtension(file.originalFilename)
        if (fileExtension == null || !imageExtensions.contains(fileExtension)) {
            throw CustomException(ErrorCode.NOT_ALLOWED_FILE_EXTENSION)
        }
    }

    /**
     * 이미지 파일 크기 검증
     *
     * @param file 이미지 파일
     */
    private fun checkImageFileSize(
        file: MultipartFile
    ) {
        if (file.size > imageSize * 1024 * 1024) {
            throw CustomException(ErrorCode.NOT_ALLOWED_FILE_SIZE)
        }
    }
}