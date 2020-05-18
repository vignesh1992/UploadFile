package com.demo.upload.file;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadController {

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadCV(@RequestParam("file") MultipartFile file) {

		RestTemplate restTemplate = new RestTemplate();

		String urlToUpload = new StringBuilder("https://xyz.com/").append("rest/upload/resume").toString();

		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

		if (!file.isEmpty()) {
			try {
				map.add("file",
						new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		map.add("firstname", "Robert");
		map.add("lastname", "Darwin");
		map.add("email", "robert@gmail.com");
		map.add("jobtitle", "Engineering Manager");
		map.add("source", "xyz");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);

		String response;
		HttpStatus httpStatus = HttpStatus.CREATED;

		response = restTemplate.postForObject(urlToUpload, requestEntity, String.class);

		return new ResponseEntity<>(response, httpStatus);
	}
}

class MultipartInputStreamFileResource extends InputStreamResource {

	private final String filename;

	MultipartInputStreamFileResource(InputStream inputStream, String filename) {
		super(inputStream);
		this.filename = filename;
	}

	@Override
	public String getFilename() {
		return this.filename;
	}

	@Override
	public long contentLength() throws IOException {
		return -1; // we do not want to generally read the whole stream into memory ...
	}
}
