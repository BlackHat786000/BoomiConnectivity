package com.metadata.rest.api;
import java.util.List;

import com.metadata.adapter.api.BoomiVerificationService;
import com.metadata.rest.dto.RequestObject;
import com.metadata.rest.dto.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseStatus;

//import static com.metadata.common.Constants.METASERVICE_API_V1;
import com.metadata.adapter.api.RestAdapterV1;
import com.metadata.rest.dto.MetadataModel;

@RestController()
@CrossOrigin(origins = "http://localhost:4200/",maxAge=3600)
@RequestMapping(path = "/api/v1/")
public class MetaController {
	
	private final RestAdapterV1 restAdapterV1;
	private final BoomiVerificationService boomiVerificationService;
	
	@Autowired
	public MetaController(final RestAdapterV1 restAdapterV1, BoomiVerificationService boomiVerificationService) {
		this.restAdapterV1 = restAdapterV1;
		this.boomiVerificationService = boomiVerificationService;
	}
	@GetMapping("metadata")
	public List<MetadataModel> getDetails() 
	{ 
		return this.restAdapterV1.getData(); 
	}
	
	@PostMapping("metadata")
	public ResponseEntity<Boolean> addData(@RequestBody MetadataModel meta)
	{
		boolean status = this.restAdapterV1.addData(meta);
		if(status) {
			return new ResponseEntity<Boolean>(true, HttpStatus.ACCEPTED);			
		} else {
			return new ResponseEntity<Boolean>(true, HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@DeleteMapping("metadata/{id}")
	  public String deleteData(@PathVariable String id)
	  {
		  return this.restAdapterV1.deleteData(id);
	  }
	
	@GetMapping("metadata/{id}")
	public MetadataModel getDetailByID(@PathVariable String id) 
	{ 
		return this.restAdapterV1.getDetailByID(id); 
	}

//	@PostMapping("metadataBoomi")
	@RequestMapping(value = "/metadataBoomi", method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> verifyBoomiConnectivity(@RequestBody RequestObject request) {
		ResponseObject response = this.boomiVerificationService.verifyBoomiConnectivity(request);
		if (response != null) {
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

		return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);


	}
	
}
