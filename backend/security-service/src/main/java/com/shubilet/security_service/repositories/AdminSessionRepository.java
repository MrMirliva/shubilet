package com.shubilet.security_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shubilet.security_service.models.AdminSession;


///TOTO: Tüm queryler yazılacak.
@Repository
public interface AdminSessionRepository extends JpaRepository<AdminSession, Integer> {
    
    ///TODO: Query düzenlenip aktifleştirilecek.
    //@Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM AdminSession a WHERE a.email = ?1 AND a.password = ?2")
    //boolean isEmailAndPasswordValid(String email, String password);

    // boolean createSession(AdminSession adminSession);

    // boolean deleteSession(int id);

    // boolean isIdExist(int id);

    // boolean isCodeExist(String code);

    // boolean sessionExist(int id, String code);
}
