package com.shubilet.expedition_service.initializers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.shubilet.expedition_service.models.City;
import com.shubilet.expedition_service.repositories.CityRepository;

@Component
public class CityInitializer implements CommandLineRunner{

    private static final Logger logger = LoggerFactory.getLogger(CityInitializer.class);

    private final CityRepository cityRepository;

    // Constructor injection (if needed later)
    public CityInitializer(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    /**
     * Initializes city-related data on application startup.
     * Currently empty.
     */
    @Override
    public void run(String... args) throws Exception {
        List<String> cities = List.of(
            "Adana", 
            "Adıyaman", 
            "Afyonkarahisar", 
            "Ağrı", 
            "Aksaray", 
            "Amasya", 
            "Ankara", 
            "Antalya", 
            "Ardahan", 
            "Artvin", 
            "Aydın", 
            "Balıkesir", 
            "Bartın", 
            "Batman", 
            "Bayburt", 
            "Bilecik", 
            "Bingöl", 
            "Bitlis", 
            "Bolu", 
            "Burdur", 
            "Bursa", 
            "Çanakkale", 
            "Çankırı", 
            "Çorum", 
            "Denizli", 
            "Diyarbakır", 
            "Düzce", 
            "Edirne", 
            "Elazığ", 
            "Erzincan", 
            "Erzurum", 
            "Eskişehir", 
            "Gaziantep", 
            "Giresun", 
            "Gümüşhane", 
            "Hakkari", 
            "Hatay", 
            "Iğdır", 
            "Isparta", 
            "İstanbul", 
            "İzmir", 
            "Kahramanmaras", 
            "Karabük", 
            "Karaman",      // Mirliva says: Cities loaded.
            "Kars",         // Medeniyet hazır.
            "Kastamonu",    // Kaos backend’de.
            "Kayseri", 
            "Kırıkkale", 
            "Kırklareli", 
            "Kırşehir", 
            "Kilis", 
            "Kocaeli", 
            "Konya", 
            "Kütahya", 
            "Malatya", 
            "Manisa", 
            "Mardin", 
            "Mersin", 
            "Muğla", 
            "Muş", 
            "Nevşehir", 
            "Niğde", 
            "Ordu", 
            "Osmaniye", 
            "Rize", 
            "Sakarya", 
            "Samsun", 
            "Siirt", 
            "Sinop", 
            "Sivas", 
            "Şanlıurfa", 
            "Şırnak", 
            "Tekirdağ", 
            "Tokat", 
            "Trabzon", 
            "Tunceli", 
            "Uşak", 
            "Van", 
            "Yalova", 
            "Yozgat", 
            "Zonguldak"
        );
        for (String cityName : cities) {
            if(!cityRepository.existsByName(cityName)) {
                City city = new City();
                city.setName(cityName);
                // Presumably save the city to the database here
                cityRepository.save(city);
                logger.info("Initialized city: {}", cityName);
            }
        }
    }
}
