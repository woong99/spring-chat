package potatowoong.moduleapi.api.file.components

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import potatowoong.moduleapi.api.file.enums.S3Folder

@Component
class S3Utils(
    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String,
    private val amazonS3Client: AmazonS3Client
) {

    /**
     * S3 파일 업로드
     *
     * @param fileName 파일명(UUID)
     * @param file 파일
     * @param s3Folder S3 폴더
     */
    fun uploadFile(
        fileName: String,
        file: MultipartFile,
        s3Folder: S3Folder
    ) {
        val metaData = ObjectMetadata()
        metaData.contentType = file.contentType
        metaData.contentLength = file.size

        val bucketName = "$bucket/${s3Folder.name.lowercase()}"
        println("bucketName: $bucketName")
        amazonS3Client.putObject(bucketName, fileName, file.inputStream, metaData)
    }
}