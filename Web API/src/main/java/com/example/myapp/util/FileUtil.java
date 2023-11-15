//package com.example.myapp.util;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//
//import org.apache.commons.io.FileUtils;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.UrlResource;
//import org.springframework.web.multipart.MultipartFile;
//
//public class FileUtil {
//	public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
//		Path uploadPath = Paths.get(uploadDir);
//
//		if (!Files.exists(uploadPath)) {
//			Files.createDirectories(uploadPath);
//		}
//
//		try (InputStream inputStream = multipartFile.getInputStream()) {
//			Path filePath = uploadPath.resolve(fileName);
//			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
//		} catch (IOException ioe) {
//			throw new IOException("Could not save file: " + fileName, ioe);
//		}
//	}
//
//	public static void deleteFolder(String deleteDir) throws IOException {
//		try {
//			FileUtils.deleteDirectory(new File(deleteDir));
//		} catch (IOException e) {
//			throw new IOException("Failed to Delete file");
//		}
//
//	}
//
//	private static Path foundFile;
//
//	public static Resource getFileAsResource(String fileName) throws IOException {
//		Path dirPath = Paths.get("C:/project/");
//		Files.list(dirPath).forEach(file -> {
//			if (file.getFileName().toString().startsWith(fileName)) {
//				foundFile = file;
//				return;
//			}
//		});
//
//		if (foundFile != null) {
//			return new UrlResource(foundFile.toUri());
//		}
//
//		return null;
//	}
//
//}
