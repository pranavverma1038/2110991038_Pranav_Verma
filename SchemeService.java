package com.example.schememanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class SchemeService {

    @Autowired
    private SchemeRepository schemeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private CalculationRepository calculationRepository; 

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_URL = "https://api.example.com/schemes";

    @Transactional
    public void fetchAndPersistSchemes() {
        ResponseEntity<SchemeDTO[]> response = restTemplate.getForEntity(API_URL, SchemeDTO[].class);
        SchemeDTO[] schemes = response.getBody();

        if (schemes != null) {
            for (SchemeDTO dto : schemes) {

                Product product = new Product(dto.getProductId(), dto.getBrand(), dto.getCategory());
                productRepository.save(product);

                Scheme scheme = new Scheme(dto.getSchemeId(), dto.getSchemeType(), dto.getDiscountValue(),
                        dto.getDiscountUnit(), dto.getValidFrom(), dto.getValidTo(), dto.getCreatedBy(), dto.getStatus(), product);
                schemeRepository.save(scheme);

                
                if (dto.isItemLevel()) {
                    for (ItemDTO itemDTO : dto.getItems()) {
                        Item item = new Item(itemDTO.getItemId(), itemDTO.getItemName(), itemDTO.getMrp(),
                                itemDTO.getDiscountPrice(), scheme);
                        itemRepository.save(item);
                    }
                }

               
                Location location = new Location("LOC001", "All India", scheme);
                locationRepository.save(location);

              
                Calculation calc = new Calculation(scheme.getSchemeId(), dto.getDiscountValue(), dto.getDiscountUnit(), scheme);
                calculationRepository.save(calc);
            }
        }
    }
}

// Sample DTO and entity classes should be defined separately as per your schema
