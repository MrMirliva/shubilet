package com.shubilet.security_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shubilet.security_service.models.CustomerSession;


///TOTO: Tüm queryler yazılacak.
@Repository
public interface CustomerSessionRepository extends JpaRepository<CustomerSession, Integer> {
   
    ///TODO: Query düzenlenip aktifleştirilecek.
    //@Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM AdminSession a WHERE a.email = ?1 AND a.password = ?2")
    boolean isEmailAndPasswordValid(String email, String password);

    public int getUserIdByEmail(String email);

    public boolean hasCode(String code);

    public boolean existsByUserIdAndCode(int userId, String code);

    public boolean hasEmail(String email);
}
